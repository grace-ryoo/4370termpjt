package termProject.services;

import java.sql.*;
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

    public void bookmark(String userId, String recipeId) {
        final String sql = "INSERT INTO bookmark (userId, postId) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, postId);
            pstmt.executeUpdate();
            System.out.println("Success: bookmarked post");
        } catch (SQLException e) {
            throw new RuntimeException("Error bookmarking post: ", e);
        }
    }

    public void unBookmark(String userId, String postId) {
        final String sql = "DELETE from bookmark WHERE userId = ? AND postId = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, postId);
            pstmt.executeUpdate();
            System.out.println("Success: bookmarked post");
        } catch (SQLException e) {
            throw new RuntimeException("Error bookmarking post: ", e);
        }
    }

    public void addReview() {

    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        final String sql = "SELECT * FROM categories";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Category category = new Category(
                        rs.getString("categoryId"),
                        rs.getString("categoryName"));
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching categories", e);
        }
        return categories;
    }

    public List<Recipe> getTrendingRecipes() {
        List<Recipe> trendingRecipes = new ArrayList<>();
        final String sql = "SELECT r.*, u.*, c.*, " +
                "(SELECT COUNT(*) FROM likes l WHERE l.recipeId = r.recipeId) as likeCount " +
                "FROM recipes r " +
                "JOIN users u ON r.userId = u.userId " +
                "JOIN categories c ON r.categoryId = c.categoryId " +
                "ORDER BY likeCount DESC, r.created_at DESC LIMIT 10";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                trendingRecipes.add(mapRecipeFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching trending recipes", e);
        }
        return trendingRecipes;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        final String sql = "SELECT r.*, u.*, c.* FROM recipes r " +
                "JOIN users u ON r.userId = u.userId " +
                "JOIN categories c ON r.categoryId = c.categoryId " +
                "ORDER BY r.created_at DESC";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                recipes.add(mapRecipeFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all recipes", e);
        }
        return recipes;
    }

    public boolean createRecipe(String recipeName, String description, String userId,
            String categoryId, List<String> ingredients,
            int prepTime, int cookTime, int servings) {
        if (userId == null || userId.isEmpty()) {
            throw new RuntimeException("User ID is required to create a recipe.");
        }

        final String sql = "INSERT INTO recipes (recipeName, description, userId, categoryId, " +
                "prep_time, cook_time, servings) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, recipeName);
                pstmt.setString(2, description);
                pstmt.setString(3, userId);
                pstmt.setString(4, categoryId);
                pstmt.setInt(5, prepTime);
                pstmt.setInt(6, cookTime);
                pstmt.setInt(7, servings);

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
                        rs.getString("categoryName"));

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
                        rs.g);
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
