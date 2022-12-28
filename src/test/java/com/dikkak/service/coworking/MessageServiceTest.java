package com.dikkak.service.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.dto.PageCustom;
import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.dto.coworking.message.Message;
import com.dikkak.dto.coworking.message.TextReq;
import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.CoworkingFile;
import com.dikkak.entity.coworking.CoworkingMessage;
import com.dikkak.entity.proposal.CategoryEnum;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.user.ProviderTypeEnum;
import com.dikkak.entity.user.User;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.repository.UserRepository;
import com.dikkak.repository.coworking.CoworkingRepository;
import com.dikkak.repository.coworking.file.CoworkingFileRepository;
import com.dikkak.repository.coworking.message.CoworkingMessageRepository;
import com.dikkak.repository.proposal.ProposalRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.dikkak.common.ResponseMessage.UNAUTHORIZED_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class MessageServiceTest {

    @Autowired
    MessageService messageService;
    
    @Autowired
    CoworkingMessageRepository messageRepository;

    @Autowired
    CoworkingRepository coworkingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProposalRepository proposalRepository;

    @Autowired
    CoworkingFileRepository fileRepository;

    private Proposal proposal;
    private Coworking coworking;

    @BeforeEach
    @Transactional
    void init() {
        User client = userRepository.save(User.builder()
                .email("client@naver.com")
                .providerType(ProviderTypeEnum.KAKAO)
                .build());
        User designer = userRepository.save(User.builder()
                .email("designer@gmail.com")
                .providerType(ProviderTypeEnum.KAKAO)
                .build());

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
    void tearDown() {
        messageRepository.deleteAll();
        coworkingRepository.deleteAll();
        proposalRepository.deleteAll();
        userRepository.deleteAll();
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
        PageCustom<Message<GetChattingRes>> messageList1 = messageService.getMessageList(coworking, PageRequest.of(0, 2));
        PageCustom<Message<GetChattingRes>> messageList2 = messageService.getMessageList(coworking, PageRequest.of(1, 2));

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
    @DisplayName("외주작업실 권한이 있는 회원이 텍스트 메시지를 전송한다.")
    @Transactional
    void AuthorizedUserSendTextMessage() {
        //given
        User designer = coworking.getDesigner();
        designer.setUserType(UserTypeEnum.DESIGNER);

        TextReq textReq = new TextReq();
        textReq.setEmail(designer.getEmail());
        textReq.setContent("내용");
        textReq.setCoworkingId(coworking.getId());
        System.out.println("textReq = " + textReq);

        //when
        CoworkingMessage message = messageService.saveTextMessage(textReq, coworking);

        //then
        assertThat(message.getUser()).isEqualTo(designer);
        assertThat(message.getCoworkingFile()).isNull();
        assertThat(message.getCoworking()).isEqualTo(coworking);
        assertThat(message.getContent()).isEqualTo(textReq.getContent());
    }

    @Test
    @DisplayName("권한이 없는 회원이 메시지를 전송하면 예외가 발생한다.")
    @Transactional
    void UnauthorizedUserSendTextMessage() {
        //given
        User user = userRepository.save(User.builder()
                .email("user@email.com")
                .providerType(ProviderTypeEnum.KAKAO)
                .build());

        TextReq textReq = new TextReq();
        textReq.setEmail(user.getEmail());
        textReq.setContent("내용");
        textReq.setCoworkingId(coworking.getId());
        System.out.println("textReq = " + textReq);

        //when, then
        BaseException exception = assertThrows(BaseException.class,
                () -> messageService.saveTextMessage(textReq, coworking));
        assertThat(exception.getResponseMessage()).isEqualTo(UNAUTHORIZED_REQUEST);
    }
}
