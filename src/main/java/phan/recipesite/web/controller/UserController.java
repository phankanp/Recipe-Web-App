package phan.recipesite.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import phan.recipesite.model.User;
import phan.recipesite.model.validator.PasswordValidator;
import phan.recipesite.service.UserService;
import phan.recipesite.web.FlashMessage;

import javax.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;

    private final PasswordValidator passwordValidator;

    public UserController(UserService userService, PasswordValidator passwordValidator) {
        this.userService = userService;
        this.passwordValidator = passwordValidator;
    }

    // User sign up form
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signupForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }

        return "/signup";
    }

    // Add new user
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String addUser(@Valid User user, BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        passwordValidator.validate(user, result);

        if (result.hasErrors()) {
            return "signup";
        }

        userService.save(user);

        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Successfully created account! Please login."));

        return "redirect:/recipes";
    }

    // Login form
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String loginForm(Model model, String error, String logout) {
        model.addAttribute("user", new User());

        if (error != null) {
            model.addAttribute("error", "Your username or password is invalid.");
        }

        if (logout != null)
            model.addAttribute("logout", "You have been logged out successfully.");

        return "login";
    }

    // User profile
    @RequestMapping(value = "/userprofile", method = RequestMethod.GET)
    public String userProfile(Model model, Authentication authentication) {

        User user = userService.findByUsername(authentication.getName());

        model.addAttribute("user", user);

        return "profile";
    }

    // Access denies page
    @RequestMapping("/403")
    public String accessDenied() {
        return "403";
    }
}
