package com.dikkak.repository.proposal;

import com.dikkak.entity.proposal.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProposalRepository extends JpaRepository<Proposal, Long> {
}
