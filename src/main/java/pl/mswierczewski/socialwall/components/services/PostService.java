package pl.mswierczewski.socialwall.components.services;

import pl.mswierczewski.socialwall.components.models.Post;
import pl.mswierczewski.socialwall.dtos.post.PostRequest;
import pl.mswierczewski.socialwall.dtos.post.PostResponse;
import pl.mswierczewski.socialwall.exceptions.api.EntityNotFoundException;

import java.util.List;

public interface PostService {
    PostResponse addNewPost(String userId, PostRequest images);

    Post getPostById(String postId) throws EntityNotFoundException;

    PostResponse getPostResponseById(String postId) throws EntityNotFoundException;

    void save(Post post);

    void deletePostById(String owner, String postId);

    List<PostResponse> getUserPosts(String userId, int page, int pageSize);

    List<PostResponse> getPostRecommendedForUser(String name, int page, int pageSize);
}
