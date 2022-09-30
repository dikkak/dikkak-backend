package com.dikkak.repository.coworking;

import com.dikkak.dto.coworking.GetChattingRes;

import java.util.List;

public interface CoworkingMessageRepositoryCustom {

    List<GetChattingRes> getCoworkingStepMessage(Long coworkingId, int step);
}
