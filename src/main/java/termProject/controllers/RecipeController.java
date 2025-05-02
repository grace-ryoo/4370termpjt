package termProject.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import termProject.models.Recipe;
import termProject.services.CategoryService;
import termProject.services.CuisineService;
import termProject.services.DietService;
import termProject.services.RecipeService;
import termProject.services.ReviewService;
import termProject.services.UserService;

@Controller
@RequestMapping("/recipe")
public class RecipeController {
    private final RecipeService recipeService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final DietService dietService;
    private final CuisineService cuisineService;

    @Autowired
    public RecipeController(RecipeService recipeService,
            UserService userService,
            ReviewService reviewService,
            CategoryService categoryService,
            DietService dietService,
            CuisineService cuisineService) {
        this.recipeService = recipeService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.categoryService = categoryService;
        this.dietService = dietService;
        this.cuisineService = cuisineService;
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
    public ModelAndView showRecipe(@PathVariable String recipeId) {
        ModelAndView mv = new ModelAndView("recipe_detail");
        try {
            String userId = userService.isAuthenticated() ? userService.getLoggedInUser().getUserId() : null;
            Recipe recipe = recipeService.getRecipeById(recipeId, userId);

            if (recipe == null) {
                return new ModelAndView("redirect:/recipe/recipes");
            }
            System.out.println("DEBUG: Found recipe with ID " + recipeId); // Debug log
            mv.addObject("recipe", recipe);
            if (userService.isAuthenticated()) {
                mv.addObject("username", userService.getLoggedInUser().getFirstName());
            }
            return mv;

        } catch (Exception e) {
            return new ModelAndView("redirect:/recipe/recipes");
        }
    }

    @GetMapping("/view/{recipeId}")
    public ModelAndView viewRecipe(@PathVariable String recipeId) {
        ModelAndView mv = new ModelAndView("recipe_detail");
        try {
            String userId = userService.isAuthenticated() ? userService.getLoggedInUser().getUserId() : null;
            Recipe recipe = recipeService.getRecipeById(recipeId, userId);

            if (recipe == null) {
                return new ModelAndView("redirect:/recipes?error=Recipe not found");
            }

            mv.addObject("recipe", recipe);

            if (userService.isAuthenticated()) {
                mv.addObject("username", userService.getLoggedInUser().getFirstName());
            }

            return mv;
        } catch (Exception e) {
            System.err.println("Error viewing recipe: " + e.getMessage());
            e.printStackTrace();
            return new ModelAndView("redirect:/recipes?error=Error viewing recipe");
        }
    }

    @GetMapping("/new")
    public ModelAndView showRecipeForm() {
        ModelAndView mv = new ModelAndView("recipe_form");

        // Add required data for dropdowns
        mv.addObject("categories", categoryService.getAllCategories());
        mv.addObject("cuisines", cuisineService.getAllCuisines()); // Add this line
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
    public String createRecipe(
            @RequestParam("recipeName") String recipeName,
            @RequestParam("description") String description,
            @RequestParam("categoryId") String categoryId,
            @RequestParam("prep_time") int prepTime,
            @RequestParam("cook_time") int cookTime,
            @RequestParam("servings") int servings,
            @RequestParam("ingredients[]") List<String> ingredients,
            @RequestParam("amounts[]") List<String> amounts,
            @RequestParam("units[]") List<String> units,
            @RequestParam("dietId") String dietId,
            @RequestParam("cookingLevel") String cookingLevel,
            @RequestParam("cuisineId") int cuisineId) {

        try {
            String userId = userService.getLoggedInUser().getUserId();
            String recipeId = recipeService.createRecipe(recipeName, description, userId,
                    ingredients, amounts, units, prepTime, cookTime, servings,
                    categoryId, dietId, cookingLevel, cuisineId);

            // Redirect to the recipes page after successful creation
            return "redirect:/recipe/recipes";
        } catch (Exception e) {
            return "redirect:/recipe/new?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/recipes")
    public ModelAndView showAllRecipes(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) Integer dietId,
            @RequestParam(required = false) Integer cuisineId,
            @RequestParam(required = false) String cookingLevel) {

        ModelAndView mv = new ModelAndView("recipes");
        try {
            // Get all recipes (filtered or unfiltered)
            List<Recipe> recipes = recipeService.getFilteredRecipes(categoryId, dietId, cuisineId, cookingLevel);
            mv.addObject("recipes", recipes);

            // Get filter options from database
            List<Map<String, Object>> categories = categoryService.getAllCategories().stream()
                    .map(cat -> {
                        Map<String, Object> catMap = new HashMap<>();
                        catMap.put("categoryId", cat.getCategoryId());
                        catMap.put("categoryName", cat.getCategoryName());
                        catMap.put("selected", cat.getCategoryId().equals(categoryId));
                        return catMap;
                    })
                    .collect(Collectors.toList());

            List<Map<String, Object>> diets = dietService.getAllDiets().stream()
                    .map(diet -> {
                        Map<String, Object> dietMap = new HashMap<>();
                        dietMap.put("dietId", diet.getDietId());
                        dietMap.put("dietName", diet.getDietName());
                        dietMap.put("selected",
                                dietId != null && diet.getDietId() == dietId);
                        return dietMap;
                    })
                    .collect(Collectors.toList());

            List<Map<String, Object>> cuisines = cuisineService.getAllCuisines().stream()
                    .map(cuisine -> {
                        Map<String, Object> cuisineMap = new HashMap<>();
                        cuisineMap.put("cuisineId", cuisine.getCuisineId());
                        cuisineMap.put("cuisineName", cuisine.getCuisineName());
                        cuisineMap.put("selected",
                                String.valueOf(cuisine.getCuisineId()).equals(String.valueOf(cuisineId)));
                        return cuisineMap;
                    })
                    .collect(Collectors.toList());

            // Add filter options to model
            mv.addObject("categories", categories);
            mv.addObject("diets", diets);
            mv.addObject("cuisines", cuisines);

            // Add cooking level states
            mv.addObject("cookingLevels", List.of(
                    Map.of("value", "Beginner", "name", "Beginner", "selected", "Beginner".equals(cookingLevel)),
                    Map.of("value", "Intermediate", "name", "Intermediate", "selected",
                            "Intermediate".equals(cookingLevel)),
                    Map.of("value", "Advanced", "name", "Advanced", "selected", "Advanced".equals(cookingLevel))));

            if (userService.isAuthenticated()) {
                mv.addObject("username", userService.getLoggedInUser().getFirstName());
            }

            return mv;
        } catch (Exception e) {
            System.err.println("Error loading recipes: " + e.getMessage());
            e.printStackTrace();
            mv.addObject("error", "Error loading recipes");
            return mv;
        }
    }
}
