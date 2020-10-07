package pl.mswierczewski.socialwall.components.models;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.mswierczewski.socialwall.components.enums.SocialWallUserRole;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "usernameIndex", columnList = "username", unique = true),
                @Index(name = "emailIndex", columnList = "email", unique = true)
        })
public class SocialWallUser implements UserDetails, Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(nullable = false, unique = true)
    private String id;

    @Size(min = 2, max = 30)
    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @ElementCollection(targetClass = SocialWallUserRole.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", nullable = false))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private final Set<SocialWallUserRole> roles = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false, unique = true)
    private SocialWallUserProfile userProfile;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_user_id")
    )
    @LazyCollection(LazyCollectionOption.EXTRA)
    private final Set<SocialWallUser> following = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "following")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private final Set<SocialWallUser> followers = new HashSet<>();

    @Column
    private boolean isAccountNonExpired;

    @Column
    private boolean isAccountNonLocked;

    @Column
    private boolean isCredentialsNonExpired;

    @Column
    private boolean isEnabled;

    public SocialWallUser(String username, String password, String email, SocialWallUserRole role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.isEnabled = false;
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.isCredentialsNonExpired = true;
        addRole(role);
    }

    public SocialWallUser() {

    }

    public void addRole(SocialWallUserRole role){
        roles.add(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserProfile(SocialWallUserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public SocialWallUserProfile getUserProfile() {
        return userProfile;
    }

    public Set<SocialWallUser> getFollowing() {
        return following;
    }

    public void addFollowing(SocialWallUser following) {
        this.following.add(following);
    }

    public Set<SocialWallUser> getFollowers() {
        return followers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SocialWallUser)) return false;
        SocialWallUser user = (SocialWallUser) o;
        return isAccountNonExpired() == user.isAccountNonExpired() &&
                isAccountNonLocked() == user.isAccountNonLocked() &&
                isCredentialsNonExpired() == user.isCredentialsNonExpired() &&
                isEnabled() == user.isEnabled() &&
                getId().equals(user.getId()) &&
                getUsername().equals(user.getUsername()) &&
                getPassword().equals(user.getPassword()) &&
                getEmail().equals(user.getEmail()) &&
                roles.equals(user.roles) &&
                getUserProfile().equals(user.getUserProfile());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getPassword(), getEmail(), roles, isAccountNonExpired(), isAccountNonLocked(), isCredentialsNonExpired(), isEnabled());
    }

    @Override
    public String toString() {
        return "SocialWallUser{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", isAccountNonExpired=" + isAccountNonExpired +
                ", isAccountNonLocked=" + isAccountNonLocked +
                ", isCredentialsNonExpired=" + isCredentialsNonExpired +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
