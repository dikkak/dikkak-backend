package com.dikkak.repository.coworking.schedule;

import com.dikkak.entity.coworking.CoworkingSchedule;
import com.dikkak.entity.coworking.StepType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.dikkak.entity.coworking.QCoworkingSchedule.coworkingSchedule;
import static com.dikkak.entity.coworking.QCoworkingStep.coworkingStep;

@RequiredArgsConstructor
public class CoworkingScheduleRepositoryImpl implements CoworkingScheduleRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public CoworkingSchedule getCoworkingScheduleByStep(Long coworkingId, StepType step) {
        return queryFactory.select(coworkingSchedule)
                .from(coworkingSchedule)
                .join(coworkingStep).on(coworkingStep.coworking.id.eq(coworkingId), coworkingStep.type.eq(step))
                .fetchOne();
    }
}
