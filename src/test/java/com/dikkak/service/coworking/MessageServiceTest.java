package com.dikkak.service.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.dto.message.TextReq;
import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.CoworkingMessage;
import com.dikkak.entity.proposal.CategoryEnum;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.user.ProviderTypeEnum;
import com.dikkak.entity.user.User;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.repository.UserRepository;
import com.dikkak.repository.coworking.CoworkingRepository;
import com.dikkak.repository.coworking.message.CoworkingMessageRepository;
import com.dikkak.repository.proposal.ProposalRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
