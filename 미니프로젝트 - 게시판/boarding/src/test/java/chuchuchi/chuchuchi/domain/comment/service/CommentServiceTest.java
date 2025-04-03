package chuchuchi.chuchuchi.domain.comment.service;

import chuchuchi.chuchuchi.domain.comment.Comment;
import chuchuchi.chuchuchi.domain.comment.repository.CommentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.LongStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    private Long saveComment(){
        Comment comment = Comment.builder().content("댓글").build();

        Long id = commentRepository.save(comment).getId();

        clear();

        return id;
    }

    private Long saveReComment (Long parentId){
        Comment parent = commentRepository.findById(parentId).orElse(null);
        Comment comment = Comment.builder().content("댓글").parent(parent).build();

        Long id = commentRepository.save(comment).getId();
        clear();

        return id;
    }


    //댓글 삭제 경우
    //대댓글 남아 있음
    //DB와 화면에서는 지워지지 않고, "삭제된 댓글입니다"로 표시
    @Test
    public void RemoveCommentYesReComment() throws Exception{

        //given
        Long commentId = saveComment();
        saveReComment(commentId);
        saveReComment(commentId);
        saveReComment(commentId);
        saveReComment(commentId);

        Assertions.assertThat(commentService.findById(commentId).getChildList().size()).isEqualTo(4);

        //when
        commentService.remove(commentId);
        clear();

        //then
        Comment findComment = commentRepository.findById(commentId).get();
        assertThat(findComment).isNotNull();
        assertThat(findComment.isRemoved()).isTrue();
        assertThat(findComment.getChildList().size()).isEqualTo(4);


    }

    //댓글 삭제경우
    //대댓글이 없음
    //DB에서 바로삭제
    @Test
    public void RemoveCommentNoReComment() throws Exception{
        //given
        Long commentId = saveComment();

        //when
        commentService.remove(commentId);
        clear();

        //then
        Assertions.assertThat(commentService.findAll().size()).isSameAs(0);
        assertThat(assertThrows(EntityNotFoundException.class, () -> commentService.findById(commentId)).getMessage())
                .isEqualTo("댓글이 없습니다.");

    }


    //댓글 삭제 경우
    //대댓글이 존재하나 모두 삭제된 경우
    //댓글과, 달려있는 대댓글 모두 DB에서 일괄 삭제
    @Test
    public void RemoveCommentYesReCommentButAllRemove() throws Exception{

        Long commentId = saveComment();
        Long reCommentId1 = saveReComment(commentId);
        Long reCommentId2 = saveReComment(commentId);
        Long reCommentId3 = saveReComment(commentId);
        Long reCommentId4 = saveReComment(commentId);

        Assertions.assertThat(commentService.findById(commentId).getChildList().size()).isEqualTo(4);

        commentService.remove(reCommentId1);
        clear();

        commentService.remove(reCommentId2);
        clear();

        commentService.remove(reCommentId3);
        clear();

        commentService.remove(reCommentId4);
        clear();

        Assertions.assertThat(commentService.findById(reCommentId1).isRemoved()).isTrue();
        Assertions.assertThat(commentService.findById(reCommentId2).isRemoved()).isTrue();
        Assertions.assertThat(commentService.findById(reCommentId3).isRemoved()).isTrue();
        Assertions.assertThat(commentService.findById(reCommentId4).isRemoved()).isTrue();
        clear();

        commentService.remove(commentId);
        clear();

        LongStream.rangeClosed(commentId, reCommentId4).forEach(id ->
                assertThat(assertThrows(EntityNotFoundException.class,
                        () -> commentService.findById(id)).getMessage())
                        .isEqualTo("댓글이 없습니다."));


    }

    //대댓글 삭제하는 경우
    //부모 댓글이 삭제되지 않은 경우
    //내용만 삭제, DB에서는 삭제 X
    @Test
    public void RemoveReCommentYesParentComment() throws Exception{
        //given
        Long commentId = saveComment();
        Long ReCommentId = saveReComment(commentId);

        commentService.remove(ReCommentId);
        clear();

        Assertions.assertThat(commentService.findById(commentId)).isNotNull();
        Assertions.assertThat(commentService.findById(ReCommentId)).isNotNull();
        Assertions.assertThat(commentService.findById(commentId).isRemoved()).isFalse();
        Assertions.assertThat(commentService.findById(ReCommentId).isRemoved()).isTrue();
    }

    //대댓글 삭제하는 경우
    //부모 댓글 삭제 대댓글 모두 삭제
    //부모를 포함한 모든 대댓글 DB에서 삭제, 화면에서도 삭제
    @Test
    public void RemoveReCommentNoParentComment() throws Exception{
        //given
        Long commentId = saveComment();
        Long ReCommentId1 = saveReComment(commentId);
        Long ReCommentId2 = saveReComment(commentId);
        Long ReCommentId3 = saveReComment(commentId);

        commentService.remove(ReCommentId2);
        clear();

        commentService.remove(ReCommentId3);
        clear();

        commentService.remove(commentId);
        clear();

        Assertions.assertThat(commentService.findById(commentId)).isNotNull();
        Assertions.assertThat(commentService.findById(commentId).getChildList().size()).isEqualTo(3);

        //when
        commentService.remove(ReCommentId1);
        clear();

        //then
        LongStream.rangeClosed(commentId, ReCommentId3).forEach(id ->
                assertThat(assertThrows(EntityNotFoundException.class, () -> commentService.findById(id))
                        .getMessage()).isEqualTo("댓글이 없습니다."));

    }

    //대댓글 삭제하는 경우
    //부모 댓글 삭제, 다른 대댓글 남아있음
    //해당 대댓글만 삭제, DB삭제 X 화면상에만 삭제된 댓글입니다 표시
    public void removeReCommentYesOtherChildComment() throws Exception{
        Long commentId = saveComment();
        Long reCommentId1 = saveReComment(commentId);
        Long reCommentId2 = saveReComment(commentId);
        Long reCommentId3 = saveReComment(commentId);

        commentService.remove(commentId);
        clear();
        commentService.remove(reCommentId3);
        clear();

        Assertions.assertThat(commentService.findById(commentId)).isNotNull();
        Assertions.assertThat(commentService.findById(commentId).getChildList().size()).isEqualTo(3);

        //when
        commentService.remove(reCommentId2);
        Assertions.assertThat(commentService.findById(commentId)).isNotNull();

        //then
        Assertions.assertThat(commentService.findById(reCommentId2)).isNotNull();
        Assertions.assertThat(commentService.findById(reCommentId2).isRemoved()).isTrue();
        Assertions.assertThat(commentService.findById(reCommentId3).getId()).isNotNull();
        Assertions.assertThat(commentService.findById(reCommentId1).getId()).isNotNull();
        Assertions.assertThat(commentService.findById(commentId).getId()).isNotNull();


    }




}
