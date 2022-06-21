package com.dikkak.entity.proposal;

import javax.persistence.*;

@Entity
public class Reference {

    @Id @GeneratedValue
    @Column(name = "reference_id")
    private Long id;

    private String fileUrl;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;
}
