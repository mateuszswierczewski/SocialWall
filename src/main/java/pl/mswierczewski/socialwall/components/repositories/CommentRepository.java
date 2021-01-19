package pl.mswierczewski.socialwall.components.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mswierczewski.socialwall.components.models.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {


}
