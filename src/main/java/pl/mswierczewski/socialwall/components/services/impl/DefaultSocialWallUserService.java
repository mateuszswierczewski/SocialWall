package pl.mswierczewski.socialwall.components.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;
import pl.mswierczewski.socialwall.components.models.SocialWallUserProfile;
import pl.mswierczewski.socialwall.components.repositories.SocialWallUserProfileRepository;
import pl.mswierczewski.socialwall.components.repositories.SocialWallUserRepository;
import pl.mswierczewski.socialwall.components.services.SocialWallUserService;
import pl.mswierczewski.socialwall.dtos.FileDto;
import pl.mswierczewski.socialwall.dtos.user.EditUserProfileRequest;
import pl.mswierczewski.socialwall.dtos.user.UserBasicInfo;
import pl.mswierczewski.socialwall.dtos.user.UserInfo;
import pl.mswierczewski.socialwall.exceptions.FileDownloadException;
import pl.mswierczewski.socialwall.exceptions.FileUploadException;
import pl.mswierczewski.socialwall.exceptions.NotFoundException;
import pl.mswierczewski.socialwall.exceptions.api.SocialWallUserNotFoundException;
import pl.mswierczewski.socialwall.mappers.UserMapper;
import pl.mswierczewski.socialwall.utils.storage.FileBuckets;
import pl.mswierczewski.socialwall.utils.storage.FileStorage;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.http.entity.ContentType.*;

/**
 * Default implementation of SocialWallUserService.
 */
@Service
public class DefaultSocialWallUserService implements SocialWallUserService {

    private final SocialWallUserRepository userRepository;
    private final SocialWallUserProfileRepository userProfileRepository;

    private final UserMapper userMapper;
    private final FileStorage fileStorage;

    private final List<String> supportedImageTypes = Arrays.asList(
            IMAGE_JPEG.getMimeType(),
            IMAGE_PNG.getMimeType(),
            IMAGE_GIF.getMimeType()
    );


