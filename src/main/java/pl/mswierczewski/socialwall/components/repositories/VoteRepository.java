package pl.mswierczewski.socialwall.components.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mswierczewski.socialwall.components.models.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, String> {
}
