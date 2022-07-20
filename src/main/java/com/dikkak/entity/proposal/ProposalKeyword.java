package com.dikkak.entity.proposal;

import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class ProposalKeyword {

    @Id @GeneratedValue
    @Column(name = "proposal_keyword_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    @Builder
    public ProposalKeyword(Proposal proposal, Keyword keyword) {
        this.proposal = proposal;
        this.keyword = keyword;
    }
}
