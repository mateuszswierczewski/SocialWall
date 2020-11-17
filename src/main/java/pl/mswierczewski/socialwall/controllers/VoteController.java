package pl.mswierczewski.socialwall.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.mswierczewski.socialwall.components.services.VoteService;
import pl.mswierczewski.socialwall.dtos.vote.VoteRequest;
import pl.mswierczewski.socialwall.dtos.vote.VoteResponse;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping
    public ResponseEntity<VoteResponse> addVote(Principal principal, @Valid @RequestBody VoteRequest request) {
         VoteResponse response = voteService.addVote(principal.getName(), request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{voteId}")
    public ResponseEntity<String> deleteVote(Principal principal, @PathVariable String voteId) {
        voteService.deleteVote(principal.getName(), voteId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Vote deleted successfully!");
    }

    @GetMapping("/byPost/{postId}")
    public ResponseEntity<List<VoteResponse>> getVotesByPost(@PathVariable String postId) {
        List<VoteResponse> response = voteService.getVotesByPost(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
