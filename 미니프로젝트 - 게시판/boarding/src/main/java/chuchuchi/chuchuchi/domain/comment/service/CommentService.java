package chuchuchi.chuchuchi.domain.comment.service;

import chuchuchi.chuchuchi.domain.comment.Comment;
import chuchuchi.chuchuchi.domain.comment.dto.CommentSaveDto;
import chuchuchi.chuchuchi.domain.comment.dto.CommentUpdateDto;

import java.util.List;

public interface CommentService {

    void save (Long postId, CommentSaveDto commentSaveDto);

    void saveReComment(Long postId, Long parentId, CommentSaveDto commentSaveDto);

    void update(Long id, CommentUpdateDto commentUpdateDto);

    void remove (Long id) throws Exception;
}

