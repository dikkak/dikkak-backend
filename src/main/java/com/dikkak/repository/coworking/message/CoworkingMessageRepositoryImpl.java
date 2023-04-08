package com.dikkak.repository.coworking.message;

import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.dto.coworking.QGetChattingRes;
import com.dikkak.entity.coworking.Coworking;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.dikkak.entity.coworking.QCoworkingFile.coworkingFile;
import static com.dikkak.entity.coworking.QCoworkingMessage.coworkingMessage;
import static com.dikkak.entity.user.QUser.user;

@RequiredArgsConstructor
public class CoworkingMessageRepositoryImpl implements CoworkingMessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    // 외주 작업실 채팅 조회
    @Override
    public Page<GetChattingRes> getCoworkingMessage(Coworking coworking, Pageable pageable) {
        List<GetChattingRes> content = queryFactory
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
                .where(coworkingMessage.coworking.eq(coworking))
                .orderBy(coworkingMessage.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(coworkingMessage.count())
                .from(coworkingMessage)
                .where(coworkingMessage.coworking.eq(coworking))
                .fetchFirst();

        return new PageImpl<>(content, pageable, total);
    }
}
