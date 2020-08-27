package pl.mswierczewski.socialwall.exceptions;

public class UserAlreadyExistException extends RuntimeException{

    private static final String DEFAULT_MSG = "User already exists!";

    private boolean usernameExist;
    private boolean emailExist;

    public UserAlreadyExistException(boolean usernameExist, boolean emailExists){
        this(DEFAULT_MSG, usernameExist, emailExists);
    }

    public UserAlreadyExistException(String msg, boolean usernameExist, boolean emailExist){
        this(createMessage(msg, usernameExist, emailExist));
        this.usernameExist = usernameExist;
        this.emailExist = emailExist;

    }

    private UserAlreadyExistException(String msg){
        super(msg);
    }

    private static String createMessage(String msg, boolean usernameExist, boolean emailExist){
        if (usernameExist) {
            msg += "\nUsername is already taken!";
        }
        if (emailExist) {
            msg += "\nEmail is already taken";
        }
        return msg;
    }

    public boolean isUsernameExists() {
        return usernameExist;
    }

    public boolean isEmailExists() {
        return emailExist;
    }
}
