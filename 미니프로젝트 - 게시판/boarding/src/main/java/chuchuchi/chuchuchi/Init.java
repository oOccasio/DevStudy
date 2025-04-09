package chuchuchi.chuchuchi;


import chuchuchi.chuchuchi.domain.comment.Comment;
import chuchuchi.chuchuchi.domain.comment.repository.CommentRepository;
import chuchuchi.chuchuchi.domain.member.Member;
import chuchuchi.chuchuchi.domain.member.Role;
import chuchuchi.chuchuchi.domain.member.repository.MemberRepository;
import chuchuchi.chuchuchi.domain.post.Post;
import chuchuchi.chuchuchi.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.String.format;

@RequiredArgsConstructor
@Component
public class Init {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void save() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        // 1. Member 저장 및 리스트로 관리
        Member m1 = memberRepository.save(Member.builder()
                .username("username1")
                .name("name1")
                .password(passwordEncoder.encode("1234567890"))
                .nickName("밥 잘먹는 동훈이1")
                .role(Role.USER).age(22).build());

        Member m2 = memberRepository.save(Member.builder()
                .username("username2")
                .name("name2")
                .password(passwordEncoder.encode("1234567890"))
                .nickName("밥 잘먹는 동훈이2")
                .role(Role.USER).age(22).build());

        Member m3 = memberRepository.save(Member.builder()
                .username("username3")
                .name("name3")
                .password(passwordEncoder.encode("1234567890"))
                .nickName("밥 잘먹는 동훈이3")
                .role(Role.USER).age(22).build());

        List<Member> members = List.of(m1, m2, m3);

        // 2. Post 저장
        for (int i = 0; i <= 50; i++) {
            Post post = Post.builder()
                    .title("게시글 " + i)
                    .content("내용 " + i)
                    .build();

            post.confirmWriter(members.get(i % 3));
            postRepository.save(post);
        }

        List<Post> posts = postRepository.findAll();

        // 3. Comment 저장
        for (int i = 1; i <= 150; i++) {
            Comment comment = Comment.builder()
                    .content("댓글" + i)
                    .build();

            comment.confirmWriter(members.get(i % 3));
            comment.confirmPost(posts.get(i % posts.size()));

            commentRepository.save(comment);
        }

        List<Comment> comments = commentRepository.findAll();

        // 4. 대댓글 저장
        for (Comment parentComment : comments) {
            for (int i = 1; i <= 3; i++) {
                Comment recomment = Comment.builder()
                        .content("대댓글" + i)
                        .build();

                recomment.confirmWriter(members.get(i % 3));
                recomment.confirmPost(parentComment.getPost());
                recomment.confirmParent(parentComment);

                commentRepository.save(recomment);
            }
        }
    }

}
