package com.dikkak.repository.coworking.task;

import com.dikkak.dto.coworking.TaskRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CoworkingTaskRepositoryCustom {

    Page<TaskRes> getCoworkingTask(Long coworkingId, Boolean complete, Pageable pageable);
}
