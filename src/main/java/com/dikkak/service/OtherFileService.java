package com.dikkak.service;

import com.dikkak.dto.common.BaseException;
import com.dikkak.entity.proposal.Otherfile;
import com.dikkak.entity.proposal.Reference;
import com.dikkak.repository.proposal.OtherFileRepository;
import com.dikkak.repository.proposal.ReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.dikkak.dto.common.ResponseMessage.DATABASE_ERROR;

@Service
@RequiredArgsConstructor
public class OtherFileService {

    private final OtherFileRepository otherFileRepository;

    @Transactional
    public void create(Otherfile otherfile) throws BaseException {
        try {
            otherFileRepository.save(otherfile);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Otherfile> getOtherFileList(Long proposalId) throws BaseException {
        try {
            return otherFileRepository.findByProposalId(proposalId);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
