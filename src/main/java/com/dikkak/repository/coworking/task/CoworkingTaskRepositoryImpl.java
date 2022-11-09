package com.dikkak.repository.coworking.task;

import com.dikkak.dto.coworking.GetTaskRes;
import com.dikkak.dto.coworking.QGetTaskRes;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.dikkak.entity.coworking.QCoworkingStep.coworkingStep;
import static com.dikkak.entity.coworking.QCoworkingTask.coworkingTask;

@RequiredArgsConstructor
public class CoworkingTaskRepositoryImpl implements CoworkingTaskRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GetTaskRes> getCoworkingTask(Long coworkingId) {
        return queryFactory.select(new QGetTaskRes(coworkingTask.id, coworkingTask.content, coworkingTask.complete, coworkingStep.type))
                .from(coworkingTask)
                .join(coworkingStep).on(coworkingTask.coworkingStep.id.eq(coworkingStep.id))
                .where(coworkingStep.coworking.id.eq(coworkingId))
                .fetch();
    }
}
