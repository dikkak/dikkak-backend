package com.dikkak.service;

import com.dikkak.common.BaseException;
import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.dto.coworking.Message;
import com.dikkak.entity.coworking.*;
import com.dikkak.entity.user.ProviderTypeEnum;
import com.dikkak.entity.user.User;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.repository.UserRepository;
import com.dikkak.repository.coworking.CoworkingFileRepository;
import com.dikkak.repository.coworking.CoworkingMessageRepository;
import com.dikkak.repository.coworking.CoworkingStepRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CoworkingServiceTest {

    @Autowired
    CoworkingService coworkingService;

    @Autowired
    CoworkingMessageRepository messageRepository;

    @Autowired
    CoworkingFileRepository fileRepository;

    @Autowired
    CoworkingStepRepository stepRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("디자이너 매칭 & 외주작업실 생성")
    @Transactional
    void create() throws BaseException {
        //given
        User designer = userRepository.save(User.builder()
                .email("user1@naver.com")
                .providerType(ProviderTypeEnum.KAKAO)
                .build());
        designer.setUserType(UserTypeEnum.DESIGNER);

        //when
        Coworking savedCoworking = coworkingService.create(designer, 1L);

        //then
        assertThat(savedCoworking.getDesigner().getId()).isEqualTo(designer.getId());
        assertThat(savedCoworking.getProgress()).isEqualTo(StepType.CHECK_PROPOSAL);
    }

    @Test
    @DisplayName("채팅 목록 조회")
    @Transactional
    void getMessageList() throws BaseException, InterruptedException {
        //given
        User user1 = userRepository.save(User.builder()
                .email("user1@naver.com")
                .providerType(ProviderTypeEnum.KAKAO)
                .build());
        User user2 = userRepository.save(User.builder()
                .email("user2@naver.com")
                .providerType(ProviderTypeEnum.KAKAO)
                .build());
        CoworkingStep coworkingStep = stepRepository.findById(1L).get();

        CoworkingMessage message1 = messageRepository.save(new CoworkingMessage(1L, user1, null, "텍스트 메시지1", coworkingStep));
        Thread.sleep(1000);

        CoworkingMessage message2 = messageRepository.save(new CoworkingMessage(2L, user2, null, "텍스트 메시지2", coworkingStep));


        Thread.sleep(1000);
        CoworkingFile file = fileRepository.save(CoworkingFile.builder().fileUrl("url1").fileName("파일 이름").coworkingStep(coworkingStep).build());
        CoworkingMessage message3 = messageRepository.save(new CoworkingMessage(3L, user1, file, null, coworkingStep));

        //when
        List<Message<GetChattingRes>> messageList = coworkingService.getMessageList(1L, StepType.CHECK_PROPOSAL);

        //then
        for (int i=messageList.size()-3; i<messageList.size(); i++)
            System.out.println(i + " = " + messageList.get(i));

        assertThat(messageList.get(messageList.size()-3).getData().getEmail()).isEqualTo(user1.getEmail());
        assertThat(messageList.get(messageList.size()-3).getData().getContent()).isEqualTo(message1.getContent());

        assertThat(messageList.get(messageList.size()-2).getData().getEmail()).isEqualTo(user2.getEmail());
        assertThat(messageList.get(messageList.size()-2).getData().getContent()).isEqualTo(message2.getContent());

        assertThat(messageList.get(messageList.size()-1).getData().getEmail()).isEqualTo(user1.getEmail());
        assertThat(messageList.get(messageList.size()-1).getData().getFileUrl()).isEqualTo(message3.getCoworkingFile().getFileUrl());
        assertThat(messageList.get(messageList.size()-1).getData().getFileName()).isEqualTo(message3.getCoworkingFile().getFileName());
    }
}