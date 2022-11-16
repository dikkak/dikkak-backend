package com.dikkak.repository.coworking.schedule;

import com.dikkak.entity.coworking.CoworkingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoworkingScheduleRepository
        extends JpaRepository<CoworkingSchedule, Long>, CoworkingScheduleRepositoryCustom {
}
