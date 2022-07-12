package com.dikkak.entity.proposal;

import javax.persistence.*;

@Entity
public class Otherfile {

    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;
}
