package com.dikkak.repository.coworking.message;

import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.entity.coworking.StepType;

import java.util.List;

public interface CoworkingMessageRepositoryCustom {

    List<GetChattingRes> getCoworkingStepMessage(Long coworkingId, StepType step);
}
