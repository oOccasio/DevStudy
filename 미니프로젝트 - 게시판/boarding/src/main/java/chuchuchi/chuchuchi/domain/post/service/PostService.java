package chuchuchi.chuchuchi.domain.post.service;

import chuchuchi.chuchuchi.domain.post.cond.PostSearchCondition;
import chuchuchi.chuchuchi.domain.post.dto.PostInfoDto;
import chuchuchi.chuchuchi.domain.post.dto.PostPagingDto;
import chuchuchi.chuchuchi.domain.post.dto.PostSaveDto;
import chuchuchi.chuchuchi.domain.post.dto.PostUpdateDto;
import org.springframework.data.domain.Pageable;

import javax.annotation.processing.FilerException;

public interface PostService {

    /*
    게시글 등록
     */

    void save(PostSaveDto postSaveDto) throws FilerException;

    /*
    게시글 수정
     */
    void update(Long id, PostUpdateDto postUpdateDto);

    /*
    게시글 삭제
     */

    void delete(Long id);

    /*
    게시글 1개 조회
     */
    PostInfoDto getPostInfo(Long id);

    /*
    검색조건에 따른 게시글 리스트 조회 + 페이징
     */
    PostPagingDto getPostList(Pageable pageable, PostSearchCondition postSearchCondition);



}
