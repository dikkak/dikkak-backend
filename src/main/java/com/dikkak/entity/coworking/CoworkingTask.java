package com.dikkak.entity.coworking;

import com.dikkak.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class CoworkingTask extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "coworking_task_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coworking_step_id")
    private CoworkingStep coworkingStep;

    private String content;

    @ColumnDefault("false")
    private boolean complete = false;
}
