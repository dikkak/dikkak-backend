package com.dikkak.repository.coworking.task;

import com.dikkak.dto.coworking.QTaskRes;
import com.dikkak.dto.coworking.TaskRes;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.dikkak.entity.coworking.QCoworkingTask.coworkingTask;

@RequiredArgsConstructor
public class CoworkingTaskRepositoryImpl implements CoworkingTaskRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TaskRes> getCoworkingTask(Long coworkingId, Boolean complete, Pageable pageable) {
        JPAQuery<TaskRes> query = queryFactory.select(new QTaskRes(coworkingTask.id, coworkingTask.content, coworkingTask.complete))
                .from(coworkingTask)
                .where(
                        coworkingEq(coworkingId),
                        completeEq(complete)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        Sort.Order order = pageable.getSort().getOrderFor(coworkingTask.createdAt.getMetadata().getName());
        if (order == null || order.isDescending()) {
            query.orderBy(coworkingTask.createdAt.desc());
        } else {
            query.orderBy(coworkingTask.createdAt.asc());
        }
        List<TaskRes> content = query.fetch();
        long total = queryFactory
                .select(coworkingTask.count())
                .from(coworkingTask)
                .where(coworkingEq(coworkingId))
                .fetchFirst();

        return new PageImpl<>(content, pageable, total);
    }

    private static BooleanExpression coworkingEq(Long coworkingId) {
        return coworkingTask.coworking.id.eq(coworkingId);
    }

    private static Predicate completeEq(Boolean complete) {
        return complete != null ? coworkingTask.complete.eq(complete) : null;
    }
}
