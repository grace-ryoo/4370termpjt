package termProject.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import termProject.models.Category;
import termProject.models.Recipe;
import termProject.services.RecipeService;
import termProject.services.UserService;

@Controller
@RequestMapping
public class HomeController {
    private final RecipeService recipeService;
    private final UserService userService;

    @Autowired
    public HomeController(RecipeService recipeService, UserService userService) {
        this.recipeService = recipeService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView homepage(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("home_page");

        // Get current user
        String currentUserId = userService.getLoggedInUser().getUserId();
        String username = userService.getLoggedInUser().getUsername();
        mv.addObject("username", username);

        // Get recipe categories
        List<Category> categories = recipeService.getAllCategories();
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

        mv.addObject("errorMessage", error);
        return mv;
    }

    @PostMapping("/createRecipe")
    public String createRecipe(
            @RequestParam("recipeName") String recipeName,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("ingredients") List<String> ingredients,
            @RequestParam("prepTime") int prepTime,
            @RequestParam("cookTime") int cookTime,
            @RequestParam("servings") int servings) {

        String currentUserId = userService.getLoggedInUser().getUserId();

        // Validation
        if (recipeName == null || recipeName.trim().isEmpty() ||
                description == null || description.trim().isEmpty()) {
            return "redirect:/?error=Recipe details cannot be empty";
        }

        boolean success = recipeService.createRecipe(
                recipeName,
                description,
                currentUserId,
                category,
                ingredients,
                prepTime,
                cookTime,
                servings);

        if (!success) {
            return "redirect:/?error=Failed to create the recipe. Please try again.";
        }

        return "redirect:/";
    }
}
