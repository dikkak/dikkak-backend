package com.dikkak.repository.coworking.file;

import com.dikkak.dto.coworking.GetFileRes;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CoworkingFileRepositoryCustom {

    List<GetFileRes> getFileList(Long coworkingId, Pageable pageable);
}
