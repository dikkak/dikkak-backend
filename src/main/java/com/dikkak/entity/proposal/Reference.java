package com.dikkak.entity.proposal;

import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Reference {

    @Id @GeneratedValue
    @Column(name = "reference_id")
    private Long id;

    @Column(length = 500)
    private String fileUrl;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @Builder
    public Reference(String fileUrl, String description, Proposal proposal) {
        this.fileUrl = fileUrl;
        this.description = description;
        this.proposal = proposal;
    }
}
