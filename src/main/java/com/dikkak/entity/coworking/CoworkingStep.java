package com.dikkak.entity.coworking;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class CoworkingStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coworking_step_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coworking_id")
    private Coworking coworking;

    @Enumerated(EnumType.STRING)
    private StepType type;

    @Builder
    public CoworkingStep(Coworking coworking, StepType type) {
        this.coworking = coworking;
        this.type = type;
    }
}
