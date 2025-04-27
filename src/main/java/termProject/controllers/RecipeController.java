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

@Controller
@RequestMapping("/recipe")
public class RecipeController {
    @Autowired
    private RecipeService recipeService;
    private ReviewService reviewService;
    private UserService userService;

    @Autowired
    public RecipeController(RecipeService recipeService, UserService userService, ReviewService reviewService) {
        this.recipeService = recipeService;
        this.userService = userService;
        this.reviewService = reviewService;
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
        System.out.println("The user is attempting to view recipe with id: " + recipeId);

        ModelAndView mv = new ModelAndView("recipes_page");

        String currUserId = userService.getLoggedInUser().getUserId();
        // Fetch the post from the database
        Recipe recipe = recipeService.getRecipeById(recipeId, currUserId); // Fetch the recipe by ID

        if (recipe == null) {
            System.out.println("No recipe found for the given recipeId: " + recipeId);
        } else {
            System.out.println("Recipe retrieved: " + recipe.getRecipeId() + "with name: " +  recipe.getRecipeName() + "with description: " + recipe.getDescription());
        }

        // Fetch the reviews for the recipe
        /* List<Review> reviews = reviewService.getReviewById(reviewId);
        System.out.println("Review list: " + reviews);

        // Pass the recipe and reviews directly to the view
        mv.addObject("recipes", reviews); */
        /** NEED CHANGE */

        // If an error occurred, you can set the following property with the error message
        String errorMessage = error;
        mv.addObject("errorMessage", errorMessage);

        return mv;
    }


    @GetMapping("/{recipeId}/bookmark/{isAdd}/{bookmarkType}")
    public String addOrRemoveBookmark(@PathVariable("recipeId") String recipeId,
            @PathVariable("isAdd") Boolean isAdd, @PathVariable("bookmarkType") String bookmarkType) {
        System.out.println("The user is attempting add or remove a bookmark:");
        System.out.println("\trecipeId: " + recipeId);
        System.out.println("\tisAdd: " + isAdd);
        System.out.println("\tbookmarkType: " + bookmarkType);

        // Redirect the user if the comment adding is a success.
        try {
            String currUserId = userService.getLoggedInUser().getUserId();
            if (isAdd) {
                recipeService.bookmark(currUserId, recipeId, bookmarkType);
            } else {
                recipeService.unBookmark(currUserId, recipeId, bookmarkType);
            }
            return "redirect:/recipe/" + recipeId;
        } catch (Exception e) {
            // Redirect the user with an error message if there was an error.
            String message = URLEncoder.encode("Failed to (un)bookmark the recipe. Please try again.",
                    StandardCharsets.UTF_8);
            return "redirect:/recipe/" + recipeId + "?error=" + message;
        }
    }

}
