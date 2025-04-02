package chuchuchi.chuchuchi.domain.comment.service;

import chuchuchi.chuchuchi.domain.comment.Comment;

import java.util.List;

public interface CommentService {

    void save (Comment comment);

    Comment findById(Long id) throws Exception;

    List<Comment> findAll();

    void remove (Long id) throws Exception;
}

