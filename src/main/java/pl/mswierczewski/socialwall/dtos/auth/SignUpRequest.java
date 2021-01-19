package pl.mswierczewski.socialwall.dtos.auth;

import pl.mswierczewski.socialwall.components.enums.Gender;

import javax.validation.constraints.*;
import java.time.LocalDate;

public class SignUpRequest {

    @NotEmpty
    @Size(max = 30)
    private String username;

    @NotEmpty
    private String password;

    @NotEmpty
    @Email(message = "Email address is not valid!")
    @Size(max = 50)
    private String email;

    @NotEmpty
    @Size(max = 50)
    private String firstName;

    @NotEmpty
    @Size(max = 50)
    private String lastName;

    @Past
    private LocalDate birthDate;

    @NotNull
    private Gender gender;

    public SignUpRequest(String username, String password,
                         String email, String firstName,
                         String lastName, LocalDate birthDate,
                         Gender gender) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public SignUpRequest(){

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
