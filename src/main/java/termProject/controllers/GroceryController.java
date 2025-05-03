package termProject.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import termProject.models.GroceryItem;
import termProject.models.User;
import termProject.services.GroceryListService;
import termProject.services.UserService;

@Controller
@RequestMapping("/groceryList")
public class GroceryController {

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

        // Get current user
        if (userService.getLoggedInUser() == null) {
            return new ModelAndView("redirect:/login");
        }
        User user = userService.getLoggedInUser();
        mv.addObject("username", user.getFirstName());

        String userId = userService.getLoggedInUser().getUserId();
        try {
            List<GroceryItem> items = groceryListService.getGroceryList(userId);

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

    @PostMapping("/add")
    public String addGroceryItem(@RequestParam("itemName") String itemName, @RequestParam("itemQuantity") int itemQuantity) {
        if (userService.getLoggedInUser() == null) {
            return "redirect:/login";
        }

        String userId = userService.getLoggedInUser().getUserId();

        try {
            groceryListService.addGroceryItem(userId, itemName, itemQuantity);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/groceryList?error=Failed to add item.";
        }

        return "redirect:/groceryList";
    }

    @PostMapping("/delete")
    public String deleteGroceryItem(@RequestParam("itemId") int itemId) {
        if (userService.getLoggedInUser() == null) {
            return "redirect:/login";
        }

        String userId = userService.getLoggedInUser().getUserId();

        try {
            groceryListService.deleteGroceryItem(userId, itemId);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/groceryList?error=Failed to delete item.";
        }

        return "redirect:/groceryList";
    }

    @PostMapping("/mark")
    public String markItemAsBought(@RequestParam("itemId") int itemId,
            @RequestParam(value = "isBought", required = false) String isBought) {
        if (userService.getLoggedInUser() == null) {
            return "redirect:/login";
        }

        String userId = userService.getLoggedInUser().getUserId();
        boolean shouldBeMarked = (isBought != null); // Checkbox sends value only if checked

        try {
            groceryListService.setItemBoughtStatus(userId, itemId, shouldBeMarked);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/groceryList?error=Failed to update item status.";
        }

        return "redirect:/groceryList";
    }

}
