package com.dikkak.service;

import com.dikkak.entity.proposal.Otherfile;
import com.dikkak.repository.proposal.OtherFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OtherFileService {

    private final OtherFileRepository otherFileRepository;

    @Transactional
    public void create(Otherfile otherfile) {
        otherFileRepository.save(otherfile);
    }

    public List<Otherfile> getOtherFileList(Long proposalId) {
        return otherFileRepository.findByProposalId(proposalId);
    }
}
