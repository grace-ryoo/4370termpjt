package main.java.uga.menik.cs4370.models;

/**
 * Represents the basic structure of a recipe in the recipe platform. 
 */
public class BasicRecipe {
    /**
     * Unique identifier for the recipe.
     */
    private final String recipeId;

    /**
     * Text content of the post.
     */
    private final String description;

    /**
     * Date when the post was created.
     */
    private final String recipeCreateDate;

    /**
     * User who created the post.
     */
    private final User user;

    /**
     * Time it takes to prep the recipe.
     */
    private final int prep_time;

    /**
     * Time it takes to cook the recipe.
     */
    private final int cook_time;

    /**
     * Amount of servings in the recipe.
     */
    private final int servings;


    /**
     * Constructs a BasicPost with specified details.
     *
     * @param recipeId the unique identifier of the recipe
     * @param description the text content of the recipe
     * @param recipeCreateDate the creation date of the recipe
     * @param user the user who created the recipe
     * @param prep_time the time it takes to prep the recipe
     * @param cook_time the time it takes to prep the recipe
     * @param servings the amount of servings in the recipe
     */
    public BasicRecipe(String recipeId, String description, String recipeCreateDate, User user, int prep_time, int cook_time, int servings) {
        this.recipeId = recipeId;
        this.description = description;
        this.recipeCreateDate = recipeCreateDate;
        this.user = user;
        this.prep_time = prep_time;
        this.cook_time = cook_time;
        this.servings = servings;
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
    public String getDescription() {
        return description;
    }

    /**
     * Returns the recipe creation date.
     *
     * @return the recipe creation date
     */
    public String getRecipeCreateDate() {
        return recipeCreateDate;
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
     * Returns the time it takes to prep the recipe.
     *
     * @return the time it takes to prep the recipe
     */
    public int getPrepTime() {
        return prep_time;
    }

    /**
     * Returns the time it takes to cook the recipe.
     *
     * @return the time it takes to cook the recipe
     */
    public int getCookTime() {
        return cook_time;
    }

    /**
     * Returns the amount of servings in the recipe.
     *
     * @return the amount of servings in the recipe
     */
    public int getServings() {
        return servings;
    }
}
