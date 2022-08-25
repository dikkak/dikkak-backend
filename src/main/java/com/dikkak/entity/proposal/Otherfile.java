package com.dikkak.entity.proposal;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Otherfile {

    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @Column(length = 500)
    private String fileUrl;

    private String fileName;

    @Builder
    public Otherfile(String fileUrl, String fileName, Proposal proposal) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.proposal = proposal;
    }
}
