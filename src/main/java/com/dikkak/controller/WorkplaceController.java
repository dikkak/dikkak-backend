package com.dikkak.controller;

import com.dikkak.config.UserPrincipal;
import com.dikkak.common.BaseException;
import com.dikkak.dto.workplace.ClientWorkplaceRes;
import com.dikkak.dto.workplace.DesignerWorkplaceRes;
import com.dikkak.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.dikkak.common.ResponseMessage.INVALID_ACCESS_TOKEN;

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
    public Map<String, List<ClientWorkplaceRes>> getClientWorkplace(@AuthenticationPrincipal UserPrincipal principal)
            throws BaseException {

        if(principal == null)
            throw new BaseException(INVALID_ACCESS_TOKEN);

        List<ClientWorkplaceRes> workplace = proposalService.getClientWorkplace(principal.getUserId());
        return Map.of("clientWorkplace", workplace);
    }

    /**
     * 디자이너 작업실 조회 API
     * @return proposalId, proposalTitle, clientName, coworkingId, coworkingStep
     */
    @GetMapping("/designer/list")
    public DesignerWorkplaceRes getDesignerWorkplace(@AuthenticationPrincipal UserPrincipal principal)
            throws BaseException {

        if(principal == null)
            throw new BaseException(INVALID_ACCESS_TOKEN);

        return proposalService.getDesignerWorkplace(principal.getUserId());
    }
}
