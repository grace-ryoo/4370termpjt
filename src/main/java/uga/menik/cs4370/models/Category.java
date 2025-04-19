package main.java.uga.menik.cs4370.models;

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
    

    public Category(String categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
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

}
