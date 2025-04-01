package chuchuchi.chuchuchi.domain.post;

import chuchuchi.chuchuchi.domain.BaseTimeEntity;
import chuchuchi.chuchuchi.domain.comment.Comment;
import chuchuchi.chuchuchi.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "POST")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private String file_path;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList <> ();




    // == 연관관계 편의 메서드 == //
    public void confirmWriter(Member writer){
        this.writer = writer;
        writer.addPost(this);
    }

    public void addComment(Comment comment){
        commentList.add(comment);
    }


    // == update 관련 메서드 == //

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateFile_path(String file_path) {
        this.file_path = file_path;
    }

}
