package chuchuchi.chuchuchi.domain.post;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = 1611863096L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final chuchuchi.chuchuchi.domain.QBaseTimeEntity _super = new chuchuchi.chuchuchi.domain.QBaseTimeEntity(this);

    public final ListPath<chuchuchi.chuchuchi.domain.comment.Comment, chuchuchi.chuchuchi.domain.comment.QComment> commentList = this.<chuchuchi.chuchuchi.domain.comment.Comment, chuchuchi.chuchuchi.domain.comment.QComment>createList("commentList", chuchuchi.chuchuchi.domain.comment.Comment.class, chuchuchi.chuchuchi.domain.comment.QComment.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath file_path = createString("file_path");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath title = createString("title");

    public final chuchuchi.chuchuchi.domain.member.QMember writer;

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.writer = inits.isInitialized("writer") ? new chuchuchi.chuchuchi.domain.member.QMember(forProperty("writer")) : null;
    }

}

