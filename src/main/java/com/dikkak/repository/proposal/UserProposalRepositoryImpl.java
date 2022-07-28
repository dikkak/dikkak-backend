package com.dikkak.repository.proposal;

import com.dikkak.dto.workplace.ProposalRes;
import com.dikkak.dto.workplace.QProposalRes;
import com.dikkak.entity.UserTypeEnum;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.util.List;

import static com.dikkak.entity.proposal.QUserProposal.userProposal;

public class UserProposalRepositoryImpl implements UserProposalRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserProposalRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ProposalRes> getByUserId(Long userId) {
        return queryFactory
                .select(new QProposalRes(userProposal.proposal.id,
                                        userProposal.proposal.title))
                .from(userProposal)
                .where(
                        userProposal.user.id.eq(userId)
                )
                .fetch();
    }

    @Override
    public String findClientByProposalId(Long proposalId) {
        return queryFactory
                .select(userProposal.user.name)
                .from(userProposal)
                .where(
                        userProposal.proposal.id.eq(proposalId),
                        userProposal.user.userType.eq(UserTypeEnum.CLIENT)
                )
                .fetch().get(0);
    }
}
