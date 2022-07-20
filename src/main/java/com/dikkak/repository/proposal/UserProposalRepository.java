package com.dikkak.repository.proposal;

import com.dikkak.entity.proposal.UserProposal;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserProposalRepository
        extends JpaRepository<UserProposal, Long>, UserProposalRepositoryCustom {
}
