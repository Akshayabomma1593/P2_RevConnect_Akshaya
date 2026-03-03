package com.rev.app.controller;

import com.rev.app.dto.RegisterDTO;
import com.rev.app.entity.User;
import com.rev.app.exception.UserAlreadyExistsException;
import com.rev.app.service.PasswordResetService;
import com.rev.app.service.UserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    public AuthController(UserService userService, PasswordResetService passwordResetService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/feed";
        }
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        if (error != null)
            model.addAttribute("errorMessage", "Invalid username/email or password.");
        if (logout != null)
            model.addAttribute("successMessage", "You have been logged out successfully.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        model.addAttribute("roles", User.UserRole.values());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerDTO") RegisterDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", User.UserRole.values());
            return "auth/register";
        }
        try {
            userService.register(dto);
            logger.info("AuthController: Registration successful for user: {}. Redirecting to login.",
                    dto.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Account created! Please log in.");
            return "redirect:/login";
        } catch (UserAlreadyExistsException ex) {
            logger.warn("AuthController: Registration failed - user already exists: {}", ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("roles", User.UserRole.values());
            return "auth/register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String usernameOrEmail, Model model) {
        try {
            String token = passwordResetService.createResetToken(usernameOrEmail);
            model.addAttribute("successMessage", "Reset token generated. Use it within "
                    + passwordResetService.getTokenTtlMinutes() + " minutes.");
            model.addAttribute("resetToken", token);
            model.addAttribute("usernameOrEmail", usernameOrEmail);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("usernameOrEmail", usernameOrEmail);
        }
        return "auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Password and confirm password must match.");
            model.addAttribute("token", token);
            return "auth/reset-password";
        }

        try {
            passwordResetService.resetPassword(token, password);
            redirectAttributes.addFlashAttribute("successMessage", "Password reset successful. Please sign in.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("token", token);
            return "auth/reset-password";
        }
    }
}
