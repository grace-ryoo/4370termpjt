package uga.menik.cs4370.services;

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

import uga.menik.cs4370.models.Recipe;
import uga.menik.cs4370.models.User;
import uga.menik.cs4370.models.Category;

@Service
public class BookmarkService {
    @Autowired
    private DataSource dataSource;

    public BookmarkService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Recipe> getBookmarkedRecipes(String userId) {
        List<Recipe> recipes = new ArrayList<>();

        final String sql = "";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId); // For is_hearted check
            pstmt.setString(2, userId); // For user's bookmarks

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                        rs.getString("userId"), 
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("profileImagePath")
                    );

                    Category category = new Category(
                        rs.getString("categoryId"),
                        rs.getString("categoryName")
                    );

                    Recipe recipe = new Recipe(
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
                    recipes.add(recipe);
                    
                }
            }
        } catch(SQLException e) {
            throw new RuntimeException("Error fetching posts", e);
        }

        return recipes;
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
