package com.dikkak.dto.workplace;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ProposalRes {

    private Long id;
    private String title;

    @QueryProjection
    public ProposalRes(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}
