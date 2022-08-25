package com.dikkak.controller;

import com.dikkak.dto.admin.GetProposalsRes;
import com.dikkak.dto.admin.MatchingReq;
import com.dikkak.dto.common.BaseException;
import com.dikkak.dto.common.BaseResponse;
import com.dikkak.entity.user.User;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.service.CoworkingService;
import com.dikkak.service.ProposalService;
import com.dikkak.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.dikkak.dto.common.ResponseMessage.ADMIN_REQUIRED;
import static com.dikkak.dto.common.ResponseMessage.INVALID_FORMAT_EMAIL;

@RequestMapping("/admin")
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ProposalService proposalService;
    private final CoworkingService coworkingService;
    private static final Pattern EMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",Pattern.CASE_INSENSITIVE);

    /**
     * 클라이언트 제안서 목록 조회 api
     * @param userId admin 계정 id
     * @param req 클라이언트 email
     * @return 제안서 목록
     */
    @PostMapping("/user/proposals")
    public ResponseEntity<?> getProposals(@AuthenticationPrincipal Long userId,
                                          @RequestBody Map<String, String> req) {

        try {
            User user = userService.getUser(userId);

            String email = req.get("email");

            // admin 계정이 아닌 경우
            if(!user.getUserType().equals(UserTypeEnum.ADMIN)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(ADMIN_REQUIRED));
            }

            // client email 유효성 검사
            if(email == null || !isRegexEmail(email)) {
                throw new BaseException(INVALID_FORMAT_EMAIL);
            }

            User client = userService.getUserByEmail(email);

            // 존재하지 않는 회원이거나 클라이언트 회원이 아닌 경우
            if(client == null || !client.getUserType().equals(UserTypeEnum.CLIENT))
                return  ResponseEntity.badRequest().body(new BaseResponse("존재하지 않는 클라이언트 이메일입니다."));

            // 회원의 전체 제안서 목록
            List<GetProposalsRes> proposalList = proposalService.getProposalList(client.getId());

            return ResponseEntity.ok().body(proposalList);

        } catch (BaseException e) {
            return ResponseEntity.badRequest().body(new BaseResponse(e));
        }

    }

    @PostMapping("/proposal/designer")
    public ResponseEntity<?> matching(@AuthenticationPrincipal Long userId,
                                      @RequestBody MatchingReq req) {
        try {

            // admin 계정이 아닌 경우
            User user = userService.getUser(userId);
            if (!user.getUserType().equals(UserTypeEnum.ADMIN)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(ADMIN_REQUIRED));
            }

            // email 유효성 검사
            String email = req.getDesignerEmail();
            if (email == null || !isRegexEmail(email)) {
                throw new BaseException(INVALID_FORMAT_EMAIL);
            }

            // 존재하지 않는 회원이거나 디자이너 회원이 아닌 경우
            User designer = userService.getUserByEmail(email);
            if(designer == null || !designer.getUserType().equals(UserTypeEnum.DESIGNER))
                return  ResponseEntity.badRequest().body(new BaseResponse("존재하지 않는 디자이너 이메일입니다."));

            // 이미 매칭된 디자이너인 경우
            if(proposalService.existUserProposal(designer, req.getProposalId()))
                return ResponseEntity.badRequest().body(new BaseResponse("이미 매칭된 디자이너입니다."));

            coworkingService.create(designer, req.getProposalId());
            return ResponseEntity.ok().build();

        } catch (BaseException e) {
            return ResponseEntity.badRequest().body(new BaseResponse(e));
        }

    }

    private boolean isRegexEmail(String email) {
        return EMAIL.matcher(email).find();
    }

}
