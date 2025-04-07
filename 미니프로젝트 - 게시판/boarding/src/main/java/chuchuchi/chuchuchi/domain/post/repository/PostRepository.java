package chuchuchi.chuchuchi.domain.post.repository;

import chuchuchi.chuchuchi.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
