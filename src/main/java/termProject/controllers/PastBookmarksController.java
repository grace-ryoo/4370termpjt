package termProject.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import termProject.models.Recipe;
import termProject.models.User;
import termProject.services.BookmarkService;
import termProject.services.UserService;


@Controller
@RequestMapping("/cookedChronicles")
public class PastBookmarksController {

    private final UserService userService;
    private final BookmarkService bookmarkService;
    
    @Autowired
    public PastBookmarksController(UserService userService, BookmarkService bookmarkService) {
        this.userService = userService;
        this.bookmarkService = bookmarkService;
    }


    /**
     * /bookmarks URL itself is handled by this.
     */
    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "error", required = false) String error) {
        // posts_page is a mustache template from src/main/resources/templates.
        // ModelAndView class enables initializing one and populating placeholders
        // in the template using Java objects assigned to named properties.
        ModelAndView mv = new ModelAndView("pastcookbook_page");
        

        /** Modified code starts here */
        User loggedInUser = userService.getLoggedInUser();
        if (loggedInUser == null) {
            String errorMessage = "User is not logged in.";
            mv.addObject("errorMessage", errorMessage);
            return mv;
        }
        String loggedInUserId = loggedInUser.getUserId();
        mv.addObject("username", loggedInUser.getFirstName());
        mv.addObject("bookmark_type", "PAST");
        try {
            List<Recipe> recipes = bookmarkService.getPastRecipes(loggedInUserId);
            if (recipes.isEmpty()) {
                // Enable the following line if you want to show no content message.
                // Do that if your content list is empty.
                mv.addObject("isNoContent", true);
            } else {
                mv.addObject("recipes", recipes);
            }
        } catch (Exception e) {
            String errorMessage = "An error occurred while fetching past recipes.";
            e.printStackTrace();
            mv.addObject("errorMessage", errorMessage);
            return mv;
        }
        
        

        // Following line populates sample data.
        // You should replace it with actual data from the database.
        // List<Post> posts = Utility.createSamplePostsListWithoutComments();
        // mv.addObject("posts", posts);

        // If an error occured, you can set the following property with the
        // error message to show the error message to the user.
        
        

        

        return mv;
    }

    
    
}
