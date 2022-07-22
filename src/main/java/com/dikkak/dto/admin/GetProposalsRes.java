package com.dikkak.dto.admin;

import com.dikkak.entity.proposal.Proposal;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetProposalsRes {

    private Long id;
    private String title;
    private LocalDateTime createdAt;

    public GetProposalsRes(Proposal proposal) {
        this.id = proposal.getId();
        this.title = proposal.getTitle();
        this.createdAt = proposal.getCreatedAt();
    }
}
