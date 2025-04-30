package termProject.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import termProject.models.User;

@Service
public class UserService {

    private final DataSource dataSource;
    private final BCryptPasswordEncoder passwordEncoder;
    private User loggedInUser = null;

    /**
     * See AuthInterceptor notes regarding dependency injection and inversion of
     * control.
     */
    @Autowired
    public UserService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Authenticate user given the username and the password and stores user
     * object for the logged in user in session scope. Returns true if
     * authentication is succesful. False otherwise.
     */
    public boolean authenticate(String username, String password) throws SQLException {
        final String sql = "select * from user where username = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Traverse the result rows one at a time.
                // Note: This specific while loop will only run at most once 
                // since username is unique.
                while (rs.next()) {
                    String storedPasswordHash = rs.getString("password");
                    boolean isPassMatch = passwordEncoder.matches(password, storedPasswordHash);
                    if (isPassMatch) {
                        String userId = rs.getString("userId");
                        String firstName = rs.getString("firstName");
                        String lastName = rs.getString("lastName");

                        loggedInUser = new User(userId, firstName, lastName);
                    }
                    return isPassMatch;
                }
            }
        }
        return false;
    }

    /**
     * Logs out the user.
     */
    public void unAuthenticate() {
        loggedInUser = null;
    }

    /**
     * Checks if a user is currently authenticated.
     */
    public boolean isAuthenticated() {
        System.out.println("Checking if user is logged in: " + (loggedInUser != null));
        return loggedInUser != null;
    }

    /**
     * Retrieves the currently logged-in user.
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Registers a new user with the given details. Returns true if registration
     * is successful. If the username already exists, a SQLException is thrown
     * due to the unique constraint violation, which should be handled by the
     * caller.
     */
    public boolean registerUser(String username, String password, String firstName, String lastName)
            throws SQLException {
        final String registerSql = "insert into user (username, password, firstName, lastName) values (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection(); PreparedStatement registerStmt = conn.prepareStatement(registerSql, Statement.RETURN_GENERATED_KEYS)) {
            registerStmt.setString(1, username);
            registerStmt.setString(2, passwordEncoder.encode(password));
            registerStmt.setString(3, firstName);
            registerStmt.setString(4, lastName);

            int rowsAffected = registerStmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = registerStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        String userId = generatedKeys.getString(1);
                    }
                }
                return true;
            }
        }
        return false;
    }
}
