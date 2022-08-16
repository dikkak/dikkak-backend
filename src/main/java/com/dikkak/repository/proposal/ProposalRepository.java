package com.dikkak.repository.proposal;

import com.dikkak.entity.proposal.Proposal;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProposalRepository
        extends JpaRepository<Proposal, Long>, ProposalRepositoryCustom {
    List<Proposal> findByClientId(Long clientId, Sort sort);
}
