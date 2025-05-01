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
import java.util.ArrayList;
import java.util.List;

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

    /**
     * NEED UPDATE
     */
    /**
     * public void addReview() {
     *
     *      *}
     */
    public String createRecipe(String recipeName,
            String description,
            String userId,
            List<String> ingredients,
            int prepTime,
            int cookTime,
            int servings,
            String category,
            String dietId,
            String cookLevel) {
        if (userId == null || userId.isEmpty()) {
            return "-1";
        }

        if (description == null || description.trim().isEmpty()) {
            return "-1";
        }

        final String sql = "INSERT INTO recipe (recipeName, description, userId, prep_time, cook_time, servings, categoryId, dietId, cookingLevel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, recipeName);
            pstmt.setString(2, description);
            pstmt.setString(3, userId);
            pstmt.setInt(4, prepTime);
            pstmt.setInt(5, cookTime);
            pstmt.setInt(6, servings);
            pstmt.setString(7, category);
            pstmt.setString(8, dietId);
            pstmt.setString(9, cookLevel);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    String recipeId = rs.getString(1);
                    if (ingredients != null && !ingredients.isEmpty()) {
                        saveIngredients(conn, recipeId, ingredients);
                    }
                    return recipeId;
                }
            }
            return "-1";
        } catch (SQLException e) {
            System.err.println("Error creating recipe: " + e.getMessage());
            return "-1";
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

        String sql = "SELECT r.*, u.firstName, u.lastName, c.categoryId, c.categoryName, c.categoryImageUrl, "
                + "COALESCE(AVG(rt.stars), 0) AS averageRating, "
                + "COUNT(rt.userId) AS countRatings "
                + "FROM recipe r "
                + "JOIN user u ON r.userId = u.userId "
                + "JOIN category c ON r.categoryId = c.categoryId "
                + "LEFT JOIN rating rt ON r.recipeId = rt.recipeId"
                + "WHERE r.recipeId = ? "
                + "GROUP BY r.recipeId, u.userId, u.firstName, u.lastName, c.categoryId, c.categoryName, c.categoryImageUrl\";";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recipeId);
            stmt.setString(2, userId);
            ResultSet rs = stmt.executeQuery();
            System.out.println(" Fetching post for recipeId: " + recipeId + " using userId: " + userId);
            if (rs.next()) {
                User user = new User(rs.getString("userId"), rs.getString("userName"),rs.getString("firstName"), rs.getString("lastName"));

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
                        getIngredientsForRecipe(recipeId),
                        rs.getInt("averageRating"),
                        rs.getInt("countRatings")
                //avgRating,
                //numRatings
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
                rs.getString("userName"),
                rs.getString("firstName"),
                rs.getString("lastName"));

        Category category = new Category(
                rs.getString("categoryId"),
                rs.getString("categoryName"),
                rs.getString("categoryImageUrl"));

        String recipeId = rs.getString("recipeId");
        List<String> ingredients = getIngredientsForRecipe(recipeId);

        int avgRating = (int) Math.round(rs.getDouble("averageRating"));
        int numRatings = rs.getInt("countRatings");

        return new Recipe(
                recipeId,
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
                ingredients,
                avgRating,
                numRatings); 
    }

    public List<String> getIngredientsForRecipe(String recipeId) throws SQLException {
        List<String> ingredients = new ArrayList<>();
        final String sql = "SELECT ingredientName FROM recipe_ingredients WHERE recipeId = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recipeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ingredients.add(rs.getString("ingredientName"));
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

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        final String sql = "SELECT r.*, u.*, c.*, " +
                          "COALESCE(AVG(rt.stars), 0) AS averageRating, " +
                          "COUNT(rt.userId) AS countRatings " +
                          "FROM recipe r " +
                          "JOIN user u ON r.userId = u.userId " +
                          "JOIN category c ON r.categoryId = c.categoryId " +
                          "LEFT JOIN rating rt ON r.recipeId = rt.recipeId " +
                          "GROUP BY r.recipeId";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                recipes.add(mapRecipeFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching recipes", e);
        }
        return recipes;
    }

    

    public List<Recipe> getRecipesByCategory(String categoryId) {
        List<Recipe> recipes = new ArrayList<>();
        final String sql = "SELECT r.*, u.*, c.* FROM recipe r " +
                "JOIN user u ON r.userId = u.userId " +
                "JOIN category c ON r.categoryId = c.categoryId " +
                "WHERE r.categoryId = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                recipes.add(mapRecipeFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching recipes by category", e);
        }
        return recipes;
    }
    
    public List<Recipe> getTrendingRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        final String sql = "SELECT r.*, u.*, c.*, " +
                "COALESCE(AVG(rt.stars), 0) AS averageRating, " +
                "COUNT(rt.userId) AS countRatings " +
                "FROM recipe r " +
                "JOIN user u ON r.userId = u.userId " +
                "JOIN category c ON r.categoryId = c.categoryId " +
                "LEFT JOIN rating rt ON r.recipeId = rt.recipeId " +
                "GROUP BY r.recipeId ORDER BY COUNT(rt.userId) DESC LIMIT 10";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                recipes.add(mapRecipeFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching trending recipes", e);
        }
        return recipes;
    }
    
}
