package termProject.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import termProject.models.*;

@Service
public class RecipeService {
    @Autowired
    private DataSource dataSource;
    private UserService userService;
    private ReviewService reviewService;

    public RecipeService(DataSource dataSource, UserService userService, ReviewService reviewService) {
        this.dataSource = dataSource;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    public void bookmark(String userId, String recipeId, String bookmark_type) {
        final String sql = "INSERT INTO bookmark (userId, recipeId, bookmark_type) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, recipeId);
            pstmt.setString(3, bookmark_type);
            pstmt.executeUpdate();
            System.out.println("Success: bookmarked recipe as " + bookmark_type);
        } catch (SQLException e) {
            throw new RuntimeException("Error bookmarking recipe: ", e);
        }
    }

    public void unBookmark(String userId, String recipeId, String bookmark_type) {
        final String sql = "DELETE from bookmark WHERE userId = ? AND recipeId = ? AND bookmark_type = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, recipeId);
            pstmt.setString(3, bookmark_type);
            pstmt.executeUpdate();
            System.out.println("Success: unbookmarked recipe");
        } catch (SQLException e) {
            throw new RuntimeException("Error unbookmarking recipe: ", e);
        }
    }

    /** NEED UPDATE */
    /**
    public void addReview() {

    }
    */

    public boolean createRecipe(String recipeName, String description, String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new RuntimeException("User ID is required to create a recipe.");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new RuntimeException("Recipe content cannot be empty.");
        }

        final String sql = "INSERT INTO review (recipeName, description, userId) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, recipeName);
            pstmt.setString(2, description);
            pstmt.setString(3, userId);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0 && ingredients != null && !ingredients.isEmpty()) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        String recipeId = rs.getString(1);
                        saveIngredients(conn, recipeId, ingredients);
                    }
                }

                conn.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating recipe", e);
        }
    }

    private void saveIngredients(Connection conn, String recipeId, List<String> ingredients)
            throws SQLException {
        final String sql = "INSERT INTO recipe_ingredients (recipeId, ingredient) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String ingredient : ingredients) {
                pstmt.setString(1, recipeId);
                pstmt.setString(2, ingredient);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public Recipe getRecipeById(String recipeId, String userId) {
        Recipe recipe = null;

        String sql = "";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recipeId);
            stmt.setString(2, userId);
            ResultSet rs = stmt.executeQuery();
            System.out.println(" Fetching post for recipeId: " + recipeId + " using userId: " + userId);
            if (rs.next()) {
                User user = new User(rs.getString("userId"), rs.getString("firstName"), rs.getString("lastName"));

                // Format the date
                String formattedDate = "Never";
                Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy, hh:mm a");
                    formattedDate = sdf.format(timestamp);
                }

                Category category = new Category(
                    rs.getString("categoryId"),
                    rs.getString("categoryName")
                );

                recipe = new Recipe(
                    rs.getString("recipeId"),
                    rs.getString("recipeName"),
                    rs.getString("description"),
                    convertUTCtoEST(rs.getString("recipeCreateDate")),
                    user,
                    category,
                    rs.getInt("prep_time"),
                    rs.getInt("cook_time"),
                    rs.getInt("servings")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching recipes by ID", e);
        }

        return recipe;
    }

    private Recipe mapRecipeFromResultSet(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("userId"),
                rs.getString("firstName"),
                rs.getString("lastName"));

        Category category = new Category(
                rs.getString("categoryId"),
                rs.getString("categoryName"));

        String recipeId = rs.getString("recipeId");
        List<String> ingredients = getIngredientsForRecipe(recipeId);

        return new Recipe(
                recipeId,
                rs.getString("recipeName"),
                rs.getString("description"),
                convertUTCtoEST(rs.getString("created_at")),
                user,
                category,
                rs.getInt("prep_time"),
                rs.getInt("cook_time"),
                rs.getInt("servings"),
                ingredients); // Add ingredients to constructor
    }

    private List<String> getIngredientsForRecipe(String recipeId) throws SQLException {
        List<String> ingredients = new ArrayList<>();
        final String sql = "SELECT ingredient FROM recipe_ingredients WHERE recipeId = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recipeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ingredients.add(rs.getString("ingredient"));
            }
        }
        return ingredients;
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
