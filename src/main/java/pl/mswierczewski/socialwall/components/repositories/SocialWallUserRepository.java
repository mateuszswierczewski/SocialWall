package pl.mswierczewski.socialwall.components.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

@Repository
public interface SocialWallUserRepository extends JpaRepository<SocialWallUser, String> {

    Optional<SocialWallUser> findByUsername(String username);

    Optional<SocialWallUser> findByEmail(String email);

    Optional<SocialWallUser> findById(String id);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsById(String id);

    Page<SocialWallUser> findByUsernameContaining(String name, Pageable pageable);
}
