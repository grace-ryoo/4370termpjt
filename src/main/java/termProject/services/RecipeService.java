package termProject.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import termProject.models.Category;
import termProject.models.Recipe;
import termProject.models.User;

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

    public boolean createRecipe(String recipeName, String description,
    String userId, String categoryId, int prep_time, int cook_time, int servings, String cuisineId, String dietId, String cookingLevel) {
        if (userId == null || userId.isEmpty()) {
            throw new RuntimeException("User ID is required to create a recipe.");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new RuntimeException("Recipe content cannot be empty.");
        }

        if (recipeName == null || recipeName.trim().isEmpty()) {
            throw new RuntimeException("Recipe name cannot be empty.");
        }

        if (categoryId == null || categoryId.isEmpty()) {
            throw new RuntimeException("CategoryId cannot be empty.");
        }

        if (prep_time <= 0) {
            throw new RuntimeException("Preparation time cannot be less than or equal to 0.");
        }

        if (cook_time <= 0) {
            throw new RuntimeException("Cooking time cannot be less than or equal to 0.");
        }

        if (servings <= 0) {
            throw new RuntimeException("Servings cannot be less than or equal to 0.");
        }

        if (cuisineId == null || cuisineId.isEmpty()) {
            throw new RuntimeException("CuisineId cannot be empty.");
        }

        if (dietId == null || dietId.isEmpty()) {
            throw new RuntimeException("DietId cannot be empty.");
        }

        if (cookingLevel == null || cookingLevel.trim().isEmpty()) {
            throw new RuntimeException("Cooking level cannot be empty.");
        }

        final String sql = "INSERT INTO recipe (recipeName, description, userId, categoryId, prep_time, cook_time, servings, cuisineId, dietId, cookingLevel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, recipeName);
            pstmt.setString(2, description);
            pstmt.setString(3, userId);
            pstmt.setString(4, categoryId);
            pstmt.setInt(5, prep_time);
            pstmt.setInt(6, cook_time);
            pstmt.setInt(7, servings);
            pstmt.setString(8, cuisineId);
            pstmt.setString(9, dietId);
            pstmt.setString(10, cookingLevel);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating recipe", e);
        }
    }

    public Recipe getRecipeById(String recipeId, String userId) {
        Recipe recipe = null;

        String sql = "SELECT r.*, u.firstName, u.lastName, c.categoryId, c.categoryName, c.categoryImageUrl, " +
            "COALESCE(AVG(rt.stars), 0) AS averageRating, " +
            "COUNT(rt.userId) AS countRatings " +
            "FROM recipe r " + 
            "JOIN user u ON r.userId = u.userId " +
            "JOIN category c ON r.categoryId = c.categoryId " +
            "LEFT JOIN rating rt ON r.recipeId = rt.recipeId" +
            "WHERE r.recipeId = ? " +
            "GROUP BY r.recipeId, u.userId, u.firstName, u.lastName, c.categoryId, c.categoryName, c.categoryImageUrl\";";

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
                    rs.getString("categoryName"),
                    rs.getString("categoryImageUrl")
                );

                int avgRating = (int) Math.round(rs.getDouble("averageRating"));
                int numRatings = rs.getInt("countRatings");

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
                    rs.getString("cuisineId"),
                    rs.getString("dietId"),
                    rs.getString("cookingLevel"),
                    avgRating,
                    numRatings
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


    public void addRating(String userId, String recipeId) {
        final String sql = "INSERT INTO rating (userId, postId) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, recipeId);
            pstmt.executeUpdate();
            System.out.println("Success: rated recipe");

        } catch (SQLException e) {
            throw new RuntimeException("Error rating recipe: ", e);
        }
    }

    public void undoRating(String userId, String recipeId) {
        final String sql = "DELETE FROM rating WHERE userId = ? AND recipeId = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, recipeId);
            pstmt.executeUpdate();
            System.out.println("Success: undid rating for recipe");

        } catch (SQLException e) {
            throw new RuntimeException("Error undoing recipe: ", e);
        }
    }

    



}
