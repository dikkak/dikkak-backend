package com.dikkak.repository;

import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.dto.coworking.QGetChattingRes;
import com.dikkak.entity.coworking.CoworkingMessage;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.dikkak.entity.coworking.QCoworkingMessage.coworkingMessage;
import static com.dikkak.entity.coworking.QCoworkingStep.coworkingStep;

public class CoworkingMessageRepositoryImpl implements CoworkingMessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CoworkingMessageRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 외주 작업실 step의 채팅 조회
    @Override
    public List<GetChattingRes> getCoworkingStepMessage(Long coworkingId, int step) {
        return queryFactory
                .select(new QGetChattingRes(
                        coworkingMessage.id, coworkingMessage.user.id, coworkingMessage.content, coworkingMessage.fileUrl, coworkingMessage.createdAt
                ))
                .from(coworkingMessage)
                .join(coworkingStep).on(
                        coworkingStep.coworking.id.eq(coworkingId),
                        coworkingStep.step.eq(step)
                )
                .fetch();
    }
}
