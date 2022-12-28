package com.dikkak.service.coworking;

import com.dikkak.dto.coworking.GetFileRes;
import com.dikkak.repository.coworking.file.CoworkingFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {

    private final CoworkingFileRepository fileRepository;

    // file 목록 조회
    public List<GetFileRes> getFileList(Long coworkingId, Pageable pageable) {
        return fileRepository.getFileList(coworkingId, pageable);
    }
}
