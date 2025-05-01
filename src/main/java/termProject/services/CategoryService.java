package termProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import termProject.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private DataSource dataSource;

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        final String sql = "SELECT * FROM category";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(new Category(
                        rs.getString("categoryId"),
                        rs.getString("categoryName"),
                        rs.getString("categoryImageUrl")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching categories", e);
        }
        return categories;
    }
}
