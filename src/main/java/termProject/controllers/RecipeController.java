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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import termProject.models.Recipe;
import termProject.services.CategoryService;
import termProject.services.CuisineService;
import termProject.services.DietService;
import termProject.services.FileStorageService;
import termProject.services.RecipeService;
import termProject.services.ReviewService;
import termProject.services.UserService;
import termProject.models.User;
import termProject.services.BookmarkService;

@Controller
@RequestMapping("/recipe")
public class RecipeController {
    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookmarkService bookmarkService;

    private final ReviewService reviewService;
    private final CategoryService categoryService;
    private final DietService dietService;
    private final CuisineService cuisineService;
    private final FileStorageService fileStorageService;

    @Autowired
    public RecipeController(ReviewService reviewService,
            CategoryService categoryService,
            DietService dietService,
            CuisineService cuisineService,
            FileStorageService fileStorageService) {
        this.reviewService = reviewService;
        this.categoryService = categoryService;
        this.dietService = dietService;
        this.cuisineService = cuisineService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/view/{id}")
    public ModelAndView viewRecipe(@PathVariable("id") String recipeId) {
        ModelAndView mv = new ModelAndView("recipe_detail");

        // Check if user is logged in
        User loggedInUser = userService.getLoggedInUser();
        if (loggedInUser != null) {
            String userId = loggedInUser.getUserId();
            mv.addObject("username", loggedInUser.getFirstName());

            // Get recipe details - this is crucial
            Recipe recipe = recipeService.getRecipeById(recipeId, userId);
            if (recipe == null) {
                // Handle recipe not found
                return new ModelAndView("redirect:/?error=Recipe+not+found");
            }

            // Add recipe object to the model
            mv.addObject("recipe", recipe);

            // Get bookmark status
            String bookmarkType = bookmarkService.getUserBookmarkType(userId, recipeId);
            mv.addObject("isPastBookmarked", "PAST".equals(bookmarkType));
            mv.addObject("isFutureBookmarked", "FUTURE".equals(bookmarkType));

            // Get user's rating for this recipe
            Integer userRating = recipeService.getUserRating(userId, recipeId);
            if (userRating != null) {
                mv.addObject("userHasRated", true);
                mv.addObject("userRating", userRating); // Add the actual rating number

                // Set individual rating flags for checked state
                for (int i = 1; i <= 5; i++) {
                    mv.addObject("userRating" + i, userRating == i);
                }
            } else {
                mv.addObject("userHasRated", false);
            }
        }

        return mv;
    }

    @GetMapping("/new")
    public ModelAndView showRecipeForm() {
        ModelAndView mv = new ModelAndView("recipe_form");

        // Add required data for dropdowns
        mv.addObject("categories", categoryService.getAllCategories());
        mv.addObject("cuisines", cuisineService.getAllCuisines());
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
                recipeService.unBookmark(currUserId, recipeId);
            }
            return "redirect:/recipe/view/" + recipeId;
        } catch (Exception e) {
            String message = URLEncoder.encode("Failed to update bookmark: " + e.getMessage(),
                    StandardCharsets.UTF_8);
            return "redirect:/recipe/view/" + recipeId + "?error=" + message;
        }
    }

    @PostMapping("/{recipeId}/rate")
    public String rateRecipe(@PathVariable String recipeId, @RequestParam int rating) {
        try {
            if (!userService.isAuthenticated()) {
                return "redirect:/login";
            }

            String userId = userService.getLoggedInUser().getUserId();
            recipeService.addRating(userId, recipeId, rating);

            return "redirect:/recipe/view/" + recipeId;
        } catch (Exception e) {
            return "redirect:/recipe/view/" + recipeId + "?error=" +
                    URLEncoder.encode("Failed to rate recipe: " + e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/{recipeId}/review")
    public String addReview(@PathVariable String recipeId, @RequestParam String comment) {
        try {
            if (!userService.isAuthenticated()) {
                return "redirect:/login";
            }

            String userId = userService.getLoggedInUser().getUserId();
            reviewService.addReview(userId, recipeId, comment);

            return "redirect:/recipe/view/" + recipeId;
        } catch (Exception e) {
            return "redirect:/recipe/view/" + recipeId + "?error=" +
                    URLEncoder.encode("Failed to add review: " + e.getMessage(), StandardCharsets.UTF_8);
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
            @RequestParam("cuisineId") int cuisineId,
            @RequestParam(value = "recipeImage", required = false) MultipartFile recipeImage) {

        try {
            String imageUrl = null;
            if (recipeImage != null && !recipeImage.isEmpty()) {
                // Save the image and get its URL
                imageUrl = fileStorageService.storeFile(recipeImage);
            }

            // Create recipe with image URL
            String recipeId = recipeService.createRecipe(
                    recipeName, description, userService.getLoggedInUser().getUserId(),
                    ingredients, amounts, units, prepTime, cookTime, servings,
                    categoryId, dietId, cookingLevel, cuisineId, imageUrl);

            return "redirect:/recipe/view/" + recipeId;
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
                                dietId != null && diet.getDietId() == (dietId));
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

            // Add cooking level flags
            mv.addObject("isEasy", "easy".equals(cookingLevel));
            mv.addObject("isMedium", "medium".equals(cookingLevel));
            mv.addObject("isHard", "hard".equals(cookingLevel));

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
