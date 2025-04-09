package chuchuchi.chuchuchi;

import chuchuchi.chuchuchi.domain.comment.Comment;
import chuchuchi.chuchuchi.domain.comment.repository.CommentRepository;
import chuchuchi.chuchuchi.domain.member.Member;
import chuchuchi.chuchuchi.domain.member.Role;
import chuchuchi.chuchuchi.domain.member.repository.MemberRepository;
import chuchuchi.chuchuchi.domain.post.Post;
import chuchuchi.chuchuchi.domain.post.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.Long.parseLong;
import static java.lang.Long.valueOf;
import static java.lang.String.format;

@RequiredArgsConstructor
@Component
public class InitService {


    private final Init init;


    @PostConstruct
    public void init() {
        init.save();
    }
}
