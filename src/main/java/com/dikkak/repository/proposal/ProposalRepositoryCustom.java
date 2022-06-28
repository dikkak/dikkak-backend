package com.dikkak.repository.proposal;

import com.dikkak.dto.proposal.GetProposalRes;

import java.util.List;

public interface ProposalRepositoryCustom {

    List<GetProposalRes> getByUserId(Long userId);
}
