package pl.mswierczewski.socialwall.components.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mswierczewski.socialwall.components.models.Post;
import pl.mswierczewski.socialwall.components.models.SocialWallUser;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {

    Page<Post> findAllByAuthor(SocialWallUser author, Pageable pageable);
}
