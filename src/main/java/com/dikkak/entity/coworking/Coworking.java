package com.dikkak.entity.coworking;

import com.dikkak.entity.BaseEntity;
import com.dikkak.entity.user.User;
import com.dikkak.entity.proposal.Proposal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designer_id")
    private User designer;

    // 현재 진행 단계
    @ColumnDefault("CHECK_PROPOSAL")
    @Enumerated(EnumType.STRING)
    private StepType progress = StepType.CHECK_PROPOSAL;

    public Coworking(Proposal proposal, User designer) {
        this.proposal = proposal;
        this.designer = designer;
    }
}
