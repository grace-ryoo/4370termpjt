package termProject.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import termProject.models.Recipe;
import termProject.services.ProfileService;
import termProject.services.UserService;

/**
 * Handles /profile URL and its sub URLs.
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {
    
    // UserService has user login and registration related functions.
    private final UserService userService;
    private final ProfileService profileService;

    /**
     * See notes in AuthInterceptor.java regarding how this works 
     * through dependency injection and inversion of control.
     */
    @Autowired
    public ProfileController(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }
    /**
     * This function handles /profile URL itself.
     * This serves the webpage that shows recipe of the logged in user.
     */
    @GetMapping
    public ModelAndView profileOfLoggedInUser() {
        System.out.println("User is attempting to view profile of the logged in user.");
        return profileOfSpecificUser(userService.getLoggedInUser().getUserId());
    }

    /**
     * This function handles /profile/{userId} URL.
     * This serves the webpage that shows posts of a speific user given by userId.
     * See comments in PeopleController.java in followUnfollowUser function regarding 
     * how path variables work.
     */
    @GetMapping("/{userId}")
    public ModelAndView profileOfSpecificUser(@PathVariable("userId") String userId) {
        System.out.println("User is attempting to view profile: " + userId);
        
        // See notes on ModelAndView in BookmarksController.java.
        ModelAndView mv = new ModelAndView("recipe_page");
        

        // Following line populates sample data.
        // You should replace it with actual data from the database.
        // List<Recipe> recipe = Utility.createSampleRecipesListWithoutComments();
        if (userId == null || userId.trim().isEmpty()) {
            String errorMessage = "User ID is null or empty.";
            mv.addObject("errorMessage", errorMessage);
        }
        try {
            List<Recipe> recipes = profileService.getRecipeBySpecificUser(userId);

            if (recipes.isEmpty()) {
                // Enable the following line if you want to show no content message.
                // Do that if your content list is empty.
                mv.addObject("isNoContent", true);
            } else {
                mv.addObject("recipes", recipes);
            }
        } catch (Exception e) {
            String errorMessage = "An error occurred while fetching recipes by User ID.";
            e.printStackTrace();
            mv.addObject("errorMessage", errorMessage);
        }
        return mv;
    }
}