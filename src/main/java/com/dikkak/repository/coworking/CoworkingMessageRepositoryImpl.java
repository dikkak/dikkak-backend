package com.dikkak.repository.coworking;

import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.dto.coworking.QGetChattingRes;
import com.dikkak.entity.coworking.StepType;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.dikkak.entity.coworking.QCoworkingFile.coworkingFile;
import static com.dikkak.entity.coworking.QCoworkingMessage.coworkingMessage;
import static com.dikkak.entity.coworking.QCoworkingStep.coworkingStep;
import static com.dikkak.entity.user.QUser.user;

public class CoworkingMessageRepositoryImpl implements CoworkingMessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CoworkingMessageRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 외주 작업실 step의 채팅 조회
    @Override
    public List<GetChattingRes> getCoworkingStepMessage(Long coworkingId, StepType step) {
        return queryFactory
                .select(new QGetChattingRes(
                        user.email, coworkingMessage.content,
                        coworkingFile.fileName, coworkingFile.fileUrl, coworkingMessage.createdAt
                ))
                .from(coworkingMessage)
                .join(user).on(
                        coworkingMessage.user.id.eq(user.id)
                )
                .join(coworkingStep).on(
                        coworkingStep.coworking.id.eq(coworkingId),
                        coworkingStep.type.eq(step)
                )
                .leftJoin(coworkingFile).on(
                        coworkingMessage.coworkingFile.id.eq(coworkingFile.id)
                )
                .fetch();
    }
}
