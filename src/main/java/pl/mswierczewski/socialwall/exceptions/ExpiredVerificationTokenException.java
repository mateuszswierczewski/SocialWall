package pl.mswierczewski.socialwall.exceptions;

public class ExpiredVerificationTokenException extends RuntimeException{

    public ExpiredVerificationTokenException(String msg){
        super(msg);
    }
}
