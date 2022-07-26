package com.dikkak.repository.proposal;

import com.dikkak.entity.User;
import com.dikkak.entity.proposal.UserProposal;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserProposalRepository
        extends JpaRepository<UserProposal, Long>, UserProposalRepositoryCustom {

    List<UserProposal> findByUserId(Long id, Sort sort);
    Optional<UserProposal> findByUserAndProposalId(User user, Long proposalId);
}
