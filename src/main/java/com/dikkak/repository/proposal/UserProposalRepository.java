package com.dikkak.repository.proposal;

import com.dikkak.entity.proposal.UserProposal;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserProposalRepository
        extends JpaRepository<UserProposal, Long>, UserProposalRepositoryCustom {

    List<UserProposal> findByUserId(Long id, Sort sort);
}
