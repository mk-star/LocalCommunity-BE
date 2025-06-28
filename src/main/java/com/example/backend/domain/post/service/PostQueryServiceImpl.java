package com.example.backend.domain.post.service;

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

    @Transactional
    public PostResponseDTO.PostPreViewDTO getPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

        long view = increaseViewCont(post, postId, userId);

        return PostConverter.postPreViewDTO(post, view);
    }

    private Long increaseViewCont(Post post, Long postId, Long userId) {
        String postKey = "post:" + postId;
        Long view = 0L;

        if (redisUtil.get(postKey) == null) {
            view = post.getView();
            redisUtil.set(postKey, String.valueOf(view));
            redisUtil.expire(postKey, 25, TimeUnit.HOURS);
        } else {
            view = Long.parseLong(redisUtil.get(postKey));
        }

        String userKey = "user:" + userId + ":post:" + postId;

        if (!isVisited(userKey)) {
            redisUtil.set(userKey, "1");
            redisUtil.expire(userKey, 24, TimeUnit.HOURS);
            view = redisUtil.incr(postKey, 1L);
        }
        return view;
    }

    // Redis에서 해당 유저가 이 게시글을 방문한 적이 있는지 확인
    private boolean isVisited(String visitedKey) {
        return redisUtil.exists(visitedKey);
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
