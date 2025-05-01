package termProject.models;

/**
 * Represents the category of a recipe in the recipe platform.
 */
public class Category {
    /**
     * Unique identifier for the category.
     */
    private final String categoryId;

    /**
     * Name of the category.
     */
    private final String categoryName;


    /**
     * Category Image
     */
    private final String categoryImageUrl;

    /**
     * Constructs a new Category with the specified ID and name.
     *
     * @param categoryId   the unique identifier for the category
     * @param categoryName the name of the category
     * @param categoryImageUrl the URL of the category image
     */
    public Category(String categoryId, String categoryName, String categoryImageUrl) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryImageUrl = categoryImageUrl;
    }

    /**
     * Returns the category ID.
     *
     * @return the category ID
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Returns the category name.
     *
     * @return the category name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Returns the category image URL.
     *
     * @return the category image URL
     */
    public String getCategoryImageUrl() {
        return categoryImageUrl;
    }

}
