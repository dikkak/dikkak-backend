package com.dikkak.repository.proposal;

import com.dikkak.dto.workplace.ClientWorkplaceRes;
import com.dikkak.dto.workplace.DesignerWorkplaceRes.WorkInfo;

import java.util.List;

public interface ProposalRepositoryCustom {
    List<ClientWorkplaceRes> getClientWorkplace(Long clientId);
    List<WorkInfo> getDesignerWorkplace(Long designerId);
    long updateProposalsInactive(Long clientId, List<Long> proposalList);


}
