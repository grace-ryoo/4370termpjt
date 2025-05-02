package termProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import termProject.models.Diet;

@Service
public class DietService {
    @Autowired
    private DataSource dataSource;

    public List<Diet> getAllDiets() {
        List<Diet> diets = new ArrayList<>();
        final String sql = "SELECT dietId, dietName, dietDescription FROM diet";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                diets.add(new Diet(
                        rs.getInt("dietId"),
                        rs.getString("dietName"),
                        rs.getString("dietDescription")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching diets", e);
        }
        return diets;
    }
}
