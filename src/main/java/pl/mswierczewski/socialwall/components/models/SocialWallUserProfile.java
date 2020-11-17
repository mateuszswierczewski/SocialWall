package pl.mswierczewski.socialwall.components.models;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;
import pl.mswierczewski.socialwall.components.enums.Gender;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "user_profiles")
public class SocialWallUserProfile {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    //optionals fields

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String country;

    @Column
    private String description;

    @Column
    private String profileImageLink;

    //relations

    @OneToOne(mappedBy = "userProfile")
    private SocialWallUser user;

    public SocialWallUserProfile(String firstName, String lastName, LocalDate birthDate, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public SocialWallUserProfile(){

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUser(SocialWallUser user) {
        this.user = user;
    }

    public String getId() {
        return id;
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

    public Optional<String> getCity() {
        return Optional.ofNullable(city);
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Optional<String> getCountry() {
        return Optional.ofNullable(country);
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Optional<String> getProfileImageLink() {
        return Optional.ofNullable(profileImageLink);
    }

    public void setProfileImageLink(String profileImageLink) {
        this.profileImageLink = profileImageLink;
    }

    public Integer getAge(){
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SocialWallUserProfile)) return false;
        SocialWallUserProfile that = (SocialWallUserProfile) o;
        return getId().equals(that.getId()) &&
                getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getBirthDate().equals(that.getBirthDate()) &&
                getGender() == that.getGender() &&
                Objects.equals(getCity(), that.getCity()) &&
                Objects.equals(getCountry(), that.getCountry()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(getProfileImageLink(), that.getProfileImageLink()) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstName(), getLastName(), getBirthDate(), getGender(), getCity(), getCountry(), getDescription(), getProfileImageLink());
    }
}
