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
public class ProfileService {

    @Autowired
    private DataSource dataSource;

    private RecipeService recipeService;

    public ProfileService(DataSource dataSource, RecipeService recipeService) {
        this.dataSource = dataSource;
        this.recipeService = recipeService;
    }

    public List<Recipe> getRecipeBySpecificUser(String userId) {
        List<Recipe> recipesByUser = new ArrayList<>();
    
        String sql = "SELECT r.*, u.*, c.*, " +
                "COALESCE(AVG(rt.stars), 0) AS averageRating, " +
                "COUNT(rt.userId) AS countRatings " +
                "FROM user u " +
                "JOIN recipe r ON u.userId = r.userId " +
                "JOIN category c ON r.categoryId = c.categoryId " +
                "LEFT JOIN rating rt ON r.recipeId = rt.recipeId " +
                "WHERE u.userId = ? " +
                "GROUP BY r.recipeId ";
    
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                recipesByUser.add(recipeService.mapRecipeFromResultSet(rs));
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