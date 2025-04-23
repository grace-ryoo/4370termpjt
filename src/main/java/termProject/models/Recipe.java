package termProject.models;

import termProject.models.User;
import termProject.models.Category;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents the basic structure of a recipe in the recipe platform.
 */
public class Recipe {
    /**
     * Unique identifier for the recipe.
     */
    private final String recipeId;

    /**
     * Name of the recipe.
     */
    private final String recipeName;

    /**
     * Description of the recipe.
     */
    private final String description;

    /**
     * Date when the recipe was created.
     */
    private final String recipeCreateDate;

    /**
     * User who created the recipe.
     */
    private final User user;

    /**
     * Category of the recipe.
     */
    private final Category category;

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
     * List of ingredients in the recipe.
     */
    private final List<String> ingredients;

    /**
     * Constructs a Recipe with specified details.
     *
     * @param recipeId         the unique identifier of the recipe
     * @param name             the name of the recipe
     * @param description      the text content of the recipe
     * @param recipeCreateDate the creation date of the recipe
     * @param user             the user who created the recipe
     * @param category         the category of the recipe
     * @param prep_time        the time it takes to prep the recipe
     * @param cook_time        the time it takes to prep the recipe
     * @param servings         the amount of servings in the recipe
     * @param ingredients      the list of ingredients needed for the recipe
     */
    public Recipe(String recipeId, String recipeName, String description,
            String recipeCreateDate, User user, Category category,
            int prep_time, int cook_time, int servings,
            List<String> ingredients) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.description = description;
        this.recipeCreateDate = recipeCreateDate;
        this.user = user;
        this.category = category;
        this.prep_time = prep_time;
        this.cook_time = cook_time;
        this.servings = servings;
        this.ingredients = new ArrayList<>(ingredients); // Create a defensive copy
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
     * Returns the recipe name.
     *
     * @return the recipe name
     */
    public String getRecipeName() {
        return recipeName;
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
     * Returns the category of the recipe.
     *
     * @return the category of the recipe
     */
    public Category getCategory() {
        return category;
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

    /**
     * Returns the list of ingredients in the recipe.
     *
     * @return an unmodifiable list of ingredients
     */
    public List<String> getIngredients() {
        return new ArrayList<>(ingredients); // Return a defensive copy
    }
}
