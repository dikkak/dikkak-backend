package com.dikkak.repository.proposal;

import com.dikkak.dto.workspace.ClientWorkspaceRes;
import com.dikkak.dto.workspace.DesignerWorkspaceRes;
import com.dikkak.dto.workspace.QClientWorkspaceRes;
import com.dikkak.dto.workspace.QDesignerWorkspaceRes_WorkInfo;
import com.dikkak.entity.StatusType;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.dikkak.entity.coworking.QCoworking.coworking;
import static com.dikkak.entity.proposal.QProposal.proposal;
import static com.dikkak.entity.user.QUser.user;

public class ProposalRepositoryImpl implements ProposalRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProposalRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 클라이언트 작업실 조회 - 제안서, 외주작업실
    @Override
    public List<ClientWorkspaceRes> getClientWorkspace(Long clientId) {
        return queryFactory.select(new QClientWorkspaceRes(
                    proposal.id, proposal.title, coworking.id, user.name, coworking.complete
                ))
                .from(proposal)
                .leftJoin(coworking).on(coworking.proposal.eq(proposal))
                .leftJoin(user).on(user.eq(coworking.designer))
                .where(
                        proposal.client.id.eq(clientId),
                        proposal.status.eq(StatusType.ACTIVE)
                )
                .fetch();
    }

    // 디자이너 작업실 조회
    @Override
    public List<DesignerWorkspaceRes.WorkInfo> getDesignerWorkspace(Long designerId) {
        return queryFactory.select(new QDesignerWorkspaceRes_WorkInfo(
                        proposal.id, proposal.title, proposal.client.name, coworking.id, coworking.complete
                 ))
                .from(coworking)
                .join(proposal).on(coworking.proposal.eq(proposal))
                .where(coworking.designer.id.eq(designerId))
                .fetch();
    }

    // 제안서 삭제 - status 를 inactive 로 변경
    @Override
    public long updateProposalsInactive(Long clientId, List<Long> proposalList) {
        return queryFactory
                .update(proposal)
                .set(proposal.status, StatusType.INACTIVE)
                .where(proposal.client.id.eq(clientId),
                        proposal.id.in(proposalList))
                .execute();
    }
}
