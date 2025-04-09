package chuchuchi.chuchuchi.domain.post.dto;

import chuchuchi.chuchuchi.domain.comment.Comment;
import chuchuchi.chuchuchi.domain.comment.dto.CommentInfoDto;
import chuchuchi.chuchuchi.domain.member.dto.MemberInfoDto;
import chuchuchi.chuchuchi.domain.post.Post;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class PostInfoDto {

    private Long postId;
    private String title;
    private String content;
    private String filePath;

    private MemberInfoDto writerDto;

    private List<CommentInfoDto> commentInfoDtoList;


    public PostInfoDto(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.filePath = post.getFile_path();

        this.writerDto = new MemberInfoDto(post.getWriter());

        /*
        댓글과 대댓글을 그룹짓기
        post.getCommentList()는 댓글과 대댓글이 모두 조회된다
         */

        Map<Comment, List<Comment>> commentListMap = post.getCommentList().stream()
                                                    .filter(comment -> comment.getParent() != null)
                                                    .collect(Collectors.groupingBy(Comment::getParent));

        /*
        댓글과 대댓글을 통해 CommentInfoDto 생성
         */

        commentInfoDtoList = commentListMap.keySet().stream()
                .map(comment -> new CommentInfoDto(comment, commentListMap.get(comment)))
                .toList();
    }

}
