package com.rev.app.controller;

import com.rev.app.entity.Connection;
import com.rev.app.entity.User;
import com.rev.app.service.ConnectionService;
import com.rev.app.service.NotificationService;
import com.rev.app.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/connections")
public class ConnectionController {

    private static final Logger logger = LogManager.getLogger(ConnectionController.class);

    private final ConnectionService connectionService;
    private final UserService userService;
    private final NotificationService notificationService;

    public ConnectionController(ConnectionService connectionService,
            UserService userService,
            NotificationService notificationService) {
        this.connectionService = connectionService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String connections(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        if (currentUser.getRole() != User.UserRole.PERSONAL) {
            return "redirect:/feed";
        }

        List<User> connections = connectionService.getConnections(currentUser);
        List<Connection> pendingReceived = connectionService.getPendingReceived(currentUser);
        List<Connection> pendingSent = connectionService.getPendingSent(currentUser);

        model.addAttribute("connections", connections);
        model.addAttribute("pendingReceived", pendingReceived);
        model.addAttribute("pendingSent", pendingSent);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        return "connections";
    }

    @PostMapping("/request/{userId}")
    public String sendRequest(@PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        User target = userService.findById(userId);
        if (currentUser.getRole() != User.UserRole.PERSONAL || target.getRole() != User.UserRole.PERSONAL) {
            redirectAttributes.addFlashAttribute("errorMessage", "Connections are allowed only between personal accounts.");
            return "redirect:/profile/" + target.getUsername();
        }
        try {
            connectionService.sendRequest(currentUser, target);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Connection request sent to " + target.getUsername());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile/" + target.getUsername();
    }

    @PostMapping("/{connectionId}/accept")
    public String accept(@PathVariable Long connectionId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        if (currentUser.getRole() != User.UserRole.PERSONAL) {
            redirectAttributes.addFlashAttribute("errorMessage", "Connections are available only for personal accounts.");
            return "redirect:/feed";
        }
        connectionService.acceptRequest(connectionId, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Connection accepted!");
        return "redirect:/connections";
    }

    @PostMapping("/{connectionId}/reject")
    public String reject(@PathVariable Long connectionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        if (currentUser.getRole() != User.UserRole.PERSONAL) {
            return "redirect:/feed";
        }
        connectionService.rejectRequest(connectionId, currentUser);
        return "redirect:/connections";
    }

    @PostMapping("/remove/{targetUserId}")
    public String remove(@PathVariable Long targetUserId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        if (currentUser.getRole() != User.UserRole.PERSONAL) {
            redirectAttributes.addFlashAttribute("errorMessage", "Connections are available only for personal accounts.");
            return "redirect:/feed";
        }
        User target = userService.findById(targetUserId);
        connectionService.removeByUsers(currentUser, target);
        redirectAttributes.addFlashAttribute("successMessage", "Connection removed.");
        return "redirect:/connections";
    }
}
