package com.dikkak.repository.coworking.task;

import com.dikkak.dto.coworking.QTaskRes;
import com.dikkak.dto.coworking.TaskRes;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.dikkak.entity.coworking.QCoworkingTask.coworkingTask;

@RequiredArgsConstructor
public class CoworkingTaskRepositoryImpl implements CoworkingTaskRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TaskRes> getCoworkingTask(Long coworkingId) {
        return queryFactory.select(new QTaskRes(coworkingTask.id, coworkingTask.content, coworkingTask.complete))
                .from(coworkingTask)
                .where(coworkingTask.coworking.id.eq(coworkingId))
                .fetch();
    }
}
