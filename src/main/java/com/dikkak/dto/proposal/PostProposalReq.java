package com.dikkak.dto.proposal;

import com.dikkak.entity.proposal.CategoryEnum;
import lombok.Data;

import java.util.List;

@Data
public class PostProposalReq {

    private String title;
    private CategoryEnum category;
    private String detail;  // null 포함
    private String purpose;
    private String deadline;
    private List<String> keywords;
    private String mainColor;
    private List<String> subColors;
    private List<String> referencesDesc; // 레퍼런스 설명
    private String requirements;
}
