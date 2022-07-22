package com.dikkak.controller;

import com.dikkak.dto.admin.GetProposalsRes;
import com.dikkak.dto.common.BaseException;
import com.dikkak.dto.common.BaseResponse;
import com.dikkak.entity.User;
import com.dikkak.entity.UserTypeEnum;
import com.dikkak.service.ProposalService;
import com.dikkak.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            System.out.println("email = " + email);

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

    private boolean isRegexEmail(String email) {
        return EMAIL.matcher(email).find();
    }

}
