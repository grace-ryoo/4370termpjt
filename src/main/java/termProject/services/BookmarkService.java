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

import termProject.models.Recipe;

@Service
public class BookmarkService {
    @Autowired
    private DataSource dataSource;

    private RecipeService recipeService;

    public BookmarkService(DataSource dataSource, RecipeService recipeService) {
        this.dataSource = dataSource;
        this.recipeService = recipeService;
    }

    public List<Recipe> getBookmarkedRecipesByType(String userId, String type) {
        List<Recipe> recipes = new ArrayList<>();

        final String sql = "SELECT r.*, u.username, u.firstName, u.lastName, c.categoryId, c.categoryName, c.categoryImageUrl, "
                +
                "COALESCE(AVG(rt.stars), 0) AS averageRating, " +
                "COUNT(rt.userId) AS countRatings, " +
                "b.bookmark_type " +
                "FROM bookmark b " +
                "JOIN recipe r ON b.recipeId = r.recipeId " +
                "JOIN user u ON r.userId = u.userId " +
                "JOIN category c ON r.categoryId = c.categoryId " +
                "LEFT JOIN rating rt ON r.recipeId = rt.recipeId " +
                "WHERE b.userId = ? AND b.bookmark_type = ? " +
                "GROUP BY r.recipeId, u.userId, u.username, u.firstName, u.lastName, c.categoryId, c.categoryName, c.categoryImageUrl, b.bookmark_type";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, type);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                recipes.add(recipeService.mapRecipeFromResultSet(rs));
            }

        } catch (SQLException e) {
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

    public String getBookmarkType(String recipeId) {
        String bookmarkType = null;
        String query = "SELECT bookmark_type FROM bookmark WHERE recipeId = ?";

        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, recipeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                bookmarkType = resultSet.getString("bookmark_type");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception as needed
        }

        return bookmarkType;
    }

    /**
     * Retrieves the bookmark type for a recipe by a specific user
     * 
     * @param userId   The ID of the user checking the bookmark
     * @param recipeId The ID of the recipe to check
     * @return The bookmark type ("PAST", "FUTURE") or null if not bookmarked
     */
    public String getUserBookmarkType(String userId, String recipeId) {
        String bookmarkType = null;
        String query = "SELECT bookmark_type FROM bookmark WHERE userId = ? AND recipeId = ?";

        try (Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, userId);
            stmt.setString(2, recipeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                bookmarkType = rs.getString("bookmark_type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookmarkType;
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
