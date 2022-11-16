package com.dikkak.repository.coworking.schedule;

import com.dikkak.entity.coworking.CoworkingSchedule;
import com.dikkak.entity.coworking.StepType;

public interface CoworkingScheduleRepositoryCustom {
    CoworkingSchedule getCoworkingScheduleByStep(Long coworkingId, StepType step);
}
