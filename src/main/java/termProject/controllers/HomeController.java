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
    public String createPost(@RequestParam(name = "description") String description, @RequestParam(name = "recipeName") String recipeName) {
        System.out.println("User is creating " + recipeName + " recipe:" + description);

        String currentUserId = userService.getLoggedInUser().getUserId();

        if (recipeName == null || recipeName.trim().isEmpty() || description == null || description.trim().isEmpty()) {
            return "redirect:/?error=Recipe cannot be empty";
        }

        boolean success = recipeService.createRecipe(recipeName, description, currentUserId);

        if (!success) {
            return "redirect:/?error=Failed to create the recipe. Please try again.";
        }

        return "redirect:/";
    }
}
