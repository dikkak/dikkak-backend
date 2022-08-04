package com.dikkak.dto.proposal;

import com.dikkak.entity.proposal.CategoryEnum;
import com.dikkak.entity.proposal.Proposal;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class GetProposalRes {

    private String client;  // 클라이언트 이름
    private String title;
    private CategoryEnum category;
    private String detail;
    private String purpose;
    private LocalDateTime deadline;
    private List<String> keywords = new ArrayList<>();
    private String mainColor;
    private List<String> subColors = new ArrayList<>();
    private List<String> referenceFile = new ArrayList<>();
    private List<String> referenceDesc = new ArrayList<>();
    private List<String> etcFile = new ArrayList<>();
    private String additionalDesc;

    public GetProposalRes(Proposal proposal, String clientName) {
        this.client = clientName;
        this.title = proposal.getTitle();
        this.category = proposal.getCategory();
        this.detail = proposal.getDetail();
        this.deadline = proposal.getDeadline();
        this.purpose = proposal.getPurpose();
        this.mainColor = proposal.getMainColor();
        if(proposal.getSubColors() != null && !proposal.getSubColors().isEmpty()) {
            this.subColors.addAll(List.of(proposal.getSubColors().split(",")));
        }
        this.additionalDesc = proposal.getRequirements();
    }
}
