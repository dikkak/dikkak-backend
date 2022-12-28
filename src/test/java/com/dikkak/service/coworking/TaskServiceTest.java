package com.dikkak.service.coworking;

import com.dikkak.dto.coworking.GetTaskRes;
import com.dikkak.dto.coworking.message.Message;
import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.CoworkingTask;
import com.dikkak.entity.proposal.CategoryEnum;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.user.ProviderTypeEnum;
import com.dikkak.entity.user.User;
import com.dikkak.repository.UserRepository;
import com.dikkak.repository.coworking.CoworkingRepository;
import com.dikkak.repository.coworking.task.CoworkingTaskRepository;
import com.dikkak.repository.proposal.ProposalRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceTest {
    @Autowired
    TaskService taskService;
    @Autowired
    CoworkingTaskRepository taskRepository;
    @Autowired
    ProposalRepository proposalRepository;
    @Autowired
    CoworkingRepository coworkingRepository;
    @Autowired
    UserRepository userRepository;
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
        Proposal proposal = proposalRepository.save(new Proposal(client, req));
        coworking = coworkingRepository.save(new Coworking(proposal, designer));
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        coworkingRepository.deleteAll();
        proposalRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("task 목록 조회")
    void getTaskList() {
        //given
        String content = "할 일1";
        taskRepository.save(CoworkingTask.of(coworking, content));

        //when
        List<Message<GetTaskRes>> taskList = taskService.getTaskList(coworking.getId());

        //then
        assertThat(taskList.size()).isEqualTo(1);
        assertThat(taskList.get(0).getData().getContent().equals(content)).isTrue();
        assertThat(taskList.get(0).getData().isChecked()).isFalse();
    }

}
