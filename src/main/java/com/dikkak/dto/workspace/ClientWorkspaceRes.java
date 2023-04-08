package com.dikkak.dto.workspace;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class ClientWorkspaceRes {
    private Long proposalId;
    private String proposalTitle;

    private Long coworkingId;
    private String designerName;
    private boolean complete;

    @QueryProjection
    public ClientWorkspaceRes(Long proposalId, String proposalTitle, Long coworkingId, String designerName, boolean complete) {
        this.proposalId = proposalId;
        this.proposalTitle = proposalTitle;
        this.coworkingId = coworkingId;
        this.designerName = designerName;
        this.complete = complete;
    }
}
