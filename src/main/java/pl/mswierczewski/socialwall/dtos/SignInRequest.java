package pl.mswierczewski.socialwall.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SignInRequest {

    @NotNull
    @NotEmpty
    @Size(max = 30)
    private String username;

    @NotNull
    @NotEmpty
    private String password;

    public SignInRequest(String usernameOrEmail, String password) {
        this.username = usernameOrEmail;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
