package com.dikkak.service;

import com.dikkak.entity.proposal.Reference;
import com.dikkak.repository.proposal.ReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReferenceService {

    private final ReferenceRepository referenceRepository;

    @Transactional
    public void create(Reference reference) {
        referenceRepository.save(reference);
    }

    public List<Reference> getRefList(Long proposalId) {
        return referenceRepository.findByProposalId(proposalId);
    }
}
