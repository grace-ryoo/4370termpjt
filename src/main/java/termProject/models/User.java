package termProject.models;

/**
 * Represents a user of the recipe platform.
 */
public class User {
    /**
     * Unique identifier for the user.
     */
    private final String userId;

    /**
     * Username of the user.
     */
    private final String username;

    /**
     * First name of the user.
     */
    private final String firstName;

    /**
     * Last name of the user.
     */
    private final String lastName;

    /**
     * Constructs a User with specified details.
     *
     * @param userId the unique identifier of the user
     * @param username the username of the user
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param profileImagePath the path of the profile image file for the user
     */
    public User(String userId, String username, String firstName, String lastName) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Returns the user ID.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Returns the username of the user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the first name of the user.
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of the user.
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "User{id=" + userId + ", name='" + firstName + "'}";
    }
}
