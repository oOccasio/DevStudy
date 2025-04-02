package chuchuchi.chuchuchi.domain.comment.repository;

import chuchuchi.chuchuchi.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
