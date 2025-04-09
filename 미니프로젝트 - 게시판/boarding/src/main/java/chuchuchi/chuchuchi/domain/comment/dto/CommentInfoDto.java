package chuchuchi.chuchuchi.domain.comment.dto;

import chuchuchi.chuchuchi.domain.comment.Comment;
import chuchuchi.chuchuchi.domain.member.dto.MemberInfoDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentInfoDto {

    private final static String DEFAULT_DELETE_MESSAGE = "삭제된 댓글입니다.";

    private Long postId;

    private Long commentId;
    private String content;
    private boolean isRemoved;

    private MemberInfoDto writerDto;

    private List<ReCommentInfoDto> reCommentInfoDtoList;
    public CommentInfoDto(Comment comment, List<Comment> reCommentList) {
        this.postId = comment.getPost().getId();
        this.commentId = comment.getId();
        this.content = comment.getContent();

        if(comment.isRemoved())
            this.content = DEFAULT_DELETE_MESSAGE;

        this.isRemoved = comment.isRemoved();
        this.writerDto = new MemberInfoDto(comment.getWriter());
        this.reCommentInfoDtoList = reCommentList.stream().map(ReCommentInfoDto::new).toList();
    }


}
