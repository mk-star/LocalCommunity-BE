package com.example.backend.user.kakao.service;

import com.example.backend.user.kakao.dto.KakaoTokenResponseDto;
import com.example.backend.user.kakao.dto.KakaoUserInfoResponseDto;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    @Value("${kakao.admin.key}")
    private String adminKey;
    private String clientId;
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;

    @Autowired
    public KakaoService(@Value("${kakao.client_id}") String clientId) {
        this.clientId = clientId;
        KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
        KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    }

    public String getAccessTokenFromKakao(String code) {
        KakaoTokenResponseDto kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        log.info("[Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
        log.info("[Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.getRefreshToken());
        log.info("[Kakao Service] Id Token ------> {}", kakaoTokenResponseDto.getIdToken());
        log.info("[Kakao Service] Scope ------> {}", kakaoTokenResponseDto.getScope());

        return kakaoTokenResponseDto.getAccessToken();
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        KakaoUserInfoResponseDto userInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        log.info("[Kakao Service] Auth ID ---> {}", userInfo.getId());
        log.info("[Kakao Service] NickName ---> {}", userInfo.getKakaoAccount().getProfile().getNickName());
        log.info("[Kakao Service] ProfileImageUrl ---> {}", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

        return userInfo;
    }

    public void logoutKakaoUser(String accessToken) {
        WebClient.create(KAUTH_USER_URL_HOST)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v1/user/logout")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public boolean unlinkKakaoUser(String kakaoUserId) {
        String url = "https://kapi.kakao.com/v1/user/unlink";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + adminKey);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("target_id_type", "user_id")
                .queryParam("target_id", kakaoUserId);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uriBuilder.toUriString(), HttpMethod.POST, requestEntity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
