package com.example.backend.domain.post.service;

import com.example.backend.domain.category.entity.QCategory;
import com.example.backend.domain.post.converter.PostConverter;
import com.example.backend.domain.post.dto.PostResponseDTO;
import com.example.backend.domain.post.entity.Post;
import com.example.backend.domain.post.entity.QPost;
import com.example.backend.domain.post.exception.PostErrorCode;
import com.example.backend.domain.post.exception.PostException;
import com.example.backend.domain.post.repository.PostRepository;
import com.example.backend.global.util.RedisUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PostQueryServiceImpl implements PostQueryService {
    private final PostRepository postRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final RedisUtil redisUtil;

    @Transactional(readOnly = true)
    public PostResponseDTO.PostPreViewDTO getPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

        // 게시글 조회수
        String postViewKey = "post:" + post.getId();
        int view;
        if (redisUtil.get(postViewKey) == null) {
            // Redis에 게시글 조회수 0으로 세팅
            redisUtil.set(post.getId().toString(), 0);
            view = 0;
        } else {
            view = (int) redisUtil.get(postViewKey);
        }

        // 유저별 개별 게시글 방문 기록 (user:1:post:2)
        String visitedKey = "user:" + userId + ":post:" + post.getId();

        // Redis에서 해당 유저가 이 게시글을 방문한 적이 있는지 확인
        Boolean isVisted = redisUtil.exists(visitedKey);
        if (Boolean.FALSE.equals(isVisted)) {
            redisUtil.set(visitedKey, 1);
            redisUtil.expire(visitedKey, 24, TimeUnit.HOURS);
            redisUtil.set(postViewKey, view + 1);
            post.setView(view + 1);
        }
        return PostConverter.postPreViewDTO(post);
    }

    @Override
    public Page<Post> getPostList(Long categoryId, String keyword, Integer page) {
        BooleanBuilder predicate = new BooleanBuilder();
        QPost post = QPost.post;
        Pageable pageable = PageRequest.of(page - 1, 8);

        if(categoryId != null) {
            predicate.and(post.category.id.eq(categoryId));
        }

        if(keyword != null) {
            BooleanBuilder keywordPredicate = new BooleanBuilder();
            keywordPredicate.or(post.title.containsIgnoreCase(keyword))
                    .or(post.content.containsIgnoreCase(keyword));

            predicate.and(keywordPredicate);
        }

        List<Post> pagePosts =  jpaQueryFactory
                .selectFrom(post)
                .where(predicate)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(pagePosts, pageable, pageable.getPageSize());
    }
}
