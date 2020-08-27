package pl.mswierczewski.socialwall.security.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JwtTokenRepository extends JpaRepository<JwtToken, String> {

    Optional<List<JwtToken>> findAllByUserId(String userId);

}
