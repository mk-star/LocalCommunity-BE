package com.example.backend.mypage.service;

import com.example.backend.comment.Comment;
import com.example.backend.comment.CommentDto;
import com.example.backend.comment.CommentRepository;
import com.example.backend.comment_like.CommentLike;
import com.example.backend.comment_like.CommentLikeDto;
import com.example.backend.comment_like.CommentLikeRepository;
import com.example.backend.config.S3Service;
import com.example.backend.post.Post;
import com.example.backend.post.PostDto;
import com.example.backend.post.PostRepository;
import com.example.backend.post_like.PostLike;
import com.example.backend.post_like.PostLikeDto;
import com.example.backend.post_like.PostLikeRepository;
import com.example.backend.post_scrap.PostScrap;
import com.example.backend.post_scrap.PostScrapDto;
import com.example.backend.post_scrap.PostScrapRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserDto;
import com.example.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MyPageService {

    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostScrapRepository postScrapRepository;

    private final S3Service s3Service;

    @Value("${default.profile.image.url}")
    private String defaultProfileImageUrl;

    @Autowired
    public MyPageService(PostLikeRepository postLikeRepository, CommentLikeRepository commentLikeRepository, UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository, PostScrapRepository postScrapRepository, S3Service s3Service) {
        this.postLikeRepository = postLikeRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postScrapRepository = postScrapRepository;
        this.s3Service = s3Service;
    }

    //User 정보 불러오기
    public UserDto getUserById(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));
        return convertToDto(user);
    }

    //User 정보 수정하기
    public UserDto updateUser(String userId, UserDto userDto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        user.setUsername(userDto.getUsername());
        user.setAddress(userDto.getAddress());
        user.setPhone(userDto.getPhone());
        user.setEmail(userDto.getEmail());
        user.setNickname(userDto.getNickname());
        user.setProfile_url(userDto.getProfile_url());

        userRepository.save(user);
        return convertToDto(user);
    }

    // 프로필 이미지 업데이트
    public String updateProfileImage(String userId, MultipartFile profileImage) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        try {

            String existingProfileUrl = user.getProfile_url();

            // 기존 프로필 이미지가 기본 이미지가 아니면 삭제
            if (existingProfileUrl != null && !existingProfileUrl.equals(defaultProfileImageUrl)) {
                try {
                    // 기존 URL에서 전체 파일 경로를 추출하여 S3에서 삭제
                    String existingFilePath = existingProfileUrl.substring(existingProfileUrl.indexOf("mypage/"));
                    s3Service.deleteFile(existingFilePath);
                } catch (Exception e) {
                    System.err.println("Failed to delete previous profile image from S3: " + e.getMessage());
                }
            }

            // 사용자 ID를 포함한 고정된 이미지 파일 이름 생성
            String newFileName = "mypage/" + UUID.randomUUID();
            String imageUrl = s3Service.upload(profileImage, newFileName);

            // 새로운 이미지 URL로 업데이트
            user.setProfile_url(imageUrl);
            userRepository.save(user);
            return imageUrl;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile image", e);
        }
    }

    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setUsername(user.getUsername());
        userDto.setAddress(user.getAddress());
        userDto.setPhone(user.getPhone());
        userDto.setEmail(user.getEmail());
        userDto.setNickname(user.getNickname());
        userDto.setProfile_url(user.getProfile_url());
        return userDto;
    }

    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + id));
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedDate(),
                post.getModifiedDate(),
                post.getUser().getUsername(),
                post.getLikeCount(),
                post.getComments().size(),
                post.getCategory().getName()
        );
    }


    // 본인이 작성한 글 불러오기 (페이징 추가)
    /**
     * 특정 사용자의 게시물 페이징 처리 및 반환
     *
     * @param userId 사용자 ID
     * @param page   현재 페이지 번호
     * @param size   페이지 당 항목 수
     * @return List<PostDto> 페이징된 게시물 리스트
     */
    public List<PostDto> getPostsByUserId(String userId, int page, int size) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        // 페이징 요청 객체 생성 (최신순 정렬)
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        // 페이징 처리된 게시물 가져오기
        return postRepository.findByUser(user, pageRequest)
                .stream()
                .map(post -> new PostDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getCreatedDate(),
                        post.getModifiedDate(),
                        post.getUser().getUsername(),
                        post.getLikeCount(),
                        post.getComments().size(),
                        post.getCategory().getName()
                ))
                .collect(Collectors.toList());
    }
    /**
     * 특정 사용자의 전체 게시물 수 반환
     *
     * @param userId 사용자 ID
     * @return long 총 게시물 수
     */
    public long getPostCountByUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        return postRepository.countByUser(user);
    }


    // 본인이 댓글을 단 게시글 가져오기
    public List<PostDto> getCommentedPostsByUserId(String userId, int page, int size) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        // 사용자가 작성한 댓글을 모두 가져옴
        List<Post> commentedPosts = commentRepository.findByUser(user).stream()
                .map(Comment::getPost) // 댓글이 달린 게시글 추출
                .distinct() // 중복 게시글 제거
                .sorted((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate())) // 최신순 정렬
                .collect(Collectors.toList());

        // 총 게시글 수
        int totalItems = commentedPosts.size();

        // 페이징 처리
        int start = Math.min(page * size, totalItems); // 시작 인덱스
        int end = Math.min(start + size, totalItems); // 종료 인덱스
        List<Post> paginatedPosts = commentedPosts.subList(start, end);

        // Post를 PostDto로 변환
        return paginatedPosts.stream()
                .map(post -> new PostDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getCreatedDate(),
                        post.getModifiedDate(),
                        post.getUser() != null ? post.getUser().getUsername() : "탈퇴한 사용자",
                        post.getLikeCount(),
                        post.getComments().size(),
                        post.getCategory().getName()
                ))
                .collect(Collectors.toList());
    }

    public long getCommentedPostCountByUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        // 중복 제거된 게시글 수 반환
        return commentRepository.findByUser(user).stream()
                .map(Comment::getPost) // 댓글이 달린 게시글 추출
                .distinct() // 중복 게시글 제거
                .count();
    }


    /**
     * 사용자가 스크랩한 게시글을 페이징 처리하여 가져옵니다.
     *
     * @param userId 사용자 ID
     * @param page   페이지 번호
     * @param size   페이지 당 항목 수
     * @return List<PostScrapDto> 스크랩한 게시글 리스트
     */
    public List<PostScrapDto> getScrappedPostsByUserId(String userId, int page, int size) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        // 페이징 요청 객체 생성 (최신순 정렬)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        // 스크랩 데이터를 페이징 처리하여 가져오기
        Page<PostScrap> scrapsPage = postScrapRepository.findByUser(user, pageable);

        // 스크랩 데이터를 DTO로 변환
        return scrapsPage.stream()
                .map(postScrap -> new PostScrapDto(
                        postScrap.getPost().getId(),
                        postScrap.getPost().getTitle(),
                        postScrap.getPost().getContent(),
                        postScrap.getPost().getCreatedDate(),
                        postScrap.getPost().getModifiedDate(),
                        postScrap.getPost().getUser() != null ? postScrap.getPost().getUser().getUsername() : "탈퇴한 사용자",
                        postScrap.getPost().getLikeCount(),
                        postScrap.getPost().getCommentCount(),
                        postScrap.getPost().getCategory().getName()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 스크랩한 게시글의 총 개수를 반환합니다.
     *
     * @param userId 사용자 ID
     * @return long 총 스크랩 게시글 수
     */
    public long getScrappedPostCountByUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        return postScrapRepository.countByUser(user);
    }
}
