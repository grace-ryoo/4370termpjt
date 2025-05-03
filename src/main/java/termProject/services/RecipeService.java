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
import org.springframework.jdbc.core.JdbcTemplate;
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
    private JdbcTemplate jdbcTemplate;

    public RecipeService(DataSource dataSource, UserService userService, ReviewService reviewService,
            JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.userService = userService;
        this.reviewService = reviewService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void bookmark(String userId, String recipeId, String bookmark_type) {
        final String sql = "INSERT INTO bookmark (userId, recipeId, bookmark_type) " +
            "VALUES (?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE bookmark_type = VALUES(bookmark_type)";

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

    public void unBookmark(String userId, String recipeId) {
        final String sql = "DELETE from bookmark WHERE userId = ? AND recipeId = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, recipeId);
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
     * public void addReview()
     *
     *
     */
    
    public String createRecipe(String recipeName,
            String description,
            String userId,
            List<String> ingredients,
            List<String> amounts,
            List<String> units,
            int prepTime,
            int cookTime,
            int servings,
            String category,
            String dietId,
            String cookLevel,
            int cuisineId,
            String imageUrl) {

        if (userId == null || userId.isEmpty()) {
            return "-1";
        }

        if (description == null || description.trim().isEmpty()) {
            return "-1";
        }

        final String sql = "INSERT INTO recipe (recipeName, description, userId, prep_time, cook_time, " +
                "servings, categoryId, dietId, cookingLevel, cuisineId, imageUrl) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            pstmt.setInt(10, cuisineId);
            pstmt.setString(11, imageUrl);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    String recipeId = rs.getString(1);
                    if (ingredients != null && !ingredients.isEmpty()) {
                        saveIngredients(conn, recipeId, ingredients, amounts, units);
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

    private void saveIngredients(Connection conn, String recipeId,
            List<String> ingredients, List<String> amounts, List<String> units)
            throws SQLException {
        final String sql = "INSERT INTO recipe_ingredients (recipeId, ingredientName, ingredientAmount, ingredientUnit) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < ingredients.size(); i++) {
                pstmt.setString(1, recipeId);
                pstmt.setString(2, ingredients.get(i));
                pstmt.setString(3, amounts.get(i));
                pstmt.setString(4, units.get(i));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public Recipe getRecipeById(String recipeId, String userId) {
        final String sql = "SELECT r.*, u.*, c.*, " +
                "COALESCE(AVG(rt.stars), 0) as averageRating, " +
                "COUNT(rt.userId) as countRatings " +
                "FROM recipe r " +
                "JOIN user u ON r.userId = u.userId " +
                "JOIN category c ON r.categoryId = c.categoryId " +
                "LEFT JOIN rating rt ON r.recipeId = rt.recipeId " +
                "WHERE r.recipeId = ? " +
                "GROUP BY r.recipeId, u.userId, c.categoryId";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, recipeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Recipe recipe = mapRecipeFromResultSet(rs);
                recipe.setIngredients(getIngredientsForRecipe(recipeId));

                // Debug output
                System.out.println("Recipe ID: " + recipe.getRecipeId());
                System.out.println("Recipe Name: " + recipe.getRecipeName());
                System.out.println("Stars: " + recipe.getStars());
                System.out.println("Rating Count: " + recipe.getRatingCount());

                return recipe;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching recipe: ", e);
        }
    }

    public Recipe mapRecipeFromResultSet(ResultSet rs) throws SQLException {
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

        Recipe recipe = new Recipe(
                recipeId,
                rs.getString("recipeName"),
                rs.getString("description"),
                rs.getString("userId"),
                rs.getString("categoryId"),
                rs.getString("dietId"),
                rs.getInt("prep_time"),
                rs.getInt("cook_time"),
                rs.getInt("servings"),
                rs.getString("cookingLevel"),
                rs.getInt("cuisineId"),
                ingredients,
                rs.getString("imageUrl"));

        // Set image URL
        String imageUrl = rs.getString("imageUrl");
        if (imageUrl != null && !imageUrl.startsWith("/uploads/")) {
            imageUrl = "/uploads/" + imageUrl;
        }
        recipe.setImageUrl(imageUrl);

        // Calculate and set the star rating properties
        double avgRating = rs.getDouble("averageRating");
        StringBuilder stars = new StringBuilder();
        int filledStars = (int) Math.round(avgRating);
        for (int i = 0; i < 5; i++) {
            stars.append(i < filledStars ? "★" : "☆");
        }
        recipe.setProperty("stars", stars.toString());
        recipe.setProperty("ratingCount", String.valueOf(rs.getInt("countRatings")));

        return recipe;
    }

    public List<String> getIngredientsForRecipe(String recipeId) throws SQLException {
        List<String> ingredients = new ArrayList<>();
        final String sql = "SELECT ingredientName, ingredientAmount, ingredientUnit FROM recipe_ingredients WHERE recipeId = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recipeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String ingredient = String.format("%s %s %s",
                        rs.getString("ingredientAmount"),
                        rs.getString("ingredientUnit"),
                        rs.getString("ingredientName"));
                ingredients.add(ingredient);
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

    public void addRating(String userId, String recipeId, int stars) {
        final String sql = "INSERT INTO rating (userId, recipeId, stars) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE stars = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, recipeId);
            pstmt.setInt(3, stars);
            pstmt.setInt(4, stars); // For update case
            pstmt.executeUpdate();
            System.out.println("Success: rated recipe with " + stars + " stars");
        } catch (SQLException e) {
            throw new RuntimeException("Error rating recipe: ", e);
        }
    }

    public Integer getUserRating(String userId, String recipeId) {
        final String sql = "SELECT stars FROM rating WHERE userId = ? AND recipeId = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, recipeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("stars");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting user rating: ", e);
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

    public boolean hasUserRated(String userId, String recipeId) {
        final String sql = "SELECT COUNT(*) FROM rating WHERE userId = ? AND recipeId = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, recipeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user rating: ", e);
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
                "GROUP BY r.recipeId ";
        // "ORDER BY r.recipeCreateDate DESC";

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

    public List<Recipe> getFilteredRecipes(String categoryId, Integer dietId, Integer cuisineId, String cookingLevel) {
        List<Recipe> recipes = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, u.*, c.*, " +
                        "COALESCE(AVG(rt.stars), 0) AS averageRating, " +
                        "COUNT(rt.userId) AS countRatings " +
                        "FROM recipe r " +
                        "JOIN user u ON r.userId = u.userId " +
                        "JOIN category c ON r.categoryId = c.categoryId " +
                        "LEFT JOIN rating rt ON r.recipeId = rt.recipeId " +
                        "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (categoryId != null && !categoryId.isEmpty()) {
            sql.append("AND r.categoryId = ? ");
            params.add(categoryId);
        }
        if (dietId != null) {
            sql.append("AND r.dietId = ? ");
            params.add(dietId);
        }
        if (cuisineId != null) {
            sql.append("AND r.cuisineId = ? ");
            params.add(cuisineId);
        }
        if (cookingLevel != null && !cookingLevel.isEmpty()) {
            sql.append("AND r.cookingLevel = ? ");
            params.add(cookingLevel);
        }

        sql.append("GROUP BY r.recipeId, u.userId, c.categoryId ORDER BY r.recipeCreateDate DESC");

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                recipes.add(mapRecipeFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered recipes", e);
        }
        return recipes;
    }

    public List<Recipe> getTopRatedRecipes(int limit) {
        List<Recipe> recipes = new ArrayList<>();
        final String sql = "SELECT r.*, u.*, c.*, " +
                "COALESCE(AVG(rt.stars), 0) as averageRating, " +
                "COUNT(rt.userId) as countRatings " +
                "FROM recipe r " +
                "JOIN user u ON r.userId = u.userId " +
                "JOIN category c ON r.categoryId = c.categoryId " +
                "LEFT JOIN rating rt ON r.recipeId = rt.recipeId " +
                "GROUP BY r.recipeId, u.userId, c.categoryId " +
                "ORDER BY averageRating DESC, countRatings DESC " +
                "LIMIT ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                recipes.add(mapRecipeFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching top rated recipes", e);
        }
        return recipes;
    }

}
