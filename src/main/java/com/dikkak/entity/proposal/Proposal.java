package com.dikkak.entity.proposal;

import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.entity.BaseEntity;
import com.dikkak.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor
public class Proposal extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "proposal_id")
    private Long id;

    /**
     * 공통 항목
     */
    // 클라이언트
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User client;

    // 카테고리
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryEnum category;

    // 제목
    @Column(nullable = false)
    private String title;

    // 세부사항
    private String detail;

    // 용도, 목적
    @Column(nullable = false)
    private String purpose;

    // 마감일
    @Column(nullable = false)
    private LocalDateTime deadline;

    // 메인 컬러
    @Column(name = "main_color", nullable = false)
    private String mainColor;

    // 서브 컬러
    // 0개 ~ 5개
    private String subColors;


    // 추가 요청사항
    @Lob
    private String requirements;

    public Proposal(User client, PostProposalReq req) {
        this.client = client;
        this.category = req.getCategory();
        this.title = req.getTitle();
        this.detail = req.getDetail();
        this.purpose = req.getPurpose();
        this.deadline = LocalDateTime.parse(req.getDeadline() +"T00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.mainColor = req.getMainColor();

        if(req.getSubColors()!= null && !req.getSubColors().isEmpty())
            this.subColors = String.join(",", req.getSubColors());

        this.requirements = req.getAdditionalDesc();
    }
}