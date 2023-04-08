package com.dikkak.entity.coworking;

import com.dikkak.entity.BaseEntity;
import com.dikkak.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CoworkingMessage extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "coworking_message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coworking_step_id")
    private CoworkingStep coworkingStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 1000)
    private String content;

    @Column(length = 500)
    private String fileUrl;
}
