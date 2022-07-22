package com.dikkak.entity.work;

import com.dikkak.entity.BaseEntity;
import com.dikkak.entity.proposal.Proposal;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Coworking extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "coworking_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @Column(nullable = false)
    private String title;

    // 진행 스탭: 1~9
    private Integer step = 1;



}
