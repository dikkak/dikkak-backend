package com.dikkak.repository.coworking;

import com.dikkak.dto.coworking.GetTaskRes;

import java.util.List;

public interface CoworkingTaskRepositoryCustom {

    List<GetTaskRes> getCoworkingTask(Long coworkingId);
}
