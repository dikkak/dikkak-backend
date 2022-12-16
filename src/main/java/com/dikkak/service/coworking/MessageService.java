package com.dikkak.service.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.dto.message.TextReq;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.CoworkingMessage;
import com.dikkak.entity.user.User;
import com.dikkak.repository.UserRepository;
import com.dikkak.repository.coworking.CoworkingRepository;
import com.dikkak.repository.coworking.message.CoworkingMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dikkak.common.ResponseMessage.NON_EXISTENT_EMAIL;
import static com.dikkak.common.ResponseMessage.WRONG_COWORKING_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {

    private final CoworkingRepository coworkingRepository;
    private final CoworkingMessageRepository coworkingMessageRepository;
    private final UserRepository userRepository;

    /**
     * 텍스트 메시지 저장
     */
    @Transactional
    public CoworkingMessage saveTextMessage(TextReq req) throws BaseException {
        User user = getUser(req.getEmail());
        Coworking coworking = getCoworkingById(req.getCoworkingId());

        // coworking message 저장
        return coworkingMessageRepository.save(
                CoworkingMessage.builder()
                        .coworking(coworking)
                        .user(user)
                        .content(req.getContent())
                        .build());
    }

    private User getUser(String email) throws BaseException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(NON_EXISTENT_EMAIL));
    }

    private Coworking getCoworkingById(Long coworkingId) throws BaseException {
        return coworkingRepository.findById(coworkingId)
                .orElseThrow(() -> new BaseException(WRONG_COWORKING_ID));
    }




}
