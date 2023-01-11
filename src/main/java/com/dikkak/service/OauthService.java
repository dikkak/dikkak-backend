package com.dikkak.service;

import com.dikkak.dto.auth.GetLoginRes;
import com.dikkak.dto.auth.token.TokenResponse;
import com.dikkak.common.BaseException;
import com.dikkak.entity.user.ProviderTypeEnum;
import com.dikkak.entity.user.User;
import com.dikkak.redis.RedisService;
import com.dikkak.redis.SocialToken;
import com.dikkak.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.dikkak.common.ResponseMessage.ALREADY_REGISTERED_SOCIAL_LOGIN;
import static com.dikkak.common.ResponseMessage.EXPIRED_TOKEN;
import static com.dikkak.common.ResponseMessage.INVALID_PROVIDER;
import static com.dikkak.common.ResponseMessage.LOGIN_FAILURE;
import static com.dikkak.common.ResponseMessage.LOGOUT_FAILURE;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OauthService {

    // application-oauth properties 정보를 담고 있음
    private final InMemoryClientRegistrationRepository inMemoryRepository;

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;


    /**
     * @param code 인가 코드
     * @param providerName kakao, google, facebook
     * 1. 인가 코드로 토큰을 받아오고
     * 2. 토큰을 통해 회원 정보를 받아온다.
     * 3. 회원 정보를 통해 로그인 및 회원가입을 진행한다.
     * 4. Redis에 토큰 저장
     */
    public GetLoginRes login(String providerName, String code) {
        ClientRegistration provider = inMemoryRepository.findByRegistrationId(providerName);

        // 1. 토큰을 받아온다.
        TokenResponse token = getToken(code, provider, providerName);

        // 2. 회원 정보를 받아온다.
        Map<String, Object> userProfile = getUserProfile(provider, token);
        String email = getEmail(providerName, userProfile);

        // 3. 로그인 및 회원가입
        User user = getUserByEmail(email)
                .orElseGet(() -> userService.create(
                        User.builder()
                                .email(email)
                                .providerType(ProviderTypeEnum.valueOf(providerName.toUpperCase()))
                                .build()
                        )
                );

        // 다른 provider 로 등록되어 있는 경우
        if (!user.getProviderType().toString().equals(providerName.toUpperCase())) {
            throw new BaseException(ALREADY_REGISTERED_SOCIAL_LOGIN);
        }

        // 4. Redis에 토큰 저장
        if(userProfile.get("id") != null) {
            redisService.saveSocialToken(user.getId(), user.getProviderType(), token, userProfile.get("id").toString());
        } else {
            redisService.saveSocialToken(user.getId(), user.getProviderType(), token, null);
        }

        // 토큰 발급
        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        return GetLoginRes.builder()
                .username(user.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 소셜 로그아웃
     */
    public void logout(Long userId) {
        SocialToken token = redisService.getToken(userId);

        try {
            // 구글 로그아웃
            if(token.getProvider().equals(ProviderTypeEnum.GOOGLE)) {
                WebClient.create()
                        .post()
                        .uri("https://oauth2.googleapis.com/revoke?token=" + token.getToken())
                        .headers(header -> {
                            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                            header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                        })
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            }
            // 페이스북 로그아웃
            else if (token.getProvider().equals(ProviderTypeEnum.FACEBOOK)) {
                String providerUserId = token.getProviderUserId();
                boolean success = Objects.requireNonNull(
                        WebClient.create()
                                .delete()
                                .uri("https://graph.facebook.com/" + providerUserId + "/permissions?access_token=" + token.getToken())
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<Map<String, Boolean>>() {})
                                .block())
                        .get("success");

                if (!success) { //로그아웃 실패
                    throw new BaseException(LOGOUT_FAILURE);
                }
            }
            // 토큰 삭제
            redisService.deleteToken(token);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(EXPIRED_TOKEN);
        }
    }

    /**
     * TOKEN URI에 인가 코드로 토큰을 요청한다.
     * @return TokenResponse
     */
    private TokenResponse getToken(String code, ClientRegistration provider, String providerName) {
        try {
            if (providerName.equals("facebook")) { // facebook은 get 방식
                return WebClient
                        .create()
                        .get()
                        .uri(getUri(code, provider))
                        .headers(header -> {
                            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                            header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                        })
                        .retrieve()
                        .bodyToMono(TokenResponse.class)
                        .block();
            }
            else {  //나머지는 post 방식
                return WebClient.create()
                        .post()
                        .uri(provider.getProviderDetails().getTokenUri())
                        .headers(header -> {
                            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                            header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                        })
                        .bodyValue(getTokenRequestBody(provider, code))
                        .retrieve()
                        .bodyToMono(TokenResponse.class)
                        .block();
            }
        } catch (Exception e) {
            throw new BaseException(LOGIN_FAILURE);
        }
    }

    /**
     * facebook 로그인 시 사용
     * @return 파라미터를 추가한 uri
     */
    private String getUri(String code, ClientRegistration provider) {
        return provider.getProviderDetails().getTokenUri() + "?" +
                "client_id="+ provider.getClientId()+"&"+
                "client_secret="+ provider.getClientSecret()+"&"+
                "code="+ code +"&"+
                "redirect_uri="+ provider.getRedirectUri();
    }

    /**
     * kakao, google 로그인 시 사용
     * @return 토큰 요청 body
     */
    private MultiValueMap<String, String> getTokenRequestBody(ClientRegistration provider, String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", provider.getRedirectUri());
        formData.add("client_id", provider.getClientId());
        formData.add("client_secret", provider.getClientSecret());
        return formData;
    }

    /**
     * Access Token으로 회원 정보를 요청한다.
     * @return 회원 정보
     */
    private Map<String, Object> getUserProfile(ClientRegistration provider, TokenResponse tokenResponse) {
        try {
            return WebClient.create()
                    .get()
                    .uri(provider.getProviderDetails().getUserInfoEndpoint().getUri())
                    .headers(header -> header.setBearerAuth(tokenResponse.getAccessToken())) // Authorization: Bearer ${ACCESS_TOKEN}
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            throw new BaseException(LOGIN_FAILURE);
        }
    }


    private String getEmail(String providerName, Map<String, Object> userProfile) {
        if(providerName.equals("google")) {
            return (String) userProfile.get("email");
        } else if(providerName.equals("kakao")) {
            return (String) ((Map<String, Object>) userProfile.get("kakao_account")).get("email");
        } else if(providerName.equals("facebook")) {
            return (String) userProfile.get("email");
        } else {
            throw new BaseException(INVALID_PROVIDER);
        }
    }

    private Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


}

