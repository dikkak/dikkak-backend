package com.dikkak.entity.coworking;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class CoworkingStep {

    @Id @GeneratedValue
    @Column(name = "coworking_step_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coworking_id")
    private Coworking coworking;

    @Column(nullable = false)
    private int step;

    @ColumnDefault("0")
    private int taskCount = 0;

    @Builder
    public CoworkingStep(Coworking coworking, int step) {
        this.coworking = coworking;
        this.step = step;
    }

}
