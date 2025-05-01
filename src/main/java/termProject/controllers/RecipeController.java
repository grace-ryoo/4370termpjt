package termProject.controllers;

import java.nio.charset.StandardCharsets;
import java.util.List;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import termProject.models.Review;
import termProject.models.Recipe;
import termProject.services.RecipeService;
import termProject.services.ReviewService;
import termProject.services.UserService;
import termProject.services.CategoryService;
import termProject.services.DietService;

@Controller
@RequestMapping("/recipe")
public class RecipeController {
    private final RecipeService recipeService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final DietService dietService;

    @Autowired
    public RecipeController(RecipeService recipeService,
            UserService userService,
            ReviewService reviewService,
            CategoryService categoryService,
            DietService dietService) {
        this.recipeService = recipeService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.categoryService = categoryService;
        this.dietService = dietService;
    }

    /**
     * This function handles the /recipe/{recipeId} URL. This handlers serves the
     * web page for a specific recipe. Note there is a path variable {recipeId}. An
     * example URL handled by this function looks like below:
     * http://localhost:8081/recipe/1 The above URL assigns 1 to recipeId.
     *
     * See notes from HomeController.java regardig error URL parameter.
     */
    @GetMapping("/{recipeId}")
    public ModelAndView webpage(@PathVariable("recipeId") String recipeId,
            @RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("recipe_detail"); // Change to recipe_detail instead of recipes_page

        try {
            String currUserId = userService.getLoggedInUser().getUserId();
            Recipe recipe = recipeService.getRecipeById(recipeId, currUserId);

            if (recipe == null) {
                throw new RuntimeException("Recipe not found");
            }

            mv.addObject("recipe", recipe); // Add recipe object
            // mv.addObject("reviews", reviewService.getReviewsByRecipeId(recipeId)); // Add
            // reviews
            mv.addObject("errorMessage", error);

            if (userService.isAuthenticated()) {
                mv.addObject("username", userService.getLoggedInUser().getFirstName());
            }

            return mv;
        } catch (Exception e) {
            mv.addObject("errorMessage", "Failed to load recipe: " + e.getMessage());
            return mv;
        }
    }

    @GetMapping("/new")
    public ModelAndView showRecipeForm() {
        ModelAndView mv = new ModelAndView("recipe_form");

        // Add empty recipe object for the form
        mv.addObject("recipe", new Recipe());
        
        // Add required data for dropdowns
        mv.addObject("categories", categoryService.getAllCategories());
        mv.addObject("diets", dietService.getAllDiets());
        if (userService.isAuthenticated()) {
            mv.addObject("username", userService.getLoggedInUser().getFirstName());
        }
        return mv;
    }

    @PostMapping("/{recipeId}/bookmark/{isAdd}/{bookmarkType}")
    public String addOrRemoveBookmark(@PathVariable("recipeId") String recipeId,
            @PathVariable("isAdd") Boolean isAdd,
            @PathVariable("bookmarkType") String bookmarkType) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            String currUserId = userService.getLoggedInUser().getUserId();
            if (isAdd) {
                recipeService.bookmark(currUserId, recipeId, bookmarkType);
            } else {
                recipeService.unBookmark(currUserId, recipeId, bookmarkType);
            }
            return "redirect:/recipe/" + recipeId;
        } catch (Exception e) {
            String message = URLEncoder.encode("Failed to update bookmark: " + e.getMessage(),
                    StandardCharsets.UTF_8);
            return "redirect:/recipe/" + recipeId + "?error=" + message;
        }
    }

    @PostMapping("/create")
    public String createRecipe(@RequestParam("recipeName") String recipeName,
            @RequestParam("description") String description,
            @RequestParam("categoryId") String categoryId,
            @RequestParam("prep_time") int prepTime,
            @RequestParam("cook_time") int cookTime,
            @RequestParam("servings") int servings,
            @RequestParam("ingredients[]") List<String> ingredients,
            @RequestParam("dietId") String dietId,
            @RequestParam("cookingLevel") String cookingLevel) {

        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            String userId = userService.getLoggedInUser().getUserId();
            String recipeId = recipeService.createRecipe(recipeName, description, userId,
                    ingredients, prepTime, cookTime, servings, categoryId, dietId, cookingLevel);
            return "redirect:/recipe/" + recipeId;
        } catch (Exception e) {
            String message = URLEncoder.encode("Failed to create recipe: " + e.getMessage(),
                    StandardCharsets.UTF_8);
            return "redirect:/recipe/new?error=" + message;
        }
    }
}
