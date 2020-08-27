package pl.mswierczewski.socialwall.components.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SocialWallUserRepository extends JpaRepository<SocialWallUser, UUID> {

    Optional<SocialWallUser> findByUsername(String username);

    Optional<SocialWallUser> findByEmail(String email);

    Optional<SocialWallUser> findById(String id);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsById(String id);


}
