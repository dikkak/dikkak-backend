package com.dikkak.repository.coworking.file;

import com.dikkak.dto.coworking.GetFileRes;
import com.dikkak.dto.coworking.QGetFileRes;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.dikkak.entity.coworking.QCoworkingFile.coworkingFile;
import static com.dikkak.entity.coworking.QCoworkingStep.coworkingStep;

@RequiredArgsConstructor
public class CoworkingFileRepositoryImpl implements CoworkingFileRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 파일 목록 조회
    @Override
    public List<GetFileRes> getFileList(Long coworkingId, Pageable pageable) {

        JPAQuery<GetFileRes> query = queryFactory
                .select(new QGetFileRes(coworkingFile.id, coworkingFile.fileName, coworkingFile.fileUrl, coworkingFile.isImageFile))
                .from(coworkingFile)
                .where(coworkingFile.coworking.id.eq(coworkingId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        Sort.Order order = pageable.getSort().getOrderFor("createdAt");
        if (order == null || order.isDescending()) {
            return query.orderBy(coworkingFile.createdAt.desc()).fetch();
        } else {
            return query.orderBy(coworkingFile.createdAt.asc()).fetch();
        }

    }
}
