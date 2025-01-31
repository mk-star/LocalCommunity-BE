package com.example.backend.post;

import com.example.backend.category.Category;
import com.example.backend.category.CategoryRepository;
import com.example.backend.comment.Comment;
import com.example.backend.comment.CommentRepository;
import com.example.backend.comment.CommentResponse;
import com.example.backend.config.RedisDao;
import com.example.backend.config.S3Service;
import com.example.backend.post_image.PostImage;
import com.example.backend.post_image.PostImageRepository;
import com.example.backend.post_image.PostImageResponse;
import com.example.backend.post_like.PostLikeService;
import com.example.backend.post_scrap.PostScrapService;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final PostImageRepository postImageRepository;
    private final S3Service s3Service;
    private final PostLikeService postLikeService;
    private final PostScrapService postScrapService;
    private final RedisDao redisDao;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, CategoryRepository categoryRepository, CommentRepository commentRepository, PostImageRepository postImageRepository, S3Service s3Service, PostLikeService postLikeService, PostScrapService postScrapService, RedisDao redisDao) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
        this.postImageRepository = postImageRepository;
        this.s3Service = s3Service;
        this.postLikeService = postLikeService;
        this.postScrapService = postScrapService;
        this.redisDao = redisDao;
    }

    @Transactional
    public PostResponse createPost(PostRequest postRequest, List<MultipartFile> imageFiles) throws IOException {
        User user = userRepository.findById(postRequest.getUserId()).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        Category category = categoryRepository.findById(postRequest.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), user, category);
        Post savedPost = postRepository.save(post);

        // 이미지 업로드
        List<PostImageResponse> imageResponses = new ArrayList<>();
        if(imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                try {
                    String imageUrl = s3Service.upload(file, "post");
                    PostImage postImage = new PostImage(imageUrl, savedPost);
                    postImageRepository.save(postImage); // 이미지 저장
                    imageResponses.add(new PostImageResponse(imageUrl)); // 업로드된 이미지 URL을 리스트에 추가
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload file to S3", e);
                }
            }
        }
        return PostResponse.toDto(savedPost, false, false,null, imageResponses, user.getId());
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId, Long loginId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        boolean isLiked = postLikeService.isLiked(loginId, postId);
        boolean isScrapped = postScrapService.isScrapped(loginId, postId);

        List<PostImage> postImages = postImageRepository.findByPostId(postId);
        List<PostImageResponse> imageResponses = postImages.stream()
                .map(PostImageResponse::new)
                .collect(Collectors.toList());

        // Post에 속한 모든 댓글을 조회하여 CommentResponse로 변환하여 리스트에 추가
        List<Comment> postComments = commentRepository.findByPostIdAndParentCommentIsNull(post.getId());
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment comment : postComments) {
            List<Comment> replies = commentRepository.findByParentCommentId(comment.getId());
            List<CommentResponse> replyResponses = replies.stream()
                    .map(reply -> CommentResponse.toDto(reply, null, loginId))
                    .collect(Collectors.toList());
            commentResponses.add(CommentResponse.toDto(comment, replyResponses, loginId));
        }

        String redisKey = "post:" + post.getId().toString();
        String redisUserKey = "user:" + loginId.toString();
        String values = redisDao.getValues(redisKey);
        int views = 0;
        if (values != null) {
            views = Integer.parseInt(values);
        } else {
            values = "0";
        }

        if(!redisDao.getValuesList(redisUserKey).contains(redisKey)) {
            redisDao.setValuesList(redisUserKey, redisKey);
            redisDao.setKeyExpiry(redisUserKey, Duration.ofHours(24));
            views = Integer.parseInt(values) + 1;
            redisDao.setValues(redisKey, String.valueOf(views));
        }
        post.setView(views);
        return PostResponse.toDto(post, isScrapped, isLiked, commentResponses, imageResponses, loginId);
    }

    @Transactional(readOnly = true)
    public Page<PostListResponse> getPostsByCategoryId(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        Page<Post> posts = postRepository.findByCategoryId(categoryId, pageable);
        return posts.map(PostListResponse::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PostListResponse> searchPostsByCategoryId(Long categoryId, String keyword, Pageable pageable) {
        Page<Post> posts = postRepository.findByCategoryIdAndKeyword(categoryId, keyword, pageable);
        return posts.map(PostListResponse::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PostListResponse> searchPosts(String keyword, Pageable pageable) {
        Page<Post> posts = postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        return posts.map(PostListResponse::toDto);
    }

    @Transactional(readOnly = true)
    public List<PostListResponse> getPostsByView() {
        List<Post> posts = postRepository.findTop20ByViewGreaterThanOrderByViewDesc(1);
        return posts.stream().map(PostListResponse::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostListResponse> getPostsByLikeCount() {
        List<Post> posts = postRepository.findTop20ByLikeCountGreaterThanOrderByLikeCountDesc(0);
        return posts.stream().map(PostListResponse::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostListResponse> getPostsByCommentCount() {
        List<Post> posts = postRepository.findTop20ByCommentCountGreaterThanOrderByCommentCountDesc(0);
        return posts.stream().map(PostListResponse::toDto).collect(Collectors.toList());
    }

    @Transactional
    public Long update(Long postId, PostEditRequest postEditRequest, List<MultipartFile> imageFiles) throws Exception {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        // 기존에 첨부한 이미지 URL 가져오기
        List<String> currentImageUrls = post.getPostImages().stream()
                .map(PostImage::getUrl)
                .collect(Collectors.toList());

        // 기존 이미지에서 삭제된 이미지 추출
        List<String> deleteImageUrls = currentImageUrls.stream()
                .filter(url -> !postEditRequest.getCurrentImageUrls().contains(url))
                .collect(Collectors.toList());

        // 삭제된 파일을 S3와 DB에서 삭제
        deleteS3Image(deleteImageUrls);
        for (String imageUrl : deleteImageUrls) {
            PostImage postImage = postImageRepository.findByUrl(imageUrl).orElseThrow(() -> new IllegalArgumentException("Invalid PostImage url"));
            post.removePostImage(postImage);
            postImageRepository.delete(postImage);
        }

        // 새로 첨부할 이미지 URL 리스트
        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                // S3에 업로드하고 URL 가져오기
                String imageUrl = s3Service.upload(file, "post");
                PostImage postImage = new PostImage(imageUrl, post);
                post.addPostImage(postImage); // 새로 추가된 이미지를 Post에 추가
                postImageRepository.save(postImage); // PostImage 저장
            }
        }
        // 게시글 업데이트
        post.update(postEditRequest.getTitle(), postEditRequest.getContent(), postEditRequest.isEdited());
        return postId;
    }

    @Transactional
    public void delete(Long postId) throws Exception {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        List<String> imageUrls = post.getPostImages().stream()
                .map(PostImage::getUrl)
                .collect(Collectors.toList());

        deleteS3Image(imageUrls);
        redisDao.deleteValues(postId.toString());
        postRepository.delete(post);
    }

    @Transactional
    public void deleteS3Image(List<String> imageUrls) throws Exception {
        for (String imageUrl : imageUrls) {
            URI uri = new URI(imageUrl);
            String fileName = uri.getPath().substring(1);
            s3Service.deleteFile(fileName);
        }
    }
}