package com.dikkak.service;

import com.dikkak.dto.common.BaseException;
import com.dikkak.dto.workplace.WorkplaceRes;
import com.dikkak.repository.proposal.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.dikkak.dto.common.ResponseMessage.DATABASE_ERROR;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;

    public WorkplaceRes getUserWorkplace(Long userId) throws BaseException {
        try {
            return proposalRepository.getByUserId(userId);

        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
