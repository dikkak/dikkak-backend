package com.dikkak.entity.coworking;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class CoworkingSchedule {

    @Id
    @GeneratedValue
    @Column(name = "coworking_schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coworking_id")
    private Coworking coworking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coworking_step_id")
    private CoworkingStep coworkingStep;

    @Column(length = 520)
    private String clientSchedule;

    @Column(length = 520)
    private String designerSchedule;

    private LocalDateTime deadline;
}
