package com.rev.app.controller;

import com.rev.app.entity.User;
import com.rev.app.service.NotificationService;
import com.rev.app.service.ProductService;
import com.rev.app.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/products")
public class ProductController {
    private static final Logger logger = LogManager.getLogger(ProductController.class);

    private final ProductService productService;
    private final UserService userService;
    private final NotificationService notificationService;

    public ProductController(ProductService productService,
            UserService userService,
            NotificationService notificationService) {
        this.productService = productService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/manage")
    public String manage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        if (currentUser.getRole() != User.UserRole.BUSINESS) {
            return "redirect:/feed";
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        model.addAttribute("products", productService.getAllProductsForOwner(currentUser.getId()));
        return "business/products-manage";
    }

    @PostMapping("/create")
    public String create(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) String link,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        if (currentUser.getRole() != User.UserRole.BUSINESS) {
            return "redirect:/feed";
        }
        try {
            productService.create(currentUser, name, description, price, link);
            redirectAttributes.addFlashAttribute("successMessage", "Product/service added.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            logger.error("Failed to create product for user {}", currentUser.getUsername(), ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to add product right now. Please try again.");
        }
        return "redirect:/products/manage";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        if (currentUser.getRole() != User.UserRole.BUSINESS) {
            return "redirect:/feed";
        }
        productService.deactivate(id, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Product/service archived.");
        return "redirect:/products/manage";
    }
}
