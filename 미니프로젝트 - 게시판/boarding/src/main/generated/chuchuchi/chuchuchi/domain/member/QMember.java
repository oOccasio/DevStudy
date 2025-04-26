package chuchuchi.chuchuchi.domain.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1865207416L;

    public static final QMember member = new QMember("member1");

    public final chuchuchi.chuchuchi.domain.QBaseTimeEntity _super = new chuchuchi.chuchuchi.domain.QBaseTimeEntity(this);

    public final NumberPath<Integer> age = createNumber("age", Integer.class);

    public final ListPath<chuchuchi.chuchuchi.domain.comment.Comment, chuchuchi.chuchuchi.domain.comment.QComment> commentList = this.<chuchuchi.chuchuchi.domain.comment.Comment, chuchuchi.chuchuchi.domain.comment.QComment>createList("commentList", chuchuchi.chuchuchi.domain.comment.Comment.class, chuchuchi.chuchuchi.domain.comment.QComment.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath name = createString("name");

    public final StringPath nickName = createString("nickName");

    public final StringPath password = createString("password");

    public final ListPath<chuchuchi.chuchuchi.domain.post.Post, chuchuchi.chuchuchi.domain.post.QPost> postList = this.<chuchuchi.chuchuchi.domain.post.Post, chuchuchi.chuchuchi.domain.post.QPost>createList("postList", chuchuchi.chuchuchi.domain.post.Post.class, chuchuchi.chuchuchi.domain.post.QPost.class, PathInits.DIRECT2);

    public final StringPath refreshToken = createString("refreshToken");

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final StringPath username = createString("username");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

