package com.dikkak.service;

import com.dikkak.dto.PageCustom;
import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.dto.coworking.GetTaskRes;
import com.dikkak.dto.coworking.Message;
import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.CoworkingFile;
import com.dikkak.entity.coworking.CoworkingMessage;
import com.dikkak.entity.coworking.CoworkingTask;
import com.dikkak.entity.proposal.CategoryEnum;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.user.ProviderTypeEnum;
import com.dikkak.entity.user.User;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.repository.UserRepository;
import com.dikkak.repository.coworking.CoworkingRepository;
import com.dikkak.repository.coworking.file.CoworkingFileRepository;
import com.dikkak.repository.coworking.message.CoworkingMessageRepository;
import com.dikkak.repository.coworking.task.CoworkingTaskRepository;
import com.dikkak.repository.proposal.ProposalRepository;
import com.dikkak.service.coworking.CoworkingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CoworkingServiceTest {

    @Autowired
    CoworkingService coworkingService;

    @Autowired
    CoworkingRepository coworkingRepository;

    @Autowired
    CoworkingMessageRepository messageRepository;

    @Autowired
    CoworkingFileRepository fileRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CoworkingTaskRepository taskRepository;

    @Autowired
    ProposalRepository proposalRepository;

    private Proposal proposal;
    private Coworking coworking;

    @BeforeEach
    void init() {
        User client = userRepository.save(User.builder()
                .email("client@naver.com")
                .providerType(ProviderTypeEnum.KAKAO)
                .build());
        client.setUserType(UserTypeEnum.CLIENT);
        User designer = userRepository.save(User.builder()
                .email("designer@gmail.com")
                .providerType(ProviderTypeEnum.KAKAO)
                .build());
        designer.setUserType(UserTypeEnum.DESIGNER);

        PostProposalReq req = new PostProposalReq();
        req.setCategory(CategoryEnum.LOGO_OR_CARD);
        req.setDeadline("2022-08-01");
        req.setTitle("제목");
        req.setMainColor("#ffffff");
        req.setPurpose("목적");
        proposal = proposalRepository.save(new Proposal(client, req));
        coworking = coworkingRepository.save(new Coworking(proposal, designer));
    }

    @AfterEach
    void teardown() {
        fileRepository.deleteAll();
        messageRepository.deleteAll();
        taskRepository.deleteAll();
        coworkingRepository.deleteAll();
        proposalRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("디자이너 매칭 & 외주작업실 생성")
    void create() {
        //given
        User designer = userRepository.save(User.builder()
                .email("user1@naver.com")
                .providerType(ProviderTypeEnum.KAKAO)
                .build());
        designer.setUserType(UserTypeEnum.DESIGNER);

        //when
        Coworking savedCoworking = coworkingService.create(designer, proposal.getId());

        //then
        assertThat(savedCoworking.getDesigner().getId()).isEqualTo(designer.getId());
        assertThat(savedCoworking.isComplete()).isFalse();
    }

    @Test
    @DisplayName("채팅 목록을 최신순으로 조회한다.")
    @Transactional
    void getMessageList() throws InterruptedException {
        //given
        User client  = proposal.getClient();
        User designer = coworking.getDesigner();

        CoworkingMessage message1 = messageRepository.save(new CoworkingMessage(1L, client, null, "텍스트 메시지1", coworking));
        Thread.sleep(1000);

        CoworkingMessage message2 = messageRepository.save(new CoworkingMessage(2L, designer, null, "텍스트 메시지2", coworking));
        Thread.sleep(1000);

        CoworkingFile file = fileRepository.save(CoworkingFile.builder().fileUrl("url1").fileName("파일 이름").coworking(coworking).build());
        CoworkingMessage message3 = messageRepository.save(new CoworkingMessage(3L, client, file, null, coworking));

        //when
        PageCustom<Message<GetChattingRes>> messageList1 = coworkingService.getMessageList(coworking, PageRequest.of(0, 2));
        PageCustom<Message<GetChattingRes>> messageList2 = coworkingService.getMessageList(coworking, PageRequest.of(1, 2));

        //then
        assertThat(messageList1.getContent().size()).isEqualTo(2);
        assertThat(messageList1.isHasNext()).isTrue();
        assertThat(messageList1.isHasPrev()).isFalse();

        assertThat(messageList1.getContent().get(1).getData().getEmail()).isEqualTo(designer.getEmail());
        assertThat(messageList1.getContent().get(1).getData().getContent()).isEqualTo(message2.getContent());
        assertThat(messageList1.getContent().get(0).getData().getEmail()).isEqualTo(client.getEmail());
        assertThat(messageList1.getContent().get(0).getData().getFileUrl()).isEqualTo(message3.getCoworkingFile().getFileUrl());
        assertThat(messageList1.getContent().get(0).getData().getFileName()).isEqualTo(message3.getCoworkingFile().getFileName());

        assertThat(messageList2.getContent().size()).isEqualTo(1);
        assertThat(messageList2.isHasNext()).isFalse();
        assertThat(messageList2.isHasPrev()).isTrue();
        assertThat(messageList2.getContent().get(0).getData().getEmail()).isEqualTo(client.getEmail());
        assertThat(messageList2.getContent().get(0).getData().getContent()).isEqualTo(message1.getContent());
    }

    @Test
    @DisplayName("task 목록 조회")
    void getTaskList() {
        //given
        String content = "할 일1";
        taskRepository.save(CoworkingTask.of(coworking, content));

        //when
        List<Message<GetTaskRes>> taskList = coworkingService.getTaskList(coworking.getId());

        //then
        assertThat(taskList.size()).isEqualTo(1);
        assertThat(taskList.get(0).getData().getContent().equals(content)).isTrue();
        assertThat(taskList.get(0).getData().isChecked()).isFalse();
    }
}
