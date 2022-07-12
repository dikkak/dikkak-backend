package com.dikkak.entity.proposal;

import javax.persistence.*;

@Entity
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

}
