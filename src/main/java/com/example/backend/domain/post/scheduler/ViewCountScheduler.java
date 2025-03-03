package com.example.backend.domain.post.scheduler;

import com.example.backend.domain.post.entity.Post;
import com.example.backend.domain.post.exception.PostErrorCode;
import com.example.backend.domain.post.exception.PostException;
import com.example.backend.domain.post.repository.PostRepository;
import com.example.backend.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ViewCountScheduler {
    private final RedisUtil redisUtil;
    private final PostRepository postRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void updateViewCounts() {
        Set<String> redisKeys = redisUtil.keys("post:*"); // 모든 게시물의 Redis 키 가져오기
        for (String redisKey : redisKeys) {
            String redisValue = redisUtil.get(redisKey).toString();
            if (redisValue != null) {
                Long postId = Long.valueOf(redisKey.split(":")[1]);
                Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
                if (post != null) {
                    post.setView(Integer.parseInt(redisValue));
                    postRepository.save(post);
                }
            }
        }
    }
}
