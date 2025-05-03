package termProject.services;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import termProject.models.Review;
import termProject.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    @Autowired
    private DataSource dataSource;
    private UserService userService;

    public ReviewService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }

    public void addReview(String userId, String recipeId, String reviewText) {
        final String sql = "INSERT INTO review (userId, recipeId, reviewText) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, recipeId);
            pstmt.setString(3, reviewText);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding review: ", e);
        }
    }

    private String formatReviewDate(LocalDateTime reviewDate) {
        LocalDateTime now = LocalDateTime.now();
        long hoursAgo = ChronoUnit.HOURS.between(reviewDate, now);
        long daysAgo = ChronoUnit.DAYS.between(reviewDate, now);

        if (daysAgo < 2) {
            if (hoursAgo < 1) {
                return "just now";
            } else if (hoursAgo == 1) {
                return "1 hour ago";
            } else if (daysAgo == 0) {
                return hoursAgo + " hours ago";
            } else if (daysAgo == 1) {
                return "1 day ago";
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return reviewDate.format(formatter);
    }

    public List<Review> getReviewsForRecipe(String recipeId) {
        List<Review> reviews = new ArrayList<>();
        final String sql = "SELECT r.*, u.firstName, u.lastName, u.userName, u.userId " +
                "FROM review r " +
                "JOIN user u ON r.userId = u.userId " +
                "WHERE r.recipeId = ? " +
                "ORDER BY r.reviewDate DESC";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, recipeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getString("userId"),
                        rs.getString("userName"),
                        rs.getString("firstName"),
                        rs.getString("lastName"));

                LocalDateTime reviewDate = rs.getTimestamp("reviewDate").toLocalDateTime();
                String formattedDate = formatReviewDate(reviewDate);

                Review review = new Review(
                        rs.getString("reviewId"),
                        rs.getString("recipeId"),
                        rs.getString("reviewText"),
                        formattedDate, 
                        user);
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            throw new RuntimeException("Error getting reviews: ", e);
        }
        return reviews;
    }
}
