package pl.mswierczewski.socialwall.exceptions.api;

import org.springframework.http.HttpStatus;

public class SocialWallUserNotFoundException extends ApiException {

    public static final HttpStatus status = HttpStatus.NOT_FOUND;

    public SocialWallUserNotFoundException(String userId) {
        super(String.format("User %s not found!", userId), status);
    }

    public SocialWallUserNotFoundException(){
        super("User not found!", status);
    }

}
