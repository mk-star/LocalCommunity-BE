package com.example.backend.post_like;


import com.example.backend.post.Post;
import com.example.backend.post.PostRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostLikeService(PostLikeRepository postLikeRepository, PostRepository postRepository, UserRepository userRepository) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void likePost(Long userId, Long postId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        PostLike postLike = new PostLike(user, post);
        postLikeRepository.save(postLike);
        post.increaseLikeCount();
    }

    public boolean isLiked(Long userId, Long postId) {
        return postLikeRepository.findByUserIdAndPostId(userId, postId).isPresent();
    }
}
