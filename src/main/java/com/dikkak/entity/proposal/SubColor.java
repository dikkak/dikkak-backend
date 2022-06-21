package com.dikkak.entity.proposal;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "sub_color")
public class SubColor {

    @Id @GeneratedValue
    @Column(name = "sub_color_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }
}