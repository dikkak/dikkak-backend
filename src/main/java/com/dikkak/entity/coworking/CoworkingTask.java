package com.dikkak.entity.coworking;

import com.dikkak.entity.BaseEntity;
import lombok.Builder;
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
    @JoinColumn(name = "coworking_id")
    private Coworking coworking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coworking_file_id")
    private CoworkingFile file;

    private String content;

    @ColumnDefault("false")
    private boolean complete = false;

    @Builder
    public CoworkingTask(Coworking coworking, CoworkingFile file, String content) {
        this.coworking = coworking;
        this.file = file;
        this.content = content;
    }

    public void updateComplete(boolean complete) {
        this.complete = complete;
    }
}
