package com.dikkak.repository.coworking;

import com.dikkak.dto.coworking.GetTaskRes;
import com.dikkak.dto.coworking.QGetTaskRes;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.dikkak.entity.coworking.QCoworkingStep.coworkingStep;
import static com.dikkak.entity.coworking.QCoworkingTask.coworkingTask;

public class CoworkingTaskRepositoryImpl implements CoworkingTaskRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CoworkingTaskRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<GetTaskRes> getCoworkingTask(Long coworkingId) {
        return queryFactory.select(new QGetTaskRes(coworkingTask.id, coworkingTask.content, coworkingTask.complete, coworkingStep.type))
                .from(coworkingTask)
                .join(coworkingStep).on(coworkingTask.coworkingStep.id.eq(coworkingStep.id))
                .where(coworkingStep.coworking.id.eq(coworkingId))
                .fetch();
    }
}
