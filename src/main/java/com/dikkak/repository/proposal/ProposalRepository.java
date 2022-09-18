package com.dikkak.repository.proposal;

import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.StatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProposalRepository
        extends JpaRepository<Proposal, Long>, ProposalRepositoryCustom {
    List<Proposal> findByClientIdAndStatus(Long clientId, StatusType status, Sort sort);
    Page<Proposal> findAll(Pageable pageable);
}
