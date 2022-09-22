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

    // 0 1차 컨펌, 1 2차 컨펌, 2 3차 컨펌, 3 최종
    private int step;

    @Column(length = 520)
    private String clientSchedule;

    @Column(length = 520)
    private String designerSchedule;

    private LocalDateTime deadline;
}
