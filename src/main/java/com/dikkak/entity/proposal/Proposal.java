package com.dikkak.entity.proposal;

import com.dikkak.entity.BaseEntity;
import com.dikkak.entity.CategoryEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    // 카테고리
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryEnum category;

    // 제목
    @Column(nullable = false)
    private String title;

    // 세부사항
    @Column(nullable = false)
    private String detail;

    // 용도, 목적
    @Column(nullable = false)
    private String purpose;

    // 키워드
    @OneToMany(mappedBy = "proposal")
    private List<ProposalKeyword> keywords;        // ,로 구분

    // 마감일
    @Column(nullable = false)
    private LocalDateTime deadline;

    // 레퍼런스 파일 (이미지 파일)
    // 3개 ~ 5개
    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reference> references = new ArrayList<>();

    // 기타 파일
    // 최대 0개 ~ 5개
    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Otherfile> otherFiles = new ArrayList<>();

    // 메인 컬러
    @Column(name = "main_color", nullable = false)
    private String mainColor;

    // 서브 컬러
    // 0개 ~ 5개
    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubColor> subColors = new ArrayList<>();


    // 추가 요청사항
    @Lob
    private String requirements;

    public void setSubColors(List<SubColor> subColors) {
        this.subColors = subColors;
    }
}