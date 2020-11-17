package pl.mswierczewski.socialwall.components.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mswierczewski.socialwall.components.enums.PostType;
import pl.mswierczewski.socialwall.components.models.Post;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.repositories.PostRepository;
import pl.mswierczewski.socialwall.components.services.PostService;
import pl.mswierczewski.socialwall.components.services.SocialWallUserService;
import pl.mswierczewski.socialwall.dtos.post.PostRequest;
import pl.mswierczewski.socialwall.dtos.post.PostResponse;
import pl.mswierczewski.socialwall.exceptions.api.BadRequestException;
import pl.mswierczewski.socialwall.exceptions.api.EntityNotFoundException;
import pl.mswierczewski.socialwall.exceptions.FileUploadException;
import pl.mswierczewski.socialwall.exceptions.api.RequestForbiddenException;
import pl.mswierczewski.socialwall.mappers.PostMapper;
import pl.mswierczewski.socialwall.utils.storage.FileBuckets;
import pl.mswierczewski.socialwall.utils.storage.FileStorage;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.http.entity.ContentType.*;

@Service
public class DefaultPostService implements PostService {

    private final PostRepository postRepository;

    private final SocialWallUserService userService;

    private final FileStorage fileStorage;

    private final PostMapper postMapper;

    private final List<String> supportedImageTypes = Arrays.asList(
            IMAGE_JPEG.getMimeType(),
            IMAGE_PNG.getMimeType(),
            IMAGE_GIF.getMimeType()
    );

    public DefaultPostService(PostRepository postRepository, SocialWallUserService userService, FileStorage fileStorage, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.fileStorage = fileStorage;
        this.postMapper = postMapper;
    }



    @Override
    public Post getPostById(String postId){
        return postRepository
                .findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post %s not found!", postId)));
    }

    @Override
    public PostResponse getPostResponseById(String postId){
        Post post = getPostById(postId);
        return postMapper.mapPostToPostResponse(post);
    }

    @Override
    public void save(Post post) {
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePostById(String owner, String postId){
        Post post = getPostById(postId);

        if (post.getAuthor().getId().equals(owner)) {
            if (post.getPostType().equals(PostType.IMAGE) || post.getPostType().equals(PostType.TEXT_IMAGE)) {
                String path = String.format(
                        "%s/%s",
                        FileBuckets.POSTS_IMAGES.getBucketName(),
                        post.getId()
                );

                fileStorage.deleteFiles(path, post.getImagesLinks().orElse(null));
            }

            postRepository.deleteById(postId);

        } else {
            String msg = String.format("User %s does not have permission to delete post %s", owner, postId);
            throw new RequestForbiddenException(msg);
        }
    }

    @Override
    @Transactional
    public List<PostResponse> getUserPosts(String userId, int page, int pageSize) {
        SocialWallUser author = userService.getUserById(userId);
        Pageable pageRequest = PageRequest.of(page - 1, pageSize, Sort.by("createdDateTime").descending());

        Page<Post> posts = postRepository.findAllByAuthor(author, pageRequest);

        return posts.stream()
                .map(postMapper::mapPostToPostResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostResponse addNewPost(String userId, PostRequest request) {
        if (request.getTextContent().isEmpty() && request.getImages().isEmpty()){
            throw new BadRequestException("Post must contain any text or at least one image!");
        }

        // Files validation
        request.getImages().ifPresent(
                files -> files.forEach(
                            file -> {
                                // Checks if file is not empty or null
                                if (file == null || file.isEmpty()){
                                    String msg = String.format("File %s is empty", Objects.requireNonNull(file).getOriginalFilename());
                                    throw FileUploadException.emptyFile(msg);
                                }

                                // Check if file has proper file type
                                if (!supportedImageTypes.contains(file.getContentType())){
                                    String msg = String.format("%s file type is not supported!", file.getContentType());
                                    throw FileUploadException.unsupportedMediaType(msg);
                                }
                            }
                )
        );

        SocialWallUser author = userService.getUserById(userId);

        Post post = postMapper.mapPostRequestToPost(author, request);
        Post savedPost = postRepository.save(post);

        request.getImages().ifPresent(
                file -> {
                    file.forEach(
                            image -> {
                                String path = String.format(
                                        "%s/%s",
                                        FileBuckets.POSTS_IMAGES.getBucketName(),
                                        savedPost.getId()
                                );
                                String fileName = String.format(
                                        "%s-%s",
                                        UUID.nameUUIDFromBytes(Objects.requireNonNull(image.getOriginalFilename()).getBytes()),
                                        UUID.randomUUID()
                                );
                                fileStorage.save(path, fileName, image);
                                savedPost.addImageLink(fileName);
                            }
                    );

                    postRepository.save(savedPost);
                }
        );

        return postMapper.mapPostToPostResponse(savedPost);
    }


}
