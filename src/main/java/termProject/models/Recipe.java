package termProject.models;

import java.util.List;

public class Recipe {
    private String recipeId;
    private String recipeName;
    private String description;
    private String userId;
    private String categoryId;
    private String dietId;
    private int prepTime;
    private int cookTime;
    private int servings;
    private int cuisineId;
    private String cookingLevel;
    private List<String> ingredients;
    private String imageUrl;
    private String stars;
    private int ratingCount;

    // Default constructor needed for form binding
    public Recipe() {
    }

    // Full constructor
    public Recipe(String recipeId, String recipeName, String description,
            String userId, String categoryId, String dietId,
            int prepTime, int cookTime, int servings,
            String cookingLevel, int cuisineId, List<String> ingredients,
            String imageUrl) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.description = description;
        this.userId = userId;
        this.categoryId = categoryId;
        this.dietId = dietId;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.servings = servings;
        this.cookingLevel = cookingLevel;
        this.cuisineId = cuisineId;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDietId() {
        return dietId;
    }

    public void setDietId(String dietId) {
        this.dietId = dietId;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getCookingLevel() {
        return cookingLevel;
    }

    public void setCookingLevel(String cookingLevel) {
        this.cookingLevel = cookingLevel;
    }

    public int getCuisineId() {
        return cuisineId;
    }

    public void setCuisineId(int cuisineId) {
        this.cuisineId = cuisineId;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}
