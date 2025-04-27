package termProject.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import termProject.models.Recipe;
import termProject.services.RecipeService;
import termProject.services.UserService;

@Controller
@RequestMapping
public class HomeController {
    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserService userService;

    @Autowired
    public HomeController(RecipeService recipeService, UserService userService) {
        this.recipeService = recipeService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("home_page");

        String currentUserId = userService.getLoggedInUser().getUserId();
        
        List<Recipe> recipes = null;
        mv.addObject("recipes", recipes); /** NEED ADD HERE */

        if (recipes.isEmpty()) {
            mv.addObject("isNoContent", true);
        }

        mv.addObject("errorMessage", error);
        return mv;
    }

    @PostMapping("/createrecipe")
    public String createPost(@RequestParam(name = "description") String description, @RequestParam(name = "recipeName") String recipeName, @RequestParam(name = "userId") String userId, @RequestParam(name = "categoryId") String categoryId, @RequestParam(name = "prep_time") int prep_time, @RequestParam(name = "cook_time") int cook_time, @RequestParam(name = "servings") int servings, @RequestParam(name = "cuisineId") String cuisineId, @RequestParam(name = "dietId") String dietId, @RequestParam(name = "cookingLevel") String cookingLevel) {
        System.out.println("User is creating " + recipeName + " recipe:" + description);

        String currentUserId = userService.getLoggedInUser().getUserId();

        if (recipeName == null || recipeName.trim().isEmpty() || description == null || description.trim().isEmpty() || currentUserId == null || currentUserId.isEmpty() || categoryId == null || categoryId.isEmpty() || prep_time <= 0 || cook_time <= 0 || servings <= 0 || cuisineId == null || cuisineId.isEmpty() || dietId == null || dietId.isEmpty() || cookingLevel == null || cookingLevel.trim().isEmpty()) {
            return "redirect:/?error=Recipe cannot be empty";
        }

        boolean success = recipeService.createRecipe(recipeName, description, userId, categoryId, prep_time, cook_time, servings, cuisineId, dietId, cookingLevel);

        if (!success) {
            return "redirect:/?error=Failed to create the recipe. Please try again.";
        }

        return "redirect:/";
    }
}
