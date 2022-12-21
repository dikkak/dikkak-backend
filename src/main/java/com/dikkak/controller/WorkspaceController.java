package com.dikkak.controller;

import com.dikkak.config.UserPrincipal;
import com.dikkak.common.BaseException;
import com.dikkak.dto.workspace.ClientWorkspaceRes;
import com.dikkak.dto.workspace.DesignerWorkspaceRes;
import com.dikkak.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.dikkak.common.ResponseMessage.INVALID_ACCESS_TOKEN;

@RequestMapping("/workspace")
@RestController
@RequiredArgsConstructor
public class WorkspaceController {

    private final ProposalService proposalService;

    /**
     * 클라이언트 작업실 조회 API
     * @return proposalId, proposalTitle, coworkingId, coworkingDesigner
     */
    @GetMapping("/client/list")
    public Map<String, List<ClientWorkspaceRes>> getClientWorkspace(@AuthenticationPrincipal UserPrincipal principal) {
        if(principal == null) {
            throw new BaseException(INVALID_ACCESS_TOKEN);
        }

        List<ClientWorkspaceRes> workplace = proposalService.getClientWorkspace(principal.getUserId());
        return Map.of("clientWorkplace", workplace);
    }

    /**
     * 디자이너 작업실 조회 API
     * @return proposalId, proposalTitle, clientName, coworkingId
     */
    @GetMapping("/designer/list")
    public DesignerWorkspaceRes getDesignerWorkspace(@AuthenticationPrincipal UserPrincipal principal) {
        if(principal == null) {
            throw new BaseException(INVALID_ACCESS_TOKEN);
        }

        return proposalService.getDesignerWorkspace(principal.getUserId());
    }
}
