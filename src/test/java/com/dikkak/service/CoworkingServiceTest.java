package com.dikkak.service;

import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.proposal.CategoryEnum;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.user.ProviderTypeEnum;
import com.dikkak.entity.user.User;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.repository.UserRepository;
import com.dikkak.repository.coworking.CoworkingRepository;
import com.dikkak.repository.coworking.message.CoworkingMessageRepository;
import com.dikkak.repository.proposal.ProposalRepository;
import com.dikkak.service.coworking.CoworkingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
    UserRepository userRepository;

    @Autowired
    ProposalRepository proposalRepository;

    private Proposal proposal;

    @BeforeEach
    void init() {
        User client = userRepository.save(User.builder()
                .email("client@naver.com")
                .providerType(ProviderTypeEnum.KAKAO)
                .build());
        client.setUserType(UserTypeEnum.CLIENT);

        PostProposalReq req = new PostProposalReq();
        req.setCategory(CategoryEnum.LOGO_OR_CARD);
        req.setDeadline("2022-08-01");
        req.setTitle("제목");
        req.setMainColor("#ffffff");
        req.setPurpose("목적");
        proposal = proposalRepository.save(new Proposal(client, req));
    }

    @AfterEach
    void teardown() {
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
}
