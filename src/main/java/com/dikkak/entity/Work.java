package com.dikkak.entity;

import com.dikkak.entity.proposal.Proposal;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Work extends BaseTime {

    @Id @GeneratedValue
    @Column(name = "work_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @Column(nullable = false)
    private String title;

    // 완료 여부
    private boolean complete;

}
