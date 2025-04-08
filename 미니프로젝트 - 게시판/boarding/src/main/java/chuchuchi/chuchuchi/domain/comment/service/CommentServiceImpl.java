package chuchuchi.chuchuchi.domain.comment.service;

import chuchuchi.chuchuchi.domain.comment.Comment;
import chuchuchi.chuchuchi.domain.comment.dto.CommentSaveDto;
import chuchuchi.chuchuchi.domain.comment.dto.CommentUpdateDto;
import chuchuchi.chuchuchi.domain.comment.exception.CommentException;
import chuchuchi.chuchuchi.domain.comment.exception.CommentExceptionType;
import chuchuchi.chuchuchi.domain.comment.repository.CommentRepository;
import chuchuchi.chuchuchi.domain.member.exception.MemberException;
import chuchuchi.chuchuchi.domain.member.exception.MemberExceptionType;
import chuchuchi.chuchuchi.domain.member.repository.MemberRepository;
import chuchuchi.chuchuchi.domain.post.exception.PostException;
import chuchuchi.chuchuchi.domain.post.exception.PostExceptionType;
import chuchuchi.chuchuchi.domain.post.repository.PostRepository;
import chuchuchi.chuchuchi.global.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Override
    public void save(Long postId, CommentSaveDto commentSaveDto) {

        Comment currentComment = commentSaveDto.toEntity();

        currentComment.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)));

        currentComment.confirmPost(postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND)));

        commentRepository.save(currentComment);

    }

    @Override
    public void saveReComment(Long postId, Long parentId, CommentSaveDto commentSaveDto) {

        Comment currentComment = commentSaveDto.toEntity();

        currentComment.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)));

        currentComment.confirmPost(postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND)));

        currentComment.confirmParent(commentRepository.findById(parentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)));


        commentRepository.save(currentComment);

    }

    @Override
    public void update(Long id, CommentUpdateDto commentUpdateDto) {

        Comment currentComment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));

        if(!currentComment.getWriter().getUsername().equals(SecurityUtil.getLoginUsername())) {
            throw new CommentException(CommentExceptionType.NOT_AUTHORITY_UPDATE_COMMENT);
        }

        commentUpdateDto.content().ifPresent(currentComment::updateContent);

    }

    @Override
    public void remove(Long id) throws Exception {

        Comment currentComment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));

        if(!currentComment.getWriter().getUsername().equals(SecurityUtil.getLoginUsername())) {
            throw new CommentException(CommentExceptionType.NOT_AUTHORITY_DELETE_COMMENT);
        }

        currentComment.remove();
        List<Comment> removableCommentList = currentComment.findRemovableList();
        commentRepository.deleteAll(removableCommentList);
    }
}
