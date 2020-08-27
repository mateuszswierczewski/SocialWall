package pl.mswierczewski.socialwall.exceptions;

public class SocialWallBadCredentialsException extends RuntimeException{

    public static final SocialWallBadCredentialsException BAD_USERNAME_OR_EMAIL = create(
            "Username or email is incorrect!", "usernameOrEmail"
    );

    public static final SocialWallBadCredentialsException BAD_PASSWORD = create(
            "Password is incorrect!", "password"
    );

    private final String wrongField;

    private SocialWallBadCredentialsException(String msg, String wrongField){
        super(msg);
        this.wrongField = wrongField;
    }

    public static SocialWallBadCredentialsException create(String msg, String wrongField){
        return new SocialWallBadCredentialsException(msg, wrongField);
    }

    public String getWrongField() {
        return wrongField;
    }
}
