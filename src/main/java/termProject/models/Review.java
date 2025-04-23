package termProject.models;

/**
 * Represents the basic structure of a review in the recipe platform. 
 */
public class Review {
    /**
     * Unique identifier for the review.
     */
    private final String reviewId;

    /**
     * Unique identifier for the recipe that is being reviewed.
     */
    private final String recipeId;

    /**
     * Text content of the review.
     */
    private final String comment;

    /**
     * Date when the review was created.
     */
    private final String reviewCreateDate;

    /**
     * User who created the review.
     */
    private final User user;

    /**
     * Rating of the review.
     */
    private final int rating;


    /**
     * Constructs a Review with specified details.
     *
     * @param reviewId the unique identifier of the review
     * @param recipeId the unique identifier of the recipe that is being reviewed
     * @param comment the text content of the review
     * @param reviewCreateDate the creation date of the review
     * @param user the user who created the review
     * @param rating the rating of the review
     */
    public Review(String reviewId, String recipeId, String comment, String reviewCreateDate, User user, int rating) {
        this.reviewId = reviewId;
        this.recipeId = recipeId;
        this.comment = comment;
        this.reviewCreateDate = reviewCreateDate;
        this.user = user;
        this.rating = rating;
    }

    /**
     * Returns the review ID.
     *
     * @return the recipe ID
     */
    public String getReviewId() {
        return reviewId;
    }

    /**
     * Returns the recipe ID.
     *
     * @return the recipe ID
     */
    public String getRecipeId() {
        return recipeId;
    }

    /**
     * Returns the content of the recipe.
     *
     * @return the content of the recipe
     */
    public String getComment() {
        return comment;
    }

    /**
     * Returns the review creation date.
     *
     * @return the recipe creation date
     */
    public String getReviewCreateDate() {
        return reviewCreateDate;
    }

    /**
     * Returns the user who created the recipe.
     *
     * @return the user who created the recipe
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the rating of the review.
     *
     * @return the rating of the review
     */
    public int getRating() {
        return rating;
    }
}
