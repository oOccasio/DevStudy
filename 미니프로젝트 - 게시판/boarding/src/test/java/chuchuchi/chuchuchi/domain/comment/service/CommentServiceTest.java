package chuchuchi.chuchuchi.domain.comment.service;

import chuchuchi.chuchuchi.domain.comment.Comment;
import chuchuchi.chuchuchi.domain.comment.repository.CommentRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    EntityManager em;

    private void clear(){
        em.flush();
        em.clear();
    }

    private Long SaveComment(){
        Comment comment = Comment.builder().content("댓글").build();

        Long id = commentRepository.save(comment).getId();

        clear();

        return id;
    }

    private Long SaveRe
}
