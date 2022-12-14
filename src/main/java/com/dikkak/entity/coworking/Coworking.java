package com.dikkak.entity.coworking;

import com.dikkak.entity.BaseEntity;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.user.User;
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

    @ColumnDefault("false")
    private boolean complete;

    public Coworking(Proposal proposal, User designer) {
        this.proposal = proposal;
        this.designer = designer;
    }
}
