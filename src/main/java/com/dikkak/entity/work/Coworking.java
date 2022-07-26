package com.dikkak.entity.work;

import com.dikkak.entity.BaseEntity;
import com.dikkak.entity.proposal.UserProposal;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_proposal_id")
    private UserProposal proposal;

    // 진행 스탭: 1~9
    private Integer step = 1;

    public Coworking(UserProposal proposal) {
        this.proposal = proposal;
    }
}
