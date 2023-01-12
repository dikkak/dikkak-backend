package com.dikkak.repository.coworking.file;

import com.dikkak.dto.coworking.GetFileRes;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public interface CoworkingFileRepositoryCustom {

    PageImpl<GetFileRes> getFileList(Long coworkingId, Pageable pageable);
}
