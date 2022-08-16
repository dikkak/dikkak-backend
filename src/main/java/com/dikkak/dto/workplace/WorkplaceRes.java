package com.dikkak.dto.workplace;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class WorkplaceRes {
    private Long proposalId;
    private String proposalTitle;

    private Long coworkingId;
    private String coworkingTitle;
    private Integer coworkingStep;

    @QueryProjection
    public WorkplaceRes(Long proposalId, String proposalTitle, Long coworkingId, String coworkingTitle, Integer coworkingStep) {
        this.proposalId = proposalId;
        this.proposalTitle = proposalTitle;
        this.coworkingId = coworkingId;
        this.coworkingTitle = coworkingTitle;
        this.coworkingStep = coworkingStep;
    }

}
