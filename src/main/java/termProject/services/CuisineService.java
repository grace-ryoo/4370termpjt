package termProject.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import termProject.models.Cuisine;

@Service
public class CuisineService {
    @Autowired
    private DataSource dataSource;

    public List<Cuisine> getAllCuisines() {
        List<Cuisine> cuisines = new ArrayList<>();
        final String sql = "SELECT cuisineId, cuisineName, cuisineDescription FROM cuisine";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cuisines.add(new Cuisine(
                        rs.getInt("cuisineId"),
                        rs.getString("cuisineName")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching cuisines", e);
        }
        return cuisines;
    }
}
