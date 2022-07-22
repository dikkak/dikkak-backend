package com.dikkak.entity.proposal;

import com.dikkak.entity.BaseEntity;
import com.dikkak.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class UserProposal extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;


    @Builder
    public UserProposal(User user, Proposal proposal) {
        this.user = user;
        this.proposal = proposal;
    }
}
