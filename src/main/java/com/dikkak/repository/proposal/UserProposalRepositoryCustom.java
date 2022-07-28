package com.dikkak.repository.proposal;

import com.dikkak.dto.workplace.ProposalRes;

import java.util.List;

public interface UserProposalRepositoryCustom {

    List<ProposalRes> getByUserId(Long userId);
    String findClientByProposalId(Long proposalId);
}