    public DefaultSocialWallUserService(
            SocialWallUserRepository userRepository,
            SocialWallUserProfileRepository userProfileRepository,
            UserMapper userMapper, FileStorage fileStorage) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userMapper = userMapper;
        this.fileStorage = fileStorage;
    }

    /**
     * Searches for a user in the database based on the given username. If it doesn't find it then it looks in the email
     * column. If it fails again, it throws an exception.
     *
     * @param username - String
     * @return UserDetails
     * @throws UsernameNotFoundException - throws when user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found!", username)));
    }

    /**
     * Searches the database for a user based on the given user id.
     *
     * @param userId - String
     * @return SocialWallUser - the user that was found based on the user id
     * @throws SocialWallUserNotFoundException - throws when user not found
     */
    @Override
    public SocialWallUser getUserById(String userId) throws SocialWallUserNotFoundException {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new SocialWallUserNotFoundException(userId));
    }

    /**
     * Returns an information about user by given user id.
     *
     * @param userId - String of user id
     * @return UserInfo - information about user
     */
    @Override
    public UserInfo getUserInfo(String userId) {
        SocialWallUser user = getUserById(userId);
        return userMapper.mapUserToUserInfo(user);
    }

    /**
     * Searches the database to see if the user exists by given email.
     *
     * @param email - String
     * @return Boolean - true if exists, else false
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Searches the database to see if the user exists by given username.
     *
     * @param username - String
     * @return Boolean - true if exists, else false
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Searches the database to see if the user exists by given user id.
     *
     * @param userId - String
     * @return Boolean - true if exists, else false
     */
    @Override
    public boolean existsByUserId(String userId){
        return userRepository.existsById(userId);
    }

    /**
     * Saves given user in database.
     *
     * @param user - SocialWallUser to save
     * @return SocialWallUser after save with generated id
     */
    @Override
    public SocialWallUser save(SocialWallUser user) {
        return userRepository.save(user);
    }

    /**
     * Upload user profile image to the storage. Checks if given file is not empty and if it is an image with supported
     * type. Generates file name and path. Then sends image to the storage and updates user link to profile image.
     *
     * @param userId - String of user id
     * @param file - MultipartFile containing image
     * @throws FileUploadException - when file is not valid
     */
    @Override
    @Transactional
    public void uploadUserProfileImage(String userId, MultipartFile file) throws FileUploadException {
        // Checks if file is not empty
        if (file == null || file.isEmpty()) {
            throw FileUploadException.emptyFile("File is empty!");
        }

        // Checks if format is supported
        if (!supportedImageTypes.contains(file.getContentType())){
            String msg = String.format("%s file type is not supported!", file.getContentType());
            throw FileUploadException.unsupportedMediaType(msg);
        }

        // Generates path to image and filename
        String path = String.format("%s/%s", FileBuckets.PROFILES_IMAGES.getBucketName(), userId);
        String fileName = String.format("%s-%s", UUID.nameUUIDFromBytes(Objects.requireNonNull(file.getOriginalFilename()).getBytes()), UUID.randomUUID());

        // Saves image in storage
        fileStorage.save(path, fileName, file);

        // Updates user profile image link
        SocialWallUser user = getUserById(userId);
        user.getUserProfile().setProfileImageLink(fileName);
        save(user);
    }

    /**
     * Downloads user profile image. If user has no image, then throw NotFoundException
     *
     * @param userId - String
     * @return FileDto - with user profile image
     * @throws NotFoundException - when user has no image
     * @throws FileDownloadException - when an error occurred while downloading
     */
    @Override
    public FileDto downloadUserProfileImage(String userId) throws NotFoundException, FileDownloadException {
        // Gets user
        SocialWallUser user = getUserById(userId);

        // Creates path to image
        String path = String.format("%s/%s", FileBuckets.PROFILES_IMAGES.getBucketName(), userId);

        // Download and returns an image
        return user.getUserProfile().getProfileImageLink()
                .map(fileName -> fileStorage.download(path, fileName))
                .orElseThrow(() -> new NotFoundException(String.format("No profile image of user %s found", userId)));
    }

    /**
     * Returns a basic information about user by given user id.
     *
     * @param userId - String
     * @return basic user information
     */
    @Override
    public UserBasicInfo getUserBasicInfo(String userId) {
        SocialWallUser user = getUserById(userId);
        return userMapper.mapUserToBasicUserInfo(user);
    }

    /**
     * Adds selected user to the following list.
     *
     * @param userId - String of user id
     * @param followingUserId - String of user id that will be following
     */
    @Override
    public Boolean followUser(String userId, String followingUserId) {
        SocialWallUser user = getUserById(userId);
        SocialWallUser followingUser = getUserById(followingUserId);

        user.addFollowing(followingUser);
        save(user);

        return true;
    }

    @Override
    public Boolean unfollowUser(String userId, String unfollowingUserId) {
        SocialWallUser user = getUserById(userId);
        SocialWallUser unfollowingUser = getUserById(unfollowingUserId);

        user.removeFollowing(unfollowingUser);
        save(user);

        return true;
    }

    @Override
    public List<UserBasicInfo> getUserFollowers(String principalId, String userId) {
        SocialWallUser socialWallUser = getUserById(userId);
        return socialWallUser.getFollowers().stream()
                .map(user -> userMapper.mapUserToBasicUserInfo(principalId, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserBasicInfo> getUserFollowing(String principalId, String userId) {
        SocialWallUser socialWallUser = getUserById(userId);
        return socialWallUser.getFollowing().stream()
                .map(user -> userMapper.mapUserToBasicUserInfo(principalId, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserBasicInfo> findUsersByName(String name, int page, int pageSize) {
        Pageable pageRequest = PageRequest.of(page - 1, pageSize);

        Page<SocialWallUser> users = userRepository.findByUsernameContaining(name, pageRequest);

        return users.stream()
                .map(userMapper::mapUserToBasicUserInfo)
                .collect(Collectors.toList());
    }

    @Override
    public UserInfo editUserProfile(String userId, EditUserProfileRequest editUserProfileRequest) {
        SocialWallUser user = getUserById(userId);

        user.setUsername(editUserProfileRequest.getUsername());
        SocialWallUserProfile userProfile = user.getUserProfile();
        userProfile.setFirstName(editUserProfileRequest.getFirstName());
        userProfile.setLastName(editUserProfileRequest.getLastName());
        userProfile.setBirthDate(editUserProfileRequest.getBirthDate());
        editUserProfileRequest.getCity().ifPresent(userProfile::setCity);
        editUserProfileRequest.getCountry().ifPresent(userProfile::setCountry);
        editUserProfileRequest.getDescription().ifPresent(userProfile::setDescription);

        user = userRepository.save(user);

        return userMapper.mapUserToUserInfo(user);
    }

    @Override
    public Boolean isFollowing(String userId1, String userId2) {
        SocialWallUser user1 = getUserById(userId1);

        return user1.getFollowing().stream()
                .anyMatch(user -> user.getId().equals(userId2));
    }


}