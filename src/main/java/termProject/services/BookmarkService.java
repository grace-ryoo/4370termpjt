package termProject.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import termProject.models.Category;
import termProject.models.Recipe;
import termProject.models.User;

@Service
public class BookmarkService {
    @Autowired
    private DataSource dataSource;

    public BookmarkService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Recipe> getBookmarkedRecipesByType(String userId, String type) {
        List<Recipe> recipes = new ArrayList<>();

        final String sql =  "SELECT r.*, u.firstName, u.lastName, c.categoryId, c.categoryName, c.categoryImageUrl, " +
            "COALESCE(AVG(rt.stars), 0) AS averageRating, " +
            "COUNT(rt.userId) AS countRatings " +
            "FROM bookmarks b " +
            "JOIN recipes r ON b.recipe_id = r.recipe_id " +
            "JOIN users u ON r.user_id = u.user_id " +
            "JOIN categories c ON r.category_id = c.category_id " +
            "LEFT JOIN rating rt ON r.recipe_id = rt.recipe_id " +
            "WHERE b.user_id = ? AND b.bookmark_type = ? " +
            "GROUP BY r.recipe_id, u.user_id, u.firstName, u.lastName, c.categoryId, c.categoryName, c.categoryImageUrl";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId); // For is_hearted check
            pstmt.setString(2, userId); // For user's bookmarks

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                        rs.getString("userId"), 
                        rs.getString("firstName"),
                        rs.getString("lastName")
                    );

                    Category category = new Category(
                        rs.getString("categoryId"),
                        rs.getString("categoryName"),
                        rs.getString("categoryImageUrl")
                    );

                    int avgRating = (int) Math.round(rs.getDouble("averageRating"));
                    int numRatings = rs.getInt("countRatings");

                    Recipe recipe = new Recipe(
                        rs.getString("recipeId"),
                        rs.getString("recipeName"),
                        rs.getString("description"),
                        convertUTCtoEST(rs.getString("recipeCreateDate")),
                        user,
                        category,
                        rs.getInt("prep_time"),
                        rs.getInt("cook_time"),
                        rs.getInt("servings"),
                        rs.getString("cuisineId"),
                        rs.getString("dietId"),
                        rs.getString("cookingLevel"),
                        new ArrayList<>(List.of(rs.getString("ingredients").split(","))),
                        avgRating,
                        numRatings
                    );
                    recipes.add(recipe);
                    
                }
            }
        } catch(SQLException e) {
            throw new RuntimeException("Error fetching posts", e);
        }

        return recipes;
    }
    
    public List<Recipe> getPastRecipes(String userId) {
        return getBookmarkedRecipesByType(userId, "PAST");
    }

    public List<Recipe> getFutureRecipes(String userId) {
        return getBookmarkedRecipesByType(userId, "FUTURE");
    }

    private String convertUTCtoEST(String utcTimestamp) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a");
        LocalDateTime utcDateTime = LocalDateTime.parse(utcTimestamp, inputFormatter);
        ZonedDateTime utcZoned = utcDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime estZoned = utcZoned.withZoneSameInstant(ZoneId.of("America/New_York"));

        return estZoned.format(outputFormatter);
    }
}
