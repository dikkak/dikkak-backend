package com.dikkak.repository.proposal;

import com.dikkak.dto.workplace.ProposalRes;
import com.dikkak.dto.workplace.WorkRes;
import com.dikkak.dto.workplace.WorkplaceRes;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.util.List;

import static com.dikkak.entity.QWork.work;
import static com.dikkak.entity.proposal.QUserProposal.userProposal;

public class ProposalRepositoryImpl implements ProposalRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProposalRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public WorkplaceRes getByUserId(Long userId) {
        List<Tuple> result = queryFactory
                .select(
                        userProposal.proposal.id,
                        userProposal.proposal.title,
                        work.id,
                        work.title,
                        work.complete
                )
                .from(userProposal, work)
                .where(
                        userProposal.user.id.eq(userId),
                        userProposal.proposal.eq(work.proposal)
                )
                .fetch();

        WorkplaceRes workplaceRes = new WorkplaceRes();
        for (Tuple tuple : result) {
            workplaceRes.getProposals().add(
                    new ProposalRes(tuple.get(userProposal.proposal.id),
                            tuple.get(userProposal.proposal.title))
            );

            workplaceRes.getWorks().add(
                    new WorkRes(tuple.get(work.id),
                            tuple.get(work.title),
                            tuple.get(work.complete))
            );
        }

        return workplaceRes;
    }
}
