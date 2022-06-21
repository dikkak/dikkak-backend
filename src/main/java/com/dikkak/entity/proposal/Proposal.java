package com.dikkak.entity.proposal;

import com.dikkak.entity.BaseTime;
import com.dikkak.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
public class Proposal extends BaseTime {

    @Id @GeneratedValue
    @Column(name = "proposal_id")
    private Long id;


    /**
     * 공통 항목
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User client;

    @Column(nullable = false)
    private String title;   // 제목

    @Column(nullable = false)
    private String purpose; // 용도, 목적

    // 키워드
    @OneToMany(mappedBy = "proposal")
    private List<Proposal_DesignKeyword> keywords = new ArrayList<>();

    // 마감일
    @Column(nullable = false)
    private LocalDateTime deadline;

    // 레퍼런스 파일
    // 3개 ~ 5개
    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reference> references = new ArrayList<>();

    // 추가 요청사항
    @Lob
    @Column(nullable = false)
    private String requirements;


    /**
     * 옵션 항목
     */
    // 컬러 - 로고, 명함, 패키지 디자인, 포스터, 리플렛, 랜딩페이지, 기타
    // 메인 컬러
    @Column(name = "main_color")
    private String mainColor;

    // 서브 컬러
    // 1개 ~ 5개
    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubColor> subColors = new ArrayList<>();

    // 인쇄 여부 - 명함, 패키지 디자인
    @Column(name = "printing_required")
    private boolean printing;

    // 퍼블리싱 여부 - 랜딩 페이지
    @Column(name = "publishing_required")
    private boolean publishing;

    // 제품 촬영 필요 여부 - 상세페이지
    @Column(name = "photography_required")
    private boolean photography;

    // 영상 원본 길이(5분/10분/30분/1시간/2시간 이내, 기타) - 영상 편집
    @Column(name = "original_length")
    private Time originalLength;

    // 모델링 난이도(초급, 중급, 고급, 기타) - 3D 모델링
    private String modelingDifficulty;

    // 디자인 파일 유무 - 3D 모델링
    @Column(name = "design_file_presence")
    private boolean designFile;


    public void setSubColors(List<SubColor> subColors) {
        this.subColors = subColors;
    }
}