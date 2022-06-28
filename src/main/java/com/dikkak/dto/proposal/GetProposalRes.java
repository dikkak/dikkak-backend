package com.dikkak.dto.proposal;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class GetProposalRes {

    private Long id;
    private String title;

    @QueryProjection
    public GetProposalRes(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}
