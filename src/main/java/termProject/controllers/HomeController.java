package termProject.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import termProject.models.User;
import termProject.services.RecipeService;
import termProject.services.UserService;
import termProject.services.CategoryService;

import termProject.models.Category;
import termProject.models.Recipe;

@Controller
@RequestMapping
public class HomeController {

    private final RecipeService recipeService;
    private final UserService userService;
    private final CategoryService categoryService;

    @Autowired
    public HomeController(RecipeService recipeService, UserService userService, CategoryService categoryService) {
        this.categoryService = categoryService;
        this.recipeService = recipeService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView homepage(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("home_page");

        // Get current user
        if (userService.getLoggedInUser() == null) {
            return new ModelAndView("redirect:/login");
        }
        User user = userService.getLoggedInUser();
        mv.addObject("username", user.getFirstName());

        // Get recipe categories
        List<Category> categories = categoryService.getAllCategories();
        mv.addObject("categories", categories); 

        // Get trending recipes
        List<Recipe> trendingRecipes = recipeService.getTrendingRecipes();
        mv.addObject("trendingRecipes", trendingRecipes);

        // Get all recipes
        List<Recipe> allRecipes = recipeService.getAllRecipes();
        mv.addObject("recipes", allRecipes);
        if (allRecipes.isEmpty()) {
            mv.addObject("isNoContent", true);
        }

        // Add error message if present
        mv.addObject("errorMessage", error);

        return mv;
    }

    // @PostMapping("/createRecipe")
    // public String createRecipe(
    //         @RequestParam("recipeName") String recipeName,
    //         @RequestParam("description") String description,
    //         @RequestParam("cuisineId") String category,
    //         @RequestParam("ingredients") List<String> ingredients,
    //         @RequestParam("prepTime") int prepTime,
    //         @RequestParam("cookTime") int cookTime,
    //         @RequestParam("servings") int servings,
    //         @RequestParam("dietId") String dietId,
    //         @RequestParam("cookingLevel") String cookLevel) {

    //     String currentUserId = userService.getLoggedInUser().getUserId();

    //     // Validation
    //     if (recipeName == null || recipeName.trim().isEmpty()
    //             || description == null || description.trim().isEmpty() || currentUserId == null
    //             || currentUserId.isEmpty()
    //             || category == null || category.isEmpty() || prepTime <= 0 || cookTime <= 0 || servings <= 0
    //             || dietId == null || dietId.isEmpty()
    //             || cookLevel == null || cookLevel.trim().isEmpty()) {
    //         return "redirect:/?error=Recipe details cannot be empty";
    //     }

    //     boolean success = recipeService.createRecipe(
    //             recipeName,
    //             description,
    //             currentUserId,
    //             ingredients,
    //             prepTime,
    //             cookTime,
    //             servings,
    //             category,
    //             dietId,
    //             cookLevel);

    //     if (!success) {
    //         return "redirect:/?error=Failed to create the recipe. Please try again.";
    //     }

    //     return "redirect:/";
    // }
}
