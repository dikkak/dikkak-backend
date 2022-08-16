package com.dikkak.dto.workplace;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class ClientWorkplaceRes {
    private Long proposalId;
    private String proposalTitle;

    private Long coworkingId;
    private String designerName;
    private Integer coworkingStep;

    @QueryProjection
    public ClientWorkplaceRes(Long proposalId, String proposalTitle, Long coworkingId, String designerName, Integer coworkingStep) {
        this.proposalId = proposalId;
        this.proposalTitle = proposalTitle;
        this.coworkingId = coworkingId;
        this.designerName = designerName;
        this.coworkingStep = coworkingStep;
    }
}
