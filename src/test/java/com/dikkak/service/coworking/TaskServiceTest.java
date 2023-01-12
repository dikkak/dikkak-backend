package com.dikkak.service.coworking;

import com.dikkak.config.UserPrincipal;
import com.dikkak.dto.PageCustom;
import com.dikkak.dto.coworking.AddTaskReq;
import com.dikkak.dto.coworking.TaskRes;
import com.dikkak.dto.coworking.UpdateTaskReq;
import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.CoworkingFile;
import com.dikkak.entity.coworking.CoworkingTask;
import com.dikkak.entity.proposal.CategoryEnum;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.user.ProviderTypeEnum;
import com.dikkak.entity.user.User;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.repository.UserRepository;
import com.dikkak.repository.coworking.CoworkingRepository;
import com.dikkak.repository.coworking.file.CoworkingFileRepository;
import com.dikkak.repository.coworking.task.CoworkingTaskRepository;
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
    CoworkingFileRepository fileRepository;
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
        Proposal proposal = proposalRepository.save(new Proposal(client, req));
        coworking = coworkingRepository.save(new Coworking(proposal, designer));
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        fileRepository.deleteAll();
        coworkingRepository.deleteAll();
        proposalRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("task 목록 조회")
    void getTaskList() {
        //given
        String content = "할 일1";
        taskRepository.save(CoworkingTask.builder().coworking(coworking).content(content).build());
        PageRequest pageRequest = PageRequest.of(0, 10);

        //when
        PageCustom<TaskRes> taskList = taskService.getTaskList(coworking.getId(), null, pageRequest);

        //then
        assertThat(taskList.getContent()).hasSize(1);
        assertThat(taskList.getContent().get(0).getContent()).isEqualTo(content);
        assertThat(taskList.getContent().get(0).isChecked()).isFalse();
    }

    @Test
    @DisplayName("완료 여부에 따라 task를 조회할 수 있다")
    void getCompleteTaskList() {
        //given
        String content = "할 일1";
        taskRepository.save(CoworkingTask.builder().coworking(coworking).content(content).build());

        //when
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageCustom<TaskRes> completeList = taskService.getTaskList(coworking.getId(), true, pageRequest);
        PageCustom<TaskRes> incompleteList = taskService.getTaskList(coworking.getId(), false, pageRequest);

        //then
        assertThat(completeList.getContent()).isEmpty();
        assertThat(incompleteList.getContent()).hasSize(1);
        assertThat(incompleteList.getContent().get(0).getContent()).isEqualTo(content);
        assertThat(incompleteList.getContent().get(0).isChecked()).isFalse();
        assertThat(incompleteList.isHasPrev()).isFalse();
        assertThat(incompleteList.isHasNext()).isFalse();
    }

    @Test
    @DisplayName("task를 추가할 수 있다")
    void addTask() {
        // given
        AddTaskReq request = new AddTaskReq();
        request.setContent("내용");

        // when
        TaskRes task = taskService.createTask(request, coworking, null);

        // then
        assertThat(task.getTaskId()).isNotNull();
        assertThat(task.getContent()).isEqualTo(request.getContent());
        assertThat(task.isChecked()).isFalse();
    }

    @Test
    @DisplayName("file 관련 task를 추가할 수 있다")
    void addFileTask() {
        // given
        AddTaskReq request = new AddTaskReq();
        request.setContent("내용");
        CoworkingFile file = fileRepository.save(CoworkingFile.builder().coworking(coworking).fileName("file").isImageFile(true).fileUrl("url").build());

        // when
        TaskRes task = taskService.createTask(request, coworking, file);

        // then
        assertThat(task.getTaskId()).isNotNull();
        assertThat(task.getContent()).isEqualTo(request.getContent());
        assertThat(task.isChecked()).isFalse();
    }

    @Test
    @DisplayName("task를 해결 상태로 변경할 수 있다.")
    void changeTaskToComplete() {
        // given
        Long taskId = taskRepository.save(CoworkingTask.builder().coworking(coworking).content("할 일").build()).getId();
        UpdateTaskReq request = new UpdateTaskReq();
        request.setTaskId(taskId);
        request.setChecked(true);

        // when
        taskService.updateTask(request, new UserPrincipal(coworking.getDesigner()));

        // then
        assertThat(taskRepository.findById(taskId).orElseThrow().isComplete()).isTrue();
    }
}
