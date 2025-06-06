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

import termProject.models.GroceryItem;
import termProject.models.User;

@Service
public class GroceryListService {

    @Autowired
    private DataSource dataSource;
    private UserService userService;

    public GroceryListService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<GroceryItem> getGroceryList(String userId) {
        List<GroceryItem> items = new ArrayList<>();

        final String sql = "SELECT itemId, itemName, itemQuantity, isBought FROM groceryList WHERE userId = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int itemId = rs.getInt("itemId");
                    String itemName = rs.getString("itemName");
                    int itemQuantity = rs.getInt("itemQuantity");
                    boolean isBought = rs.getBoolean("isBought");

                    User user = new User(userId, "", "", ""); // Assuming firstName and lastName are not needed here
                    GroceryItem item = new GroceryItem(itemId, itemName, itemQuantity, user);
                    item.setIsBought(isBought);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching grocery list", e);
        }

        return items;
    }

    public void addGroceryItem(String userId, String itemName, int quantity) {
        final String sql = "INSERT INTO groceryList (itemId, userId, itemName, itemQuantity) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int itemId = generateItemId(); 
            pstmt.setInt(1, itemId);
            pstmt.setString(2, userId);
            pstmt.setString(3, itemName);
            pstmt.setInt(4, quantity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding grocery item", e);
        }
    }

    public void deleteGroceryItem(String userId, int itemId) {
        final String sql = "DELETE FROM groceryList WHERE userId = ? AND itemId = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing grocery item", e);
        }
    }

    private int generateItemId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    public void markItemAsBought(String userId, int itemId) {
        final String sql = "UPDATE groceryList SET isBought = TRUE WHERE userId = ? AND itemId = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error marking grocery item as bought", e);
        }
    }

    public void setItemBoughtStatus(String userId, int itemId, boolean isBought) {
        final String sql = "UPDATE groceryList SET isBought = ? WHERE userId = ? AND itemId = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isBought);
            pstmt.setString(2, userId);
            pstmt.setInt(3, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating bought status", e);
        }
    }

}
