package com.dikkak.repository.proposal;

import com.dikkak.dto.workspace.ClientWorkspaceRes;
import com.dikkak.dto.workspace.DesignerWorkspaceRes;

import java.util.List;

public interface ProposalRepositoryCustom {
    List<ClientWorkspaceRes> getClientWorkspace(Long clientId);
    List<DesignerWorkspaceRes.WorkInfo> getDesignerWorkspace(Long designerId);
    long updateProposalsInactive(Long clientId, List<Long> proposalList);


}
