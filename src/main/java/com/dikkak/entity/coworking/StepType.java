package com.dikkak.entity.coworking;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum StepType {
    // 0. 작업 내용 확인
    CHECK_PROPOSAL("작업 내용 확인"),

    // 1. 시안 작업
    DRAFT_WORK("시안 작업"),

    // 2. 1차 컨펌
    FIRST_CONFIRM("1차 컨펌"),

    // 3. 1차 수정 작업
    FIRST_MODIFICATION_WORK("1차 수정 작업"),

    // 4. 2차 컨펌
    SECOND_CONFIRM("2차 컨펌"),

    // 5. 2차 수정 작업
    SECOND_MODIFICATION_WORK("2차 수정 작업"),

    // 6. 3차 컨펌
    THIRD_CONFIRM("3차 컨펌"),

    // 7. 3차 수정 작업
    THIRD_MODIFICATION_WORK("3차 수정 작업"),

    // 8. 최종 작업물 전달 및 수령
    FINAL_WORK_DELIVERY("최종 작업물 전달 및 수령"),

    // 9. 사후평가 및 작업종료
    POST_EVALUATION("사후평가 및 작업종료"),

    // 10. 종료
    TERMINATION("작업 종료");

    private final String krName;

}
