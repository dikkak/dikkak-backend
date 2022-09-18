package com.dikkak.dto.admin;

import com.dikkak.entity.proposal.Proposal;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class GetProposalListRes {

    private int totalPages;
    private long totalCount;
    private int page;
    private int size;
    private List<ProposalInfo> contents = new ArrayList<>();

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class ProposalInfo {
        private final Long proposalId;
        private final Long clientId;
        private final String title;
        private final LocalDateTime createdAt;

        public ProposalInfo(Proposal proposal) {
            this.proposalId = proposal.getId();
            this.clientId = proposal.getClient().getId();
            this.title = proposal.getTitle();
            this.createdAt = proposal.getCreatedAt();
        }
    }

    @Builder
    public GetProposalListRes(int totalPages, long totalCount, int page, int size, List<Proposal> contents) {
        this.totalPages = totalPages;
        this.totalCount = totalCount;
        this.page = page;
        this.size = size;
        this.contents.addAll(contents.stream()
                .map(ProposalInfo::new)
                .collect(Collectors.toList())
        );
    }
}
