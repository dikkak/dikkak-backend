package com.dikkak.entity.proposal;

import javax.persistence.*;

@Entity
public class Proposal_DesignKeyword {

    @Id @GeneratedValue
    @Column(name = "pd_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    private DesignKeyword keyword;
}