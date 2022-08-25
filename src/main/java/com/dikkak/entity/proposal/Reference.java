package com.dikkak.entity.proposal;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Reference {

    @Id @GeneratedValue
    @Column(name = "reference_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @Column(length = 500)
    private String fileUrl;

    private String fileName;

    private String description;

    @Builder
    public Reference(String fileUrl, String fileName, String description, Proposal proposal) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.description = description;
        this.proposal = proposal;
    }
}
