package com.rev.app.controller;

import com.rev.app.entity.BusinessPage;
import com.rev.app.entity.User;
import com.rev.app.service.BusinessPageService;
import com.rev.app.service.NotificationService;
import com.rev.app.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/business")
public class BusinessPageController {

    private final BusinessPageService businessPageService;
    private final UserService userService;
    private final NotificationService notificationService;

    public BusinessPageController(BusinessPageService businessPageService,
            UserService userService,
            NotificationService notificationService) {
        this.businessPageService = businessPageService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/page")
    public String editPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        if (currentUser.getRole() == User.UserRole.PERSONAL) {
            return "redirect:/feed";
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        model.addAttribute("page", businessPageService.getOrCreate(currentUser));
        return "business/page-edit";
    }

    @PostMapping("/page")
    public String savePage(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String pageName,
            @RequestParam(required = false) String about,
            @RequestParam(required = false) String contactEmail,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String hours,
            @RequestParam(required = false) String website,
            @RequestParam(required = false) String socialLinks,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        if (currentUser.getRole() == User.UserRole.PERSONAL) {
            return "redirect:/feed";
        }
        BusinessPage page = businessPageService.getOrCreate(currentUser);
        page.setPageName(pageName);
        page.setAbout(about);
        page.setContactEmail(contactEmail);
        page.setContactPhone(contactPhone);
        page.setAddress(address);
        page.setHours(hours);
        page.setWebsite(website);
        page.setSocialLinks(socialLinks);
        businessPageService.save(page);
        redirectAttributes.addFlashAttribute("successMessage", "Business page updated.");
        return "redirect:/business/page";
    }

    @GetMapping("/{username}")
    public String viewPage(@PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        model.addAttribute("page", businessPageService.findByUsername(username));
        return "business/page-view";
    }
}
