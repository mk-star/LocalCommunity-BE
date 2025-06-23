package com.example.backend.domain.post_like.service;

import com.example.backend.domain.post.entity.Post;
import com.example.backend.domain.post.exception.PostErrorCode;
import com.example.backend.domain.post.exception.PostException;
import com.example.backend.domain.post.repository.PostRepository;
import com.example.backend.domain.post_like.converter.PostLikeConverter;
import com.example.backend.domain.post_like.repository.PostLikeRepository;
import com.example.backend.domain.user.User;
import com.example.backend.domain.user.UserRepository;
import com.example.backend.domain.user.exception.UserErrorCode;
import com.example.backend.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeCommandServiceImpl implements PostLikeCommandService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    @Override
    public void likePost(Long postId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

        if (postLikeRepository.existsByPostAndUser(post, user)) {
            postLikeRepository.deleteByPostAndUser(post, user);
        } else {
            postLikeRepository.save(PostLikeConverter.toPostLike(post, user));
            post.setLikeCount(post.getLikeCount() + 1);
        }
    }
}
