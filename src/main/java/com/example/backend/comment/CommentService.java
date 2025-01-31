package com.example.backend.comment;

import com.example.backend.post.Post;
import com.example.backend.post.PostRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final EntityManager entityManager;

    @Autowired
    public CommentService(UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public CommentResponse createComment(CommentRequest commentRequest) {
        User user = userRepository.findById(commentRequest.getUserId()).orElseThrow(() -> new IllegalArgumentException("Invalid parent user ID"));
        Post post = postRepository.findById(commentRequest.getPostId()).orElseThrow(() -> new IllegalArgumentException("Invalid parent post ID"));

        Comment parentComment = null;
        if (commentRequest.getParentId() != null) { // 자식 댓글이라면
            //부모 Comment를 찾음
            parentComment = commentRepository.findById(commentRequest.getParentId()).orElseThrow(() -> new IllegalArgumentException("Invalid parent comment ID"));
        }

        Comment comment = new Comment(user, post, commentRequest.getContent(), parentComment);
        // 자식 댓글을 부모 댓글에 추가 (부모 댓글이 있는 경우)
        if (parentComment != null) {
            parentComment.addChildrenComment(comment);
            commentRepository.save(parentComment);
        }
        Comment savedComment = commentRepository.save(comment);
        post.increaseCommentCount();

        // 자식 댓글 리스트를 DTO로 변환
        List<CommentResponse> children = savedComment.getChildrenComment().stream()
                .map(child -> CommentResponse.toDto(child, null, null))
                .collect(Collectors.toList());

        return CommentResponse.toDto(savedComment, children, null);
    }

    @Transactional
    public Long editComment(Long commentId, CommentEditRequest commentEditRequest) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        comment.update(commentEditRequest.getContent(), commentEditRequest.isEdited());
        return commentId;
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        Post post = postRepository.findById(comment.getPost().getId()).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        // 자식 댓글이 있는 경우
        if (!comment.getChildrenComment().isEmpty()) {
            comment.setDeleted(true);
            comment.setContent("삭제한 댓글입니다.");
            commentRepository.save(comment);
        } else {
            commentRepository.delete(comment);
            entityManager.flush();

            // 부모 댓글이 삭제된 상태인지 체크
            Comment parentComment = comment.getParentComment();

            if (parentComment != null && parentComment.getChildrenComment().isEmpty() && parentComment.isDeleted()) {
                // 부모 댓글이 삭제되지 않은 경우에만 삭제 처리
                commentRepository.delete(parentComment);
            }
        }
        post.decreaseCommentCount();
    }
}