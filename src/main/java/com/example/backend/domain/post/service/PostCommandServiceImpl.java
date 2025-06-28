package com.example.backend.domain.post.service;

import com.example.backend.domain.category.Category;
import com.example.backend.domain.category.CategoryRepository;
import com.example.backend.domain.post.converter.PostConverter;
import com.example.backend.domain.post.dto.PostRequestDTO;
import com.example.backend.domain.post.entity.Post;
import com.example.backend.domain.post.exception.PostErrorCode;
import com.example.backend.domain.post.exception.PostException;
import com.example.backend.domain.post.repository.PostRepository;
import com.example.backend.domain.post_image.converter.PostImageConverter;
import com.example.backend.domain.post_image.entity.PostImage;
import com.example.backend.domain.post_image.repository.PostImageRepository;
import com.example.backend.domain.user.User;
import com.example.backend.domain.user.UserRepository;
import com.example.backend.domain.user.exception.UserErrorCode;
import com.example.backend.domain.user.exception.UserException;
import com.example.backend.global.config.aws.S3Service;
import com.example.backend.global.util.RedisUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
//    private final S3Service s3Service;
    private final PostImageRepository postImageRepository;
    private final RedisUtil redisUtil;

    @Override
    public Post createPost(PostRequestDTO.CreatePostRequestDTO request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        Post post = PostConverter.toPost(request, user, category);

        if (request.getFileUrls() != null && !request.getFileUrls().isEmpty()) {
            List<PostImage> postImages = postImageRepository.findAllByFileUrlIn(request.getFileUrls());
            postImages.forEach(postImage -> postImage.setPost(post));

        }
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
        //post.getPostImages().forEach(postImage -> s3Service.deleteFile(postImage.getFileUrl()));
        // Redis에 게시글 조회수 삭제
        redisUtil.delete(post.getId().toString());
        postRepository.delete(post);
    }

    @Transactional
    @Override
    public Post updatePost(Long postId, PostRequestDTO.UpdatePostRequestDTO request, List<MultipartFile> images) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
        post.update(request.getTitle(), request.getContent());

        // 기존 url
        List<String> fileUrls = post.getPostImages().stream().map(PostImage::getFileUrl).toList();

        // 기존 url 과 수정된 url을 비교하여 없다면 삭제
        for(String fileUrl : fileUrls) {
            if(!request.getFileUrls().contains(fileUrl)) {
                //s3Service.deleteFile(fileUrl);
                postImageRepository.deleteByFileUrl(fileUrl);
            }
        }

        // 새로 첨부된 이미지 업로드
        if(images != null && !images.isEmpty()) {
            List<PostImage> postImages = uploadImage(images);
            postImages.forEach(postImage -> postImage.setPost(post));
        }
        return post;
    }

    @Override
    public List<PostImage> uploadImage(List<MultipartFile> images) {
        List<String> pictureUrls = new ArrayList<String>(Integer.parseInt("하이"));
        List<PostImage> postImages = pictureUrls.stream().map(PostImageConverter::toPostImage).toList();
        return postImageRepository.saveAll(postImages);
    }
}
