package termProject.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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

    public void bookmark(String userId, String recipeId) {
        final String sql = "INSERT INTO bookmark (userId, recipeId) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, recipeId);
            pstmt.executeUpdate();
            System.out.println("Success: bookmarked post");
        } catch (SQLException e) {
            throw new RuntimeException("Error bookmarking post: ", e);
        }
    }

    public void unBookmark(String userId, String recipeId) {
        final String sql = "DELETE from bookmark WHERE userId = ? AND recipeId = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, recipeId);
            pstmt.executeUpdate();
            System.out.println("Success: bookmarked post");
        } catch (SQLException e) {
            throw new RuntimeException("Error bookmarking post: ", e);
        }
    }

    public void addReview() {
       
    }

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
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating recipe", e);
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

                List<String> ingredientItems = rs.getArray("ingredientItems") != null ? Arrays.asList((String[]) rs.getArray("ingredientItems").getArray()) : new ArrayList<>();
                List<String> ingredients = new ArrayList<>();

                for (String ingredient : ingredientItems) {
                    ingredients.add(ingredient);  // Add each item from ingredientItems to ingredients
                }

                Category category = new Category(
                    rs.getString("categoryId"),
                    rs.getString("categoryName"),
                    rs.getString("categoryImageUrl")
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
                    rs.getInt("servings"),
                    ingredients,
                    rs.getString("cuisineId"),
                    rs.getString("dietId"),
                    rs.getString("cookingLevel")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching recipes by ID", e);
        }

        return recipe;
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
