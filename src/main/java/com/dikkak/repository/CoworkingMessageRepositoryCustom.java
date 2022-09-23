package com.dikkak.repository;

import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.entity.coworking.CoworkingMessage;

import java.util.List;

public interface CoworkingMessageRepositoryCustom {

    List<GetChattingRes> getCoworkingStepMessage(Long coworkingId, int step);
}
