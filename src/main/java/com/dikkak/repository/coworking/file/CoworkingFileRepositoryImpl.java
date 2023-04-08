package com.dikkak.repository.coworking.file;

import com.dikkak.dto.coworking.GetFileRes;
import com.dikkak.dto.coworking.QGetFileRes;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.dikkak.entity.coworking.QCoworkingFile.coworkingFile;

@RequiredArgsConstructor
public class CoworkingFileRepositoryImpl implements CoworkingFileRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 파일 목록 조회
    @Override
    public PageImpl<GetFileRes> getFileList(Long coworkingId, Pageable pageable) {
        JPAQuery<GetFileRes> query = queryFactory
                .select(new QGetFileRes(coworkingFile.id, coworkingFile.fileName, coworkingFile.fileUrl, coworkingFile.isImageFile))
                .from(coworkingFile)
                .where(coworkingEq(coworkingId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        Sort.Order order = pageable.getSort().getOrderFor(coworkingFile.createdAt.getMetadata().getName());
        if (order == null || order.isDescending()) {
            query.orderBy(coworkingFile.createdAt.desc());
        } else {
            query.orderBy(coworkingFile.createdAt.asc());
        }

        List<GetFileRes> content = query.fetch();
        long total = queryFactory
                .select(coworkingFile.count())
                .from(coworkingFile)
                .where(coworkingEq(coworkingId))
                .fetchFirst();

        return new PageImpl<>(content, pageable, total);

    }

    private static BooleanExpression coworkingEq(Long coworkingId) {
        return coworkingFile.coworking.id.eq(coworkingId);
    }
}
