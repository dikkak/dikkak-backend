package com.dikkak.service.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.dto.PageCustom;
import com.dikkak.dto.coworking.GetFileRes;
import com.dikkak.entity.coworking.CoworkingFile;
import com.dikkak.repository.coworking.file.CoworkingFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dikkak.common.ResponseMessage.WRONG_FILE_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {

    private final CoworkingFileRepository fileRepository;

    // file 목록 조회
    public PageCustom<GetFileRes> getFileList(Long coworkingId, Pageable pageable) {
        Page<GetFileRes> page = fileRepository.getFileList(coworkingId, pageable);
        return PageCustom.<GetFileRes>builder()
                .content(page.getContent())
                .hasNext(page.hasNext())
                .hasPrev(page.hasPrevious())
                .next(pageable.getPageNumber()+1)
                .prev(pageable.getPageNumber()-1)
                .build();
    }

    public CoworkingFile getFile(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new BaseException(WRONG_FILE_ID));
    }
}
