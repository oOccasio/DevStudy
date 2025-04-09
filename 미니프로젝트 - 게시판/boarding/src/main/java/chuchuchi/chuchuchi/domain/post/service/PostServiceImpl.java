package chuchuchi.chuchuchi.domain.post.service;

import chuchuchi.chuchuchi.domain.member.exception.MemberException;
import chuchuchi.chuchuchi.domain.member.exception.MemberExceptionType;
import chuchuchi.chuchuchi.domain.member.repository.MemberRepository;
import chuchuchi.chuchuchi.domain.post.Post;
import chuchuchi.chuchuchi.domain.post.cond.PostSearchCondition;
import chuchuchi.chuchuchi.domain.post.dto.PostInfoDto;
import chuchuchi.chuchuchi.domain.post.dto.PostPagingDto;
import chuchuchi.chuchuchi.domain.post.dto.PostSaveDto;
import chuchuchi.chuchuchi.domain.post.dto.PostUpdateDto;
import chuchuchi.chuchuchi.domain.post.exception.PostException;
import chuchuchi.chuchuchi.domain.post.exception.PostExceptionType;
import chuchuchi.chuchuchi.domain.post.repository.PostRepository;
import chuchuchi.chuchuchi.global.file.exception.FileException;
import chuchuchi.chuchuchi.global.file.service.FileService;
import chuchuchi.chuchuchi.global.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {


    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;


    private void checkAuthority(Post post, PostExceptionType postExceptionType){
        if(!post.getWriter().getUsername().equals(SecurityUtil.getLoginUsername()))
            throw new PostException(postExceptionType);
    }


    @Override
    public void save(PostSaveDto postSaveDto) throws FileException {

        Post currentPost = postSaveDto.toEntity();

        currentPost.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)));

        postSaveDto.uploadFile().ifPresent(
                file -> currentPost.updateFile_path(fileService.save(file))
        );

        postRepository.save(currentPost);

    }


    @Override
    public void update(Long id, PostUpdateDto postUpdateDto) {


        Post currentPost = postRepository.findById(id)
                .orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND));

        checkAuthority(currentPost, PostExceptionType.NOT_AUTHORITY_UPDATE_POST);

        postUpdateDto.title().ifPresent(currentPost::updateTitle);
        postUpdateDto.content().ifPresent(currentPost::updateContent);

        if(currentPost.getFile_path() != null)
            fileService.delete(currentPost.getFile_path());

        postUpdateDto.uploadFile().ifPresentOrElse(
                multipartFile -> currentPost.updateFile_path(fileService.save(multipartFile)),
                () -> currentPost.updateFile_path(null)
        );


    }

    @Override
    public void delete(Long id) {
        Post currentPost = postRepository.findById(id)
                .orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND));

        checkAuthority(currentPost, PostExceptionType.NOT_AUTHORITY_DELETE_POST);

        if(currentPost.getFile_path() != null)
            fileService.delete(currentPost.getFile_path());

        postRepository.delete(currentPost);

    }

    @Override
    public PostInfoDto getPostInfo(Long id) {

        return new PostInfoDto(postRepository.findWithWriterById(id)
                .orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND)));
    }

    @Override
    public PostPagingDto getPostList(Pageable pageable, PostSearchCondition postSearchCondition) {
        return null;
    }
}
