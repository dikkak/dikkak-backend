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

    @Column(length = 500)
    private String fileUrl;

    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @Builder
    public Otherfile(String fileUrl, String fileName, Proposal proposal) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.proposal = proposal;
    }
}
