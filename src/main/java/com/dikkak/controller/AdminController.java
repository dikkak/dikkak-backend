package com.dikkak.controller;

import com.dikkak.common.BaseException;
import com.dikkak.config.UserPrincipal;
import com.dikkak.dto.admin.GetProposalListRes;
import com.dikkak.dto.admin.GetUserProposalsRes;
import com.dikkak.dto.admin.MatchingReq;
import com.dikkak.entity.user.User;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.service.ProposalService;
import com.dikkak.service.UserService;
import com.dikkak.service.coworking.CoworkingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.dikkak.common.ResponseMessage.ADMIN_REQUIRED;
import static com.dikkak.common.ResponseMessage.DUPLICATED_DESIGNER;
import static com.dikkak.common.ResponseMessage.INVALID_FORMAT_EMAIL;
import static com.dikkak.common.ResponseMessage.NON_EXISTENT_EMAIL;

@RequestMapping("/admin")
@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ProposalService proposalService;
    private final CoworkingService coworkingService;
    private static final Pattern EMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",Pattern.CASE_INSENSITIVE);

    /**
     * 클라이언트 제안서 목록 조회 api
     * @param principal 회원 id, 타입
     * @param req 클라이언트 email
     * @return 제안서 목록
     */
    @PostMapping("/user/proposals")
    public List<GetUserProposalsRes> getProposals(@LoginUser UserPrincipal principal,
                                                  @RequestBody Map<String, String> req) {
            // admin 계정이 아닌 경우
            if (!isAdminUser(principal)) {
                throw new BaseException(ADMIN_REQUIRED);
            }

            // client email 유효성 검사
            String email = req.get("email");
            if(email == null || !isRegexEmail(email)) {
                throw new BaseException(INVALID_FORMAT_EMAIL);
            }

            User client = userService.getUserByEmail(email);

            // 존재하지 않는 회원이거나 클라이언트 회원이 아닌 경우
            if (client == null || !client.getUserType().equals(UserTypeEnum.CLIENT)) {
                throw new BaseException(NON_EXISTENT_EMAIL);
            }

            // 회원의 전체 제안서 목록
            return proposalService.getUserProposalList(client.getId());
    }

    /**
     * 클라이언트와 디자이너 매칭 API
     * @param principal 회원 id, 타입
     * @param req 제안서 id, 디자이너 email
     */
    @PostMapping("/proposal/designer")
    public void matching(@LoginUser UserPrincipal principal, @RequestBody MatchingReq req) {
        // admin 계정이 아닌 경우
        if (!isAdminUser(principal)) {
            throw new BaseException(ADMIN_REQUIRED);
        }

        // email 유효성 검사
        String email = req.getDesignerEmail();
        if (email == null || !isRegexEmail(email)) {
            throw new BaseException(INVALID_FORMAT_EMAIL);
        }

        // 존재하지 않는 회원이거나 디자이너 회원이 아닌 경우
        User designer = userService.getUserByEmail(email);
        if (designer == null || !designer.getUserType().equals(UserTypeEnum.DESIGNER)) {
            throw new BaseException(NON_EXISTENT_EMAIL);
        }

        // 이미 매칭된 디자이너인 경우
        if (proposalService.existUserProposal(designer, req.getProposalId())) {
            throw new BaseException(DUPLICATED_DESIGNER);
        }

        coworkingService.create(designer, req.getProposalId());
    }

    /**
     * 제안서 목록 조회
     * @param principal 회원 id, 타입
     * @param page 페이지 번호
     * @param size 페이지당 제안서 개수
     */
    @GetMapping("/proposal/list")
    public GetProposalListRes getProposalList(@LoginUser UserPrincipal principal,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "15") int size) {
        // admin 계정이 아닌 경우
        if (!isAdminUser(principal)) {
            throw new BaseException(ADMIN_REQUIRED);
        }

        return proposalService.getProposalList(page, size);
    }

    private boolean isRegexEmail(String email) {
        return EMAIL.matcher(email).find();
    }

    private boolean isAdminUser(UserPrincipal principal) {
        return principal.getType().equals(UserTypeEnum.ADMIN);
    }
}
