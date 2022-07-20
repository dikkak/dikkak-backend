package com.dikkak.entity.proposal;

import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Otherfile {

    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    @Column(length = 500)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @Builder
    public Otherfile(String fileUrl, Proposal proposal) {
        this.fileUrl = fileUrl;
        this.proposal = proposal;
    }
}
