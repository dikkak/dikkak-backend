package com.dikkak.repository.proposal;

import com.dikkak.dto.proposal.GetProposalRes;
import com.dikkak.dto.proposal.QGetProposalRes;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.dikkak.entity.proposal.QUserProposal.userProposal;

public class ProposalRepositoryImpl implements ProposalRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProposalRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<GetProposalRes> getByUserId(Long userId) {
        return queryFactory.select(new QGetProposalRes(userProposal.proposal.id, userProposal.proposal.title))
                .from(userProposal)
                .where(
                        userProposal.user.id.eq(userId)
                )
                .fetch();
    }
}
