package pl.mswierczewski.socialwall.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.mswierczewski.socialwall.components.services.SocialWallUserService;
import pl.mswierczewski.socialwall.dtos.FileDto;
import pl.mswierczewski.socialwall.dtos.user.UserBasicInfo;
import pl.mswierczewski.socialwall.dtos.user.UserInfo;

import java.security.Principal;

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
    public ResponseEntity<String> uploadUserProfileImage(Principal principal, @RequestParam("file") MultipartFile file){
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
    public ResponseEntity<String> followUser(Principal principal, @PathVariable String followingUserId){
        userService.followUser(principal.getName(), followingUserId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Successfully added user to following list!");
    }

}
