package com.dikkak.entity.coworking;

import com.dikkak.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoworkingFile extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "coworking_file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coworking_step_id")
    private CoworkingStep coworkingStep;

    private String fileName;

    @Column(length = 500)
    private String fileUrl;

    private boolean isImageFile;
}
