package com.dikkak.repository.coworking.message;

import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.entity.coworking.Coworking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CoworkingMessageRepositoryCustom {

    // 외주 작업실 채팅 조회
    Page<GetChattingRes> getCoworkingMessage(Coworking coworking, Pageable pageable);

}
