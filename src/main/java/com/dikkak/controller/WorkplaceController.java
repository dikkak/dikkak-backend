package com.dikkak.controller;

import com.dikkak.config.UserPrincipal;
import com.dikkak.dto.common.BaseException;
import com.dikkak.dto.common.BaseResponse;
import com.dikkak.dto.workplace.ClientWorkplaceRes;
import com.dikkak.dto.workplace.DesignerWorkplaceRes;
import com.dikkak.service.ProposalService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dikkak.dto.common.ResponseMessage.INVALID_ACCESS_TOKEN;

@RequestMapping("/workplace")
@RestController
@RequiredArgsConstructor
public class WorkplaceController {

    private final ProposalService proposalService;

    /**
     * 클라이언트 작업실 조회 API
     * @return proposalId, proposalTitle, coworkingId, coworkingDesigner, coworkingStep
     */
    @GetMapping("/client/list")
    public ResponseEntity<?> getClientWorkplace(@AuthenticationPrincipal UserPrincipal principal) {

        if(principal == null)
            return ResponseEntity.badRequest().body(new BaseResponse(INVALID_ACCESS_TOKEN));

        try {
            List<ClientWorkplaceRes> workplace = proposalService.getClientWorkplace(principal.getUserId());
            JSONObject res = new JSONObject();
            res.put("clientWorkplace", workplace);
            return ResponseEntity.ok().body(res);
        } catch (BaseException e) {
            return ResponseEntity.badRequest().body(new BaseResponse(e));
        }
    }

    /**
     * 디자이너 작업실 조회 API
     * @return proposalId, proposalTitle, clientName, coworkingId, coworkingStep
     */
    @GetMapping("/designer/list")
    public ResponseEntity<?> getDesignerWorkplace(@AuthenticationPrincipal UserPrincipal principal) {

        if(principal == null)
            return ResponseEntity.badRequest().body(new BaseResponse(INVALID_ACCESS_TOKEN));
        try {
            DesignerWorkplaceRes workplace = proposalService.getDesignerWorkplace(principal.getUserId());
            return ResponseEntity.ok().body(workplace);
        } catch (BaseException e) {
            return ResponseEntity.badRequest().body(new BaseResponse(e));
        }

    }
}
