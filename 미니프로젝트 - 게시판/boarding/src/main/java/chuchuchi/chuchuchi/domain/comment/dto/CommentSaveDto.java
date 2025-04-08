package chuchuchi.chuchuchi.domain.comment.dto;

import chuchuchi.chuchuchi.domain.comment.Comment;

public record CommentSaveDto(String content) {

    public Comment toEntity() {
        return Comment.builder().content(content).build();
    }
}
