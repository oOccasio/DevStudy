package chuchuchi.chuchuchi.domain.post;

import chuchuchi.chuchuchi.domain.member.Role;
import chuchuchi.chuchuchi.domain.member.dto.MemberSignUpDto;
import chuchuchi.chuchuchi.domain.member.service.MemberService;
import chuchuchi.chuchuchi.domain.post.dto.PostSaveDto;
import chuchuchi.chuchuchi.domain.post.dto.PostUpdateDto;
import chuchuchi.chuchuchi.domain.post.exception.PostException;
import chuchuchi.chuchuchi.domain.post.service.PostService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class PostServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private PostService postService;

    @Autowired
    private MemberService memberService;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password123@@@";

    private void clear(){
        em.flush();
        em.clear();
    }

    private void deleteFile(String filePath){
        File files = new File(filePath);
        files.delete();
    }

    private MockMultipartFile getMockUploadFile() throws IOException{
        String content = "fake image content";
        return new MockMultipartFile(
                "file",
                "file.jpg",
                "image/jpeg",
                content.getBytes());
    }

    @BeforeEach
    public void signUpAndSetAuthentication() throws Exception {
        memberService.signUp(new MemberSignUpDto(USERNAME, PASSWORD, "name", "nickname", 22));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME)
                                .password(PASSWORD)
                                .roles(Role.USER.toString())
                                .build(),
                null)
        );

        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    private void setAnotherAuthentication() throws Exception{
        memberService.signUp(new MemberSignUpDto(USERNAME+"123", PASSWORD, "name", "nickname", 22));

        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME+"123")
                                .password(PASSWORD)
                                .roles(Role.USER.toString())
                                .build(),null));

        SecurityContextHolder.setContext(emptyContext);
        clear();
    }


    @Test
    public void postSaveSuccessNoFile() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        PostSaveDto postSaveDto = new PostSaveDto(title, content, Optional.empty());

        //when
        postService.save(postSaveDto);
        clear();

        //then
        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        Post post = em.find(Post.class, findPost.getId());
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(post.getFile_path()).isNull();
    }


    @Test
    public void postSaveSuccessFile() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        PostSaveDto postSaveDto = new PostSaveDto(title, content, Optional.of(getMockUploadFile()));

        //when
        postService.save(postSaveDto);
        clear();

        //then
        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        Post post = em.find(Post.class, findPost.getId());
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(post.getFile_path()).isNotNull();

        deleteFile(post.getFile_path());

    }

    @Test
    public void postSaveFailNoTitleOrContent() throws Exception {
        //given
        String title = "제목";
        String content = "내용";

        PostSaveDto postSaveDto1 = new PostSaveDto(null, content, Optional.empty());
        PostSaveDto postSaveDto2 = new PostSaveDto(title, null, Optional.empty());

        //when, then
        assertThrows(Exception.class, () -> postService.save(postSaveDto1));
        assertThrows(Exception.class, () -> postService.save(postSaveDto2));
    }

    @Test
    public void postUpdateSuccessNoFile() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        PostSaveDto postSaveDto = new PostSaveDto(title, content, Optional.empty());
        postService.save(postSaveDto);
        clear();

        //when
        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        PostUpdateDto postUpdateDto = new PostUpdateDto(Optional.of("바꾼제목"), Optional.of("바꾼내용"), Optional.empty());
        postService.update(findPost.getId(), postUpdateDto);


        //then
        Post post = em.find(Post.class, findPost.getId());
        assertThat(post.getContent()).isEqualTo("바꾼내용");
        assertThat(post.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(post.getFile_path()).isNull();
    }

    @Test
    public void postUpdateSuccessUpload() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        PostSaveDto postSaveDto = new PostSaveDto(title, content, Optional.empty());
        postService.save(postSaveDto);
        clear();

        //when
        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();

        PostUpdateDto postUpdateDto = new PostUpdateDto(
                Optional.of("바꾼제목"),
                Optional.of("바꾼내용"),
                Optional.of(getMockUploadFile()));
        postService.update(findPost.getId(), postUpdateDto);

        clear();

        //then
        Post post = em.find(Post.class, findPost.getId());
        assertThat(post.getContent()).isEqualTo("바꾼내용");
        assertThat(post.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(post.getFile_path()).isNotNull();

        deleteFile(post.getFile_path());

    }

    @Test
    public void PostUpdateSuccessUploadFile() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        PostSaveDto postSaveDto = new PostSaveDto(title, content, Optional.of(getMockUploadFile()));
        postService.save(postSaveDto);

        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        assertThat(findPost.getFile_path()).isNotNull();
        clear();

        //when
        PostUpdateDto postUpdateDto = new PostUpdateDto(
                Optional.of("바꾼제목"),
                Optional.of("바꾼내용"),
                Optional.empty());
        postService.update(findPost.getId(), postUpdateDto);
        clear();

        //then
        findPost = em.find(Post.class, findPost.getId());
        assertThat(findPost.getContent()).isEqualTo("바꾼내용");
        assertThat(findPost.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(findPost.getFile_path()).isNull();
    }

    @Test
    public void postDeleteSuccess() throws Exception {
        String title = "제목";
        String content = "내용";
        PostSaveDto postSaveDto = new PostSaveDto(title, content, Optional.empty());
        postService.save(postSaveDto);
        clear();

        //when
        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        postService.delete(findPost.getId());

        //then
        List<Post> findPosts = em.createQuery("select p from Post p", Post.class).getResultList();
        assertThat(findPosts.size()).isEqualTo(0);
    }

    @Test
    public void postDeleteFail() throws Exception {
        String title = "제목";
        String content = "내용";
        PostSaveDto postSaveDto = new PostSaveDto(title, content, Optional.empty());

        postService.save(postSaveDto);
        clear();

        //when,then
        setAnotherAuthentication();
        Post findPost = em.createQuery("select p from Post p", Post.class).getSingleResult();
        assertThrows(PostException.class, () -> postService.delete(findPost.getId()));
    }
}
