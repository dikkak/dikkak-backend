package com.dikkak.repository.coworking.task;

import com.dikkak.dto.coworking.TaskRes;

import java.util.List;

public interface CoworkingTaskRepositoryCustom {

    List<TaskRes> getCoworkingTask(Long coworkingId);
}
