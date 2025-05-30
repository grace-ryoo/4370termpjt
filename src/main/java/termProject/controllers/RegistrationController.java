package termProject.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import termProject.services.UserService;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    /**
     * See notes in AuthInterceptor.java regarding how this works through
     * dependency injection and inversion of control.
     */
    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * This function serves the /register page. See notes from
     * LoginController.java.
     */
    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("registration_page");

        // If an error occured, you can set the following property with the
        // error message to show the error message to the user.
        mv.addObject("errorMessage", error);

        return mv;
    }

    /**
     * This handles user registration form submissions. See notes from
     * LoginController.java.
     */
    @PostMapping
    public String register(@RequestParam("username") String username,
    @RequestParam("password") String password,
    @RequestParam("firstName") String firstName,
    @RequestParam("lastName") String lastName,
    @RequestParam("email") String email) throws UnsupportedEncodingException {
        // Passwords should have at least 3 chars.
        if (password.trim().length() < 3) {
            // If the password is too short redirect to the registration page
            // with an error message.
            String message = URLEncoder.encode("Passwords should have at least 3 nonempty letters.", "UTF-8");
            return "redirect:/register?error=" + message;
        }
        try {
            boolean registrationSuccess = userService.registerUser(username,
                    password, firstName, lastName, email);
            if (registrationSuccess) {
                // If the registration worked redirect to the login page.
                return "redirect:/login";
            } else {
                // If the registration fails redirect to registration page with a message.
                String message = URLEncoder
                        .encode("Registration failed. Please try again.", "UTF-8");
                return "redirect:/register?error=" + message;
            }
        } catch (Exception e) {
            // If the registration fails redirect to registration page with a message.
            String message = URLEncoder
                    .encode("An error occurred: " + e.getMessage(), "UTF-8");
            return "redirect:/register?error=" + message;
        }
    }

}
