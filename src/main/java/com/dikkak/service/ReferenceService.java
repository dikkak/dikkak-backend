package com.dikkak.service;

import com.dikkak.common.BaseException;
import com.dikkak.entity.proposal.Reference;
import com.dikkak.repository.proposal.ReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.dikkak.common.ResponseMessage.DATABASE_ERROR;

@Service
@RequiredArgsConstructor
public class ReferenceService {

    private final ReferenceRepository referenceRepository;

    @Transactional
    public void create(Reference reference) throws BaseException {
        try {
            referenceRepository.save(reference);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Reference> getRefList(Long proposalId) throws BaseException {
        try {
            return referenceRepository.findByProposalId(proposalId);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
