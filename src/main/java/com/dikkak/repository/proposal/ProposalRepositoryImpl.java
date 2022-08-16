package com.dikkak.repository.proposal;

import com.dikkak.dto.workplace.ClientWorkplaceRes;
import com.dikkak.dto.workplace.QClientWorkplaceRes;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.dikkak.entity.QUser.user;
import static com.dikkak.entity.proposal.QProposal.proposal;
import static com.dikkak.entity.work.QCoworking.coworking;

public class ProposalRepositoryImpl implements ProposalRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProposalRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 클라이언트 작업실 조회 - 제안서, 외주작업실
    @Override
    public List<ClientWorkplaceRes> getClientWorkplace(Long clientId) {
        return queryFactory.select(new QClientWorkplaceRes(
                    proposal.id, proposal.title, coworking.id, user.name, coworking.step
                ))
                .from(proposal)
                .leftJoin(coworking).on(coworking.proposal.eq(proposal))
                .leftJoin(user).on(user.eq(coworking.designer))
                .where(proposal.client.id.eq(clientId))
                .fetch();
    }
}
