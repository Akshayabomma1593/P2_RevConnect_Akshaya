package com.rev.app.rest;

import com.rev.app.dto.ApiPostSummary;
import com.rev.app.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    private final PostService postService;
    private final com.rev.app.service.UserService userService;

    public PostRestController(PostService postService, com.rev.app.service.UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping
    public List<ApiPostSummary> getAllPosts() {
        return postService.findAll().stream()
                .map(ApiPostSummary::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiPostSummary> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiPostSummary.fromEntity(postService.findById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        com.rev.app.entity.User currentUser = userService.findByUsername(userDetails.getUsername());
        postService.deletePost(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
