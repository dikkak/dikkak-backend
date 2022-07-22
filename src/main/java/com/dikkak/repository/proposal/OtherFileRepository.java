package com.dikkak.repository.proposal;

import com.dikkak.entity.proposal.Otherfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OtherFileRepository extends JpaRepository<Otherfile, Long> {

    List<Otherfile> findByProposalId(Long proposalId);
}
