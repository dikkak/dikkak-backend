package com.dikkak.repository.proposal;

import com.dikkak.entity.proposal.ProposalKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalKeywordRepository extends JpaRepository<ProposalKeyword, Long> {

    List<ProposalKeyword> findByProposalId(Long proposalId);
}
