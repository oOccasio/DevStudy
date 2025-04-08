package chuchuchi.chuchuchi.domain.comment.service;

import chuchuchi.chuchuchi.domain.comment.Comment;
import chuchuchi.chuchuchi.domain.comment.dto.CommentSaveDto;
import chuchuchi.chuchuchi.domain.comment.dto.CommentUpdateDto;
import chuchuchi.chuchuchi.domain.comment.exception.CommentException;
import chuchuchi.chuchuchi.domain.comment.exception.CommentExceptionType;
import chuchuchi.chuchuchi.domain.comment.repository.CommentRepository;
import chuchuchi.chuchuchi.domain.member.Role;
import chuchuchi.chuchuchi.domain.member.dto.MemberSignUpDto;
import chuchuchi.chuchuchi.domain.member.exception.MemberException;
import chuchuchi.chuchuchi.domain.member.exception.MemberExceptionType;
import chuchuchi.chuchuchi.domain.member.repository.MemberRepository;
import chuchuchi.chuchuchi.domain.member.service.MemberService;
import chuchuchi.chuchuchi.domain.post.Post;
import chuchuchi.chuchuchi.domain.post.dto.PostSaveDto;
import chuchuchi.chuchuchi.domain.post.exception.PostException;
import chuchuchi.chuchuchi.domain.post.exception.PostExceptionType;
import chuchuchi.chuchuchi.domain.post.repository.PostRepository;
import chuchuchi.chuchuchi.global.exception.BaseExceptionType;
import chuchuchi.chuchuchi.global.util.security.SecurityUtil;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    private PostRepository postRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager em;
    @Autowired
    private MemberRepository memberRepository;


    @BeforeEach
    public void signUpAndSetAuthentication() throws Exception{
        memberService.signUp(new MemberSignUpDto("USERNAME", "PASSWORD@123", "name", "nickname", 22));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username("USERNAME")
                                .password("PASSWORD@123")
                                .roles(Role.USER.toString())
                                .build(),
                        null)
        );

        SecurityContextHolder.setContext(emptyContext);
        clear();

    }

    private void anotherSignUpAndSetAuthentication() throws Exception{
        memberService.signUp(new MemberSignUpDto("USERNAME1", "PASSWORD123@@", "name", "nickname", 22));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username("USERNAME1")
                                .password("PASSWORD123@@")
                                .roles(Role.USER.toString())
                                .build(),
                        null)
        );

        SecurityContextHolder.setContext(emptyContext);
        clear();
    }



    private void clear(){
        em.flush();
        em.clear();
    }

    private Long saveComment(){


        Comment comment = Comment.builder().content("댓글").build();

        comment.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)));


        Long id = commentRepository.save(comment).getId();

        clear();

        return id;
    }

    private Long saveReComment (Long parentId){

        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));

        Comment comment = Comment.builder().content("대댓글").parent(parent).build();

        comment.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)));

        Long id = commentRepository.save(comment).getId();
        clear();

        return id;
    }

    private Long savePost(){
        String title = "제목";
        String content = "내용";
        PostSaveDto postSaveDto = new PostSaveDto(title, content, Optional.empty());

        Post save = postRepository.save(postSaveDto.toEntity());
        clear();
        return save.getId();
    }

    @Test
    public void commentSaveSuccess() throws Exception{
        //given
        Long postId = savePost();
        CommentSaveDto commentSaveDto = new CommentSaveDto("댓글");

        //when

        commentService.save(postId, commentSaveDto);
        clear();

        //then
        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class)
                .getResultList();
        assertThat(resultList.size()).isEqualTo(1);

    }

    @Test
    public void reCommentSaveSuccess() throws Exception{
        //given
        Long postId = savePost();
        clear();
        Long parentId = saveComment();
        clear();
        CommentSaveDto commentSaveDto = new CommentSaveDto("대댓글");

        //when
        commentService.saveReComment(postId, parentId, commentSaveDto);
        clear();

        //then
        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc", Comment.class)
                .getResultList();
        assertThat(resultList.size()).isEqualTo(2);
    }

    @Test
    public void commentSaveFailForNoPost() throws Exception{
        //given
        Long postId = savePost();
        CommentSaveDto commentSaveDto = new CommentSaveDto("댓글");

        //when,then
        assertThat(assertThrows(PostException.class, () -> commentService.save(postId + 1, commentSaveDto))
                .getBaseExceptionType()).isEqualTo(PostExceptionType.POST_NOT_FOUND);

    }

    @Test
    public void reCommentSaveFailForNoPost() throws Exception{
        //given
        Long postId = savePost();
        Long parentId = saveComment();
        CommentSaveDto commentSaveDto = new CommentSaveDto("댓글");

        //when, then
        assertThat(assertThrows(PostException.class,
                () -> commentService.saveReComment(postId + 1, parentId, commentSaveDto))
                .getBaseExceptionType()).isEqualTo(PostExceptionType.POST_NOT_FOUND);


    }

    @Test
    public void reCommentSaveFailForNoComment() throws Exception{
        //given
        Long postId = savePost();
        Long parentId = saveComment();
        CommentSaveDto commentSaveDto = new CommentSaveDto("댓글");

        //when, then
        assertThat(assertThrows(CommentException.class,
                () -> commentService.saveReComment(postId, parentId + 1, commentSaveDto))
                .getBaseExceptionType()).isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT);

    }

    @Test
    public void updateCommentSuccess() throws Exception{
        //given
        Long commentId = saveComment();
        clear();

        //when
        commentService.update(commentId, new CommentUpdateDto(Optional.of("댓글수정")));
        clear();

        //then
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));

        assertThat(comment.getContent()).isEqualTo("댓글수정");
    }

    @Test
    public void updateCommentFailForNoAuthority() throws Exception{
        //given
       Long commentId = saveComment();
        clear();

        anotherSignUpAndSetAuthentication();

        //when, then
        BaseExceptionType type = assertThrows(CommentException.class,
                () -> commentService.update(commentId, new
                        CommentUpdateDto(Optional.of("댓글수정")))).getBaseExceptionType();

        assertThat(type).isEqualTo(CommentExceptionType.NOT_AUTHORITY_UPDATE_COMMENT);

    }

    @Test
    public void deleteCommentFailForNoAuthority() throws Exception{
        //given
        Long postId = savePost();
        Long commentId = saveComment();
        clear();

        anotherSignUpAndSetAuthentication();

        BaseExceptionType type = assertThrows(CommentException.class, () ->
                commentService.remove(commentId)).getBaseExceptionType();

        assertThat(type).isEqualTo(CommentExceptionType.NOT_AUTHORITY_DELETE_COMMENT);
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

        Assertions.assertThat(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))
                .getChildList().size()).isEqualTo(4);

        //when
        commentService.remove(commentId);
        clear();

        //then
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));
        assertThat(findComment).isNotNull();
        assertThat(findComment.isRemoved()).isTrue();
        assertThat(findComment.getChildList().size()).isEqualTo(4);


    }

    //댓글 삭제경우
    //대댓글이 없음
    //DB 에서 바로삭제
    @Test
    public void RemoveCommentNoReComment() throws Exception{
        //given
        Long commentId = saveComment();

        //when
        commentService.remove(commentId);
        clear();

        //then
        Assertions.assertThat(commentRepository.findAll().size()).isSameAs(0);
        assertThat(assertThrows(CommentException.class, () -> commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).getBaseExceptionType())
                .isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT);

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

        Assertions.assertThat(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))
                .getChildList().size()).isEqualTo(4);

        commentService.remove(reCommentId1);
        clear();

        commentService.remove(reCommentId2);
        clear();

        commentService.remove(reCommentId3);
        clear();

        commentService.remove(reCommentId4);
        clear();

        Assertions.assertThat(commentRepository.findById(reCommentId1)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        Assertions.assertThat(commentRepository.findById(reCommentId2)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        Assertions.assertThat(commentRepository.findById(reCommentId3)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        Assertions.assertThat(commentRepository.findById(reCommentId4)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        clear();

        commentService.remove(commentId);
        clear();

        LongStream.rangeClosed(commentId, reCommentId4).forEach(id ->
                assertThat(assertThrows(CommentException.class,
                        () -> commentRepository.findById(id)
                                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)))
                        .getBaseExceptionType()).isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT));


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

        Assertions.assertThat(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(ReCommentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isFalse();
        Assertions.assertThat(commentRepository.findById(ReCommentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
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

        Assertions.assertThat(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();

        Assertions.assertThat(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))
                .getChildList().size()).isEqualTo(3);

        //when
        commentService.remove(ReCommentId1);
        clear();

        //then
        LongStream.rangeClosed(commentId, ReCommentId3).forEach(id ->
                assertThat(assertThrows(CommentException.class, () -> commentRepository.findById(id)
                        .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)))
                        .getBaseExceptionType()).isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT));

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

        Assertions.assertThat(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))
                .getChildList().size()).isEqualTo(3);

        //when
        commentService.remove(reCommentId2);
        Assertions.assertThat(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();

        //then
        Assertions.assertThat(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();

        Assertions.assertThat(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();

        Assertions.assertThat(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getId()).isNotNull();

        Assertions.assertThat(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();

        Assertions.assertThat(commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();

    }




}
