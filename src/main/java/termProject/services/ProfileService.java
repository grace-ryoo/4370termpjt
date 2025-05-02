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
import java.util.Arrays;
import java.util.List;
 
 import javax.sql.DataSource;
 
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Service;
 
 import termProject.models.Category;
 import termProject.models.Recipe;
 import termProject.models.User;
 
 @Service
 public class ProfileService {
     
     @Autowired
     private DataSource dataSource;
 
     public List<Recipe> getRecipeBySpecificUser(String userId) {
        List<Recipe> recipesByUser = new ArrayList<>();
    
        String sql = "SELECT r.*, u.userId, u.firstName, u.lastName, " +
                "c.categoryId, c.categoryName, c.categoryImageUrl, " +
                "COALESCE(AVG(rt.stars), 0) AS averageRating, " +
                "COUNT(rt.userId) AS countRatings " +
                "FROM user u " +
                "JOIN recipe r ON u.userId = r.userId " +
                "JOIN category c ON r.categoryId = c.categoryId " +
                "LEFT JOIN rating rt ON r.recipeId = rt.recipeId " +
                "WHERE u.userId = ? " +
                "GROUP BY r.recipeId, u.userId, c.categoryId ";
    
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                User user = new User(
                        rs.getString("userId"),
                        null,
                        rs.getString("firstName"),
                        rs.getString("lastName")
                );
    
                Category category = new Category(
                        rs.getString("categoryId"),
                        rs.getString("categoryName"),
                        rs.getString("categoryImageUrl")
                );
    
                Recipe recipe = new Recipe(
                        rs.getString("recipeId"),
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
                        Arrays.asList(rs.getString("ingredients").split(","))
                );
    
                recipesByUser.add(recipe);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching recipes by user ID", e);
        }
    
        return recipesByUser;
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