package com.dikkak.repository.proposal;

import com.dikkak.entity.proposal.Reference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferenceRepository extends JpaRepository<Reference, Long> {

    List<Reference> findByProposalId(Long proposalId);
}
