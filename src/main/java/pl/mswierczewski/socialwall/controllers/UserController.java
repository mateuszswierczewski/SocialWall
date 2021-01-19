package pl.mswierczewski.socialwall.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.services.SocialWallUserService;
import pl.mswierczewski.socialwall.dtos.FileDto;
import pl.mswierczewski.socialwall.dtos.user.EditUserProfileRequest;
import pl.mswierczewski.socialwall.dtos.user.UserBasicInfo;
import pl.mswierczewski.socialwall.dtos.user.UserInfo;

import java.security.Principal;
import java.util.List;

/**
 * Controller created to manage user data.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final SocialWallUserService userService;

    public UserController(SocialWallUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable String userId){
        UserInfo userInfo = userService.getUserInfo(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userInfo);
    }

    @GetMapping("/currentUser")
    public ResponseEntity<UserInfo> getCurrentSignedInUserInfo(Principal principal){
        UserInfo userInfo = userService.getUserInfo(principal.getName());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userInfo);
    }

    @GetMapping("/basicInfo/{userId}")
    public ResponseEntity<UserBasicInfo> getUserBasicInfo(@PathVariable String userId){
        UserBasicInfo userBasicInfo = userService.getUserBasicInfo(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userBasicInfo);
    }


    @PostMapping("/profileImage/upload")
    public ResponseEntity<String> uploadUserProfileImage(Principal principal, @RequestParam("photo") MultipartFile file){
        userService.uploadUserProfileImage(principal.getName(), file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Image upload successfully");
    }

    @GetMapping("/profileImage/download/{userId}")
    public ResponseEntity<byte[]> downloadUserProfileImage(@PathVariable String userId){
        FileDto image = userService.downloadUserProfileImage(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(image.getContentType())
                .body(image.getFile());
    }

    @PostMapping("/follow/{followingUserId}")
    public ResponseEntity<Boolean> followUser(Principal principal, @PathVariable String followingUserId){
        Boolean result = userService.followUser(principal.getName(), followingUserId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @PostMapping("/unfollow/{unfollowingUserId}")
    public ResponseEntity<Boolean> unfollowUser(Principal principal, @PathVariable String unfollowingUserId){
        Boolean result = userService.unfollowUser(principal.getName(), unfollowingUserId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<UserBasicInfo>> getUserFollowers(Principal principal, @PathVariable String userId) {
        List<UserBasicInfo> userFollowers = userService.getUserFollowers(principal.getName(), userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userFollowers);
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<List<UserBasicInfo>> getUserFollowing(Principal principal, @PathVariable String userId) {
        List<UserBasicInfo> userFollowing = userService.getUserFollowing(principal.getName(), userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userFollowing);
    }

    @GetMapping("/isFollowing/{userId}")
    public ResponseEntity<Boolean> isUserFollow(Principal principal, @PathVariable String userId) {
        Boolean isFollowing = userService.isFollowing(principal.getName(), userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(isFollowing);
    }

    @GetMapping("/findBy")
    public ResponseEntity<List<UserBasicInfo>> findUserByName(@RequestParam String name,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "25") int pageSize) {
        List<UserBasicInfo> users = userService.findUsersByName(name, page, pageSize);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @PostMapping("/editProfile")
    public ResponseEntity<UserInfo> editUserProfile(Principal principal, @RequestBody EditUserProfileRequest request){
        UserInfo response = userService.editUserProfile(principal.getName(), request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
