package pl.mswierczewski.socialwall.controllers;

import com.amazonaws.services.xray.model.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.mswierczewski.socialwall.components.services.PostService;
import pl.mswierczewski.socialwall.dtos.post.PostRequest;
import pl.mswierczewski.socialwall.dtos.post.PostResponse;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> addNewPost(Principal principal, @Valid @ModelAttribute PostRequest request){
        PostResponse response = postService.addNewPost(principal.getName(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable String postId){
        PostResponse response = postService.getPostResponseById(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePostById(Principal principal, @PathVariable String postId) {
        postService.deletePostById(principal.getName(), postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Post deleted successfully!");
    }

    @GetMapping("/byUser/{userId}")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable String userId,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int pageSize) {
        List<PostResponse> response = postService.getUserPosts(userId, page, pageSize);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/forUser")
    public ResponseEntity<List<PostResponse>> getPostRecommendedForUser(Principal principal,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "25") int pageSize) {
        List<PostResponse> response = postService.getPostRecommendedForUser(principal.getName(), page, pageSize);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
