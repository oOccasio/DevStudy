package chuchuchi.chuchuchi.domain.comment;

import chuchuchi.chuchuchi.domain.BaseTimeEntity;
import chuchuchi.chuchuchi.domain.member.Member;
import chuchuchi.chuchuchi.domain.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment")
@Getter
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Lob
    @Column(nullable = false)
    private String content;


    private boolean isRemoved = false;

    // == 부모를 삭제해도 댓글은 남아있음 == //
    @OneToMany(mappedBy = "parent")
    private List<Comment> childList = new ArrayList<>();


    // == 연관관계 편의 메서드 == //

    public void confirmWriter(Member writer) {
        this.writer = writer;
        writer.addComment(this);
    }

    public void confirmPost(Post post) {
        this.post = post;
        post.addComment(this);
    }

    public void addChild(Comment child) {
        childList.add(child);
    }

    public void confirmParent(Comment parent) {
        this.parent = parent;
        parent.addChild(this);
    }





    // == update 메서드 == //

    public void updateContent(String content) {
        this.content = content;
    }

    public void remove() {
        isRemoved = true;
    }


    @Builder
    public Comment( Member writer, Post post, Comment parent, String content){
        this.writer = writer;
        this.post = post;
        this.parent = parent;
        this.content = content;
        this.isRemoved = false;
    }

}
