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
         List<Recipe> recipeByUser = new ArrayList<>();
 
         final String sql = "SELECT r.id AS recipeId, r.content AS description, r.created_at AS recipeCreateDate, " +
                 "u.userId, u.username, u.firstName, u.lastName, " +
                 "(SELECT COUNT(*) FROM heart h WHERE h.recipeId = r.id) AS hearts_count, " +
                 "(SELECT COUNT(*) FROM comment c WHERE c.recipeId = r.id) AS comments_count, " +
                 "EXISTS (SELECT 1 FROM heart h WHERE h.recipeId = r.id AND h.userId = ?) AS is_hearted, " +
                 "EXISTS (SELECT 1 FROM bookmark b WHERE b.recipeId = r.id AND b.userId = ?) AS is_bookmarked " +
                 "FROM recipe r " +
                 "JOIN user u ON r.user_id = u.userId " +
                 "WHERE r.user_id = ? " +
                 "ORDER BY r.created_at DESC";
 
         try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
             pstmt.setString(1, userId); // Checking if the logged-in user liked the recipe
             pstmt.setString(2, userId); // Checking if the logged-in user bookmarked the recipe
             pstmt.setString(3, userId); // Filtering recipe by the specific user
 
             try (ResultSet rs = pstmt.executeQuery()) {
                 while (rs.next()) {
                     User user = new User(rs.getString("userId"), 
                        rs.getString("userName"),
                         rs.getString("firstName"),
                         rs.getString("lastName")
                     );
 
                     Recipe recipe = new Recipe(
                             rs.getString("recipeId"),
                             rs.getString("recipeName"),
                             rs.getString("description"),
                             rs.getString("userId"),
                             rs.getString(
                                     "categoryId"),
                             rs.getString("dietId"),
                             rs.getInt("prep_time"),
                             rs.getInt("cook_time"),
                             rs.getInt("servings"),
                             rs.getString("cookingLevel"),
                             rs.getInt("cuisineId"),

                                Arrays.asList(rs.getString("ingredients").split(",")),
                                rs.getString("imageUrl")
                     // avgRating,
                     // numRatings
                     );

                     recipeByUser.add(recipe);
                 }
             }
         } catch(SQLException e) {
             throw new RuntimeException("Error fetching recipes by profile", e);
         }
 
         return recipeByUser;
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