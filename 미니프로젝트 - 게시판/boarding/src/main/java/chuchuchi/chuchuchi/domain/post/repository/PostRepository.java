package chuchuchi.chuchuchi.domain.post.repository;

import chuchuchi.chuchuchi.domain.post.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"writer"})
    Optional<Post> findWithWriterById(Long id);

}
