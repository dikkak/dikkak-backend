package com.dikkak.repository.coworking.task;

import com.dikkak.entity.coworking.CoworkingTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoworkingTaskRepository
        extends JpaRepository<CoworkingTask, Long>, CoworkingTaskRepositoryCustom {
}
