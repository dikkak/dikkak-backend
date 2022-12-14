package com.dikkak.dto.workplace;

import com.dikkak.entity.coworking.StepType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class ClientWorkspaceRes {
    private Long proposalId;
    private String proposalTitle;

    private Long coworkingId;
    private String designerName;
    private StepType coworkingStep;

    @QueryProjection
    public ClientWorkspaceRes(Long proposalId, String proposalTitle, Long coworkingId, String designerName, StepType coworkingStep) {
        this.proposalId = proposalId;
        this.proposalTitle = proposalTitle;
        this.coworkingId = coworkingId;
        this.designerName = designerName;
        this.coworkingStep = coworkingStep;
    }
}
