package com.dikkak.controller;

import com.dikkak.config.UserPrincipal;
import com.dikkak.dto.workspace.ClientWorkspaceRes;
import com.dikkak.dto.workspace.DesignerWorkspaceRes;
import com.dikkak.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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
    public Map<String, List<ClientWorkspaceRes>> getClientWorkspace(@LoginUser UserPrincipal principal) {
        List<ClientWorkspaceRes> workplace = proposalService.getClientWorkspace(principal.getUserId());
        return Map.of("clientWorkplace", workplace);
    }

    /**
     * 디자이너 작업실 조회 API
     * @return proposalId, proposalTitle, clientName, coworkingId
     */
    @GetMapping("/designer/list")
    public DesignerWorkspaceRes getDesignerWorkspace(@LoginUser UserPrincipal principal) {
        return proposalService.getDesignerWorkspace(principal.getUserId());
    }
}
