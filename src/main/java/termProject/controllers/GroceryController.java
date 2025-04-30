package termProject.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import termProject.models.GroceryItem;
import termProject.models.User;
import termProject.services.GroceryListService;
import termProject.services.UserService;

@Controller
@RequestMapping("/groceryList")
public class GroceryController {
    @Autowired
    private GroceryListService groceryListService;
    private UserService userService;
    
    @Autowired
    public GroceryController(GroceryListService groceryListService, UserService userService) {
        this.groceryListService = groceryListService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView webpage() {
        ModelAndView mv = new ModelAndView("grocerylist_page");

       User loggedInUser = userService.getLoggedInUser();
        if (loggedInUser == null) {
            String errorMessage = "User is not logged in.";
            mv.addObject("errorMessage", errorMessage);
            return mv;
        }
        String loggedInUserId = loggedInUser.getUserId();
        try {
            List<GroceryItem> items = groceryListService.getGroceryList(loggedInUserId);
            if (items.isEmpty()) {
                // Enable the following line if you want to show no content message.
                // Do that if your content list is empty.
                mv.addObject("isNoContent", true);
            } else {
                mv.addObject("items", items);
            }
        } catch (Exception e) {
            String errorMessage = "An error occurred while fetching future recipes.";
            e.printStackTrace();
            mv.addObject("errorMessage", errorMessage);
            return mv;
        }
        return mv;
    }
}