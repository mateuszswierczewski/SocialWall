package pl.mswierczewski.socialwall.components.models;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.mswierczewski.socialwall.components.enums.SocialWallUserRole;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "users",
        indexes = {@Index(name = "usernameIndex", columnList = "username", unique = true),
                   @Index(name = "emailIndex", columnList = "email", unique = true)
        })
public class SocialWallUser implements UserDetails, Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(nullable = false)
    private String id;

    @Column(unique = true, length = 30, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, length = 50, nullable = false)
    private String email;

    @ElementCollection(targetClass = SocialWallUserRole.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private final Set<SocialWallUserRole> roles = new HashSet<>();


    private boolean isAccountNonExpired;

    private boolean isAccountNonLocked;

    private boolean isCredentialsNonExpired;

    private boolean isEnabled;

    public SocialWallUser(String username, String password, String email, SocialWallUserRole role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.isEnabled = false;
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
        //return isAccountNonExpired;
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //return isAccountNonLocked;
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        //return isCredentialsNonExpired;
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
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
