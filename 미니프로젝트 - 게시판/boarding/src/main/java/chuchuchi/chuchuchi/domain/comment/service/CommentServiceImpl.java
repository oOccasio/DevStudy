package chuchuchi.chuchuchi.domain.comment.service;

import chuchuchi.chuchuchi.domain.comment.Comment;
import chuchuchi.chuchuchi.domain.comment.repository.CommentRepository;
import chuchuchi.chuchuchi.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public void save(Comment comment) {

        commentRepository.save(comment);
    }

    @Override
    public Comment findById(Long id) throws Exception {

        return commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("댓글이 없습니다."));
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public void remove(Long id) throws Exception {

        Comment currentComment = commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("댓글이 없습니다."));
        currentComment.remove();
        List <Comment> removableCommentList = currentComment.findRemovableList();
        commentRepository.deleteAll(removableCommentList);

    }
}
