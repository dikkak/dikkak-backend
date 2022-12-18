package com.dikkak.repository.coworking.message;

import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.dto.coworking.QGetChattingRes;
import com.dikkak.entity.coworking.Coworking;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.dikkak.entity.coworking.QCoworkingFile.coworkingFile;
import static com.dikkak.entity.coworking.QCoworkingMessage.coworkingMessage;
import static com.dikkak.entity.user.QUser.user;

@RequiredArgsConstructor
public class CoworkingMessageRepositoryImpl implements CoworkingMessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    // 외주 작업실 채팅 조회
    @Override
    public List<GetChattingRes> getCoworkingMessage(Coworking coworking) {
        return queryFactory
                .select(new QGetChattingRes(
                        user.email, coworkingMessage.content,
                        coworkingFile.fileName, coworkingFile.fileUrl, coworkingFile.isImageFile,
                        coworkingMessage.createdAt
                ))
                .from(coworkingMessage)
                .join(user).on(
                        coworkingMessage.user.id.eq(user.id)
                )
                .leftJoin(coworkingFile).on(
                        coworkingMessage.coworkingFile.id.eq(coworkingFile.id)
                )
                .orderBy(coworkingMessage.createdAt.asc())
                .where(coworkingMessage.coworking.eq(coworking))
                .fetch();
    }
}
