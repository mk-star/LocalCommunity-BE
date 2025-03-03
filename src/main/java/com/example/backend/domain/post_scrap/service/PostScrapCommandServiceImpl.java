package com.example.backend.domain.post_scrap.service;

import com.example.backend.domain.post.entity.Post;
import com.example.backend.domain.post.exception.PostErrorCode;
import com.example.backend.domain.post.exception.PostException;
import com.example.backend.domain.post.repository.PostRepository;
import com.example.backend.domain.post_scrap.converter.PostScrapConverter;
import com.example.backend.domain.post_scrap.repository.PostScrapRepository;
import com.example.backend.domain.user.User;
import com.example.backend.domain.user.UserRepository;
import com.example.backend.domain.user.exception.UserErrorCode;
import com.example.backend.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostScrapCommandServiceImpl implements PostScrapCommandService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostScrapRepository postScrapRepository;

    @Transactional
    @Override
    public void scrapPost(Long postId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

        if (postScrapRepository.existsByPostAndUser(post, user)) {
            postScrapRepository.deleteByPostAndUser(post, user);
        } else {
            postScrapRepository.save(PostScrapConverter.toPostScrap(post, user));
            post.setScrapCount(post.getScrapCount()+ 1);
        }
    }
}
