package com.example.backend.user;

import com.example.backend.comment.CommentRepository;
import com.example.backend.comment_like.CommentLikeRepository;
import com.example.backend.post.PostRepository;
import com.example.backend.post_like.PostLikeRepository;
import com.example.backend.post_scrap.PostScrapRepository;
import com.example.backend.region.RegionRepository;
import com.example.backend.signLogin.JoinRequest;
import com.example.backend.signLogin.LoginRequest;
import com.example.backend.user.kakao.service.KakaoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final KakaoService kakaoService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostScrapRepository postScrapRepository;
    private final RegionRepository regionRepository;

    @Value("${default.profile.image.url}")
    private String defaultProfileImageUrl;


    // 인증 메서드: 유저 ID와 비밀번호로 인증
    public boolean authenticate(String userId, String password) {
        /*
        * User user = userRepository.findByUserId(userId);
        return user != null && encoder.matches(password, user.getPassword());
        * */
        Optional<User> optionalUser = userRepository.findByuserId(userId);
        if (optionalUser.isEmpty()) {
            return false;
        }
        User user = optionalUser.get();
        return encoder.matches(password, user.getPassword());
    }

    // 유저 ID로 사용자 찾기
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByUserId(String userId) {
        return userRepository.findByuserId(userId).orElse(null);
    }

    public String findUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 이메일로 등록된 유저가 없습니다."));
        return user.getUserId();
    }

    public String findEmailByUserId(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("해당 이름의 유저가 없습니다."));
        return user.getEmail();
    }

    // 유저 ID 중복 체크
    public boolean checkuserIdDuplicate(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 닉네임 중복 체크
    public boolean checkNicknameDuplicate(String nickname) { return userRepository.existsByNickname(nickname); }

    public boolean checkKakaoUserExists(String kakaoUserId) {
        return userRepository.existsByUserId(kakaoUserId);
    }

    public boolean checkUserEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkUserIdExists(String userId) {
        return userRepository.existsByUserId(userId);
    }



    // 회원 가입 - 비밀번호 인코딩 후 저장
    public void join2(JoinRequest req) {
        // 기본 프로필 이미지 URL 설정
        userRepository.save(req.toEntity(encoder.encode(req.getPassword()), defaultProfileImageUrl));
    }

    // 로그인 메서드
    public User login(LoginRequest req) {
        Optional<User> optionalUser = userRepository.findByuserId(req.getUserId());

        if (optionalUser.isEmpty()) {
            System.out.println("Login ID not found");
            return null;
        }

        User user = optionalUser.get();
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            System.out.println("Password does not match");
            return null;
        }

        return user;
    }

    // 유저 ID로 로그인한 사용자 정보 반환
    public User getLoginUser(Long userId) {
        return getLoginUserById(userId);
    }

    // 유저 ID로 로그인한 사용자 정보 반환 (Long 타입)
    public User getLoginUserById(Long userId) {
        if (userId == null) return null;
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.orElse(null);
    }

    // 유저 ID로 로그인한 사용자 정보 반환 (String 타입)
    public User getLoginUserByuserId(String userId) {
        if (userId == null) return null;
        Optional<User> optionalUser = userRepository.findByuserId(userId);
        return optionalUser.orElse(null);
    }

    public void updatePassword(String email, String newPassword){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        String encodedPassword = encoder.encode(newPassword);

        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    @Transactional
    public boolean deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            updateToDeletedUser(id);
            userRepository.delete(user);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteKakaoUser(Long id, String kakaoUserId) {
        boolean isUnlinked = kakaoService.unlinkKakaoUser(kakaoUserId);

        if(isUnlinked) {
            updateToDeletedUser(id);
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public void updateToDeletedUser(Long userId) {
        commentRepository.updateCommentsToDeletedUser(userId, getDeletedUserPlaceholder());
        commentLikeRepository.updateCommentLikeToDeletedUser(userId, getDeletedUserPlaceholder());
        postRepository.updatePostsToDeletedUser(userId, getDeletedUserPlaceholder());
        postLikeRepository.updatePostLikeToDeletedUser(userId, getDeletedUserPlaceholder());
        postScrapRepository.updatePostScrapToDeletedUser(userId, getDeletedUserPlaceholder());
        regionRepository.updateRegionToDeletedUser(userId, getDeletedUserPlaceholder());
    }

    @Transactional
    public User getDeletedUserPlaceholder() {
        return userRepository.findByUserId("deleted_user")
                .orElseGet(() -> {
                    User deletedUser = User.builder()
                            .userId("deleted_user")
                            .username("탈퇴한 사용자")
                            .password("")
                            .email("")
                            .nickname("탈퇴한 사용자")
                            .build();
                    return userRepository.save(deletedUser);
                });
    }
}
