package com.dikkak.repository.proposal;

import com.dikkak.entity.proposal.UserProposal;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProposalRepository
        extends JpaRepository<UserProposal, Long>, ProposalRepositoryCustom {

}
