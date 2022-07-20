package com.dikkak.service;

import com.dikkak.dto.common.BaseException;
import com.dikkak.entity.proposal.Reference;
import com.dikkak.repository.proposal.ReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dikkak.dto.common.ResponseMessage.DATABASE_ERROR;

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
}
