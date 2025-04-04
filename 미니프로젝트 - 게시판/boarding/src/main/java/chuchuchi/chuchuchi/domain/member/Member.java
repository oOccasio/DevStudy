package chuchuchi.chuchuchi.domain.member;

import chuchuchi.chuchuchi.domain.comment.Comment;
import chuchuchi.chuchuchi.domain.post.Post;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import chuchuchi.chuchuchi.domain.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Table(name = "MEMBER")
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id; //primary Key


    @Column(nullable = false, length = 30, unique = true)
    private String username;


    private String password;


    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 30)
    private String nickName;

    @Column(nullable = false, length = 30)
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 1000)
    private String refreshToken; //RefreshToken

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();




    // == 연관관계 메서드 == //
    public void addPost(Post post) {
        postList.add(post);
    }

    public void addComment(Comment comment) {
        commentList.add(comment);
    }


    //== refresh 토큰 관련 메서드 == //

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void destoryRefreshToken() {
        this.refreshToken = null;
    }



    // ==  update 관련 메서드 == //

    public void updatePassword(PasswordEncoder passwordEncoder, String password) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateName(String name){
        this.name = name;
    }

    public void updateNickName(String nickName){
        this.nickName = nickName;
    }

    public void updateAge(Integer age){
        this.age = age;
    }

    // == password 암호화 == //
    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    //== 비밀번호 변경, 회원탈퇴 시 비밀번호를 확인하고 비밀번호의 일치 여부 판단하기
    public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword) {
        return passwordEncoder.matches(checkPassword, getPassword());
    }

    public void addUserAuthority(){
        this.role = Role.USER;
    }
}
