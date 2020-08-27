package pl.mswierczewski.socialwall.exceptions;

public class SocialWallUserNotFoundException extends RuntimeException {

    public SocialWallUserNotFoundException(String userId) {
        super(String.format("User %s not found!", userId));
    }

}
