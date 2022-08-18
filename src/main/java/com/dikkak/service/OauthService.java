package com.dikkak.service;

import com.dikkak.dto.auth.GetLoginRes;
import com.dikkak.dto.auth.token.TokenResponse;
import com.dikkak.dto.common.BaseException;
import com.dikkak.entity.ProviderTypeEnum;
import com.dikkak.entity.User;
import com.dikkak.redis.RedisService;
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
import java.util.Optional;

import static com.dikkak.dto.common.ResponseMessage.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class OauthService {

    // application-oauth properties 정보를 담고 있음
    private final InMemoryClientRegistrationRepository inMemoryRepository;

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final RedisService redisService;


    /**
     * @param code 인가 코드
     * @param providerName kakao, google, facebook
     * 1. 인가 코드로 토큰을 받아오고
     * 2. 토큰을 통해 회원 정보를 받아온다.
     * 3. 회원 정보를 통해 로그인 및 회원가입을 진행한다.
     * 4. Redis에 토큰 저장
     */
    @Transactional
    public GetLoginRes login(String providerName, String code) throws BaseException {

        ClientRegistration provider = inMemoryRepository.findByRegistrationId(providerName);

        // 1. 토큰을 받아온다.
        TokenResponse token = getToken(code, provider, providerName);


        // 2. 회원 정보를 받아온다.
        Map<String, Object> userProfile = getUserProfile(provider, token);
        String email = getEmail(providerName, userProfile);

        // 3. 로그인 및 회원가입
        Optional<User> userByEmail = getUserByEmail(email);
        User user;

        // 존재하는 회원인 경우
        if(userByEmail.isPresent()) {
            user = userByEmail.get();
            // 다른 provider로 등록되어 있는 경우
            if (!user.getProviderType().toString().equals(providerName.toUpperCase())) {
                throw new BaseException(ALREADY_REGISTERED_SOCIAL_LOGIN);
            }
        }
        // 존재하지 않는 회원인 경우 - 회원가입
        else {
            user = userService.create(User.builder()
                    .email(email)
                    .providerType(ProviderTypeEnum.valueOf(providerName.toUpperCase()))
                    .build());
        }

        // 4. Redis에 토큰 저장
        redisService.saveSocialToken(user.getId(), token.getAccessToken());


        // 토큰 발급
        String accessToken = jwtService.createAccessToken(user.getId());
        String refreshToken = jwtService.createRefreshToken(user.getId());

        return GetLoginRes.builder()
                .username(user.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 소셜 로그아웃
     */
    public void logout(Long userId) throws BaseException {
        String token = redisService.getToken(userId);

        // 구글 로그아웃 (이후에 카카오, 페이스북 추가 필요)
        String res = WebClient.create()
                .post()
                .uri("https://oauth2.googleapis.com/revoke?token=" + token)
                .headers(header -> {
                    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * TOKEN URI에 인가 코드로 토큰을 요청한다.
     * @return TokenResponse
     */
    private TokenResponse getToken(String code, ClientRegistration provider, String providerName) throws BaseException {
        try {
            if (providerName.equals("facebook")) { // facebook은 get 방식
                return WebClient.create()
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
            log.error(e.getLocalizedMessage());
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
    private Map<String, Object> getUserProfile(ClientRegistration provider, TokenResponse tokenResponse) throws BaseException {
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


    private String getEmail(String providerName, Map<String, Object> userProfile) throws BaseException {
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

