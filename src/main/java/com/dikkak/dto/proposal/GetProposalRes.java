package com.dikkak.dto.proposal;

import com.dikkak.entity.proposal.CategoryEnum;
import com.dikkak.entity.proposal.Otherfile;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.proposal.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private List<ReferenceFile> referenceFile = new ArrayList<>();
    private List<EtcFile> etcFile = new ArrayList<>();
    private String additionalDesc;

    public GetProposalRes(Proposal proposal, String clientName) {
        this.client = clientName;
        this.title = proposal.getTitle();
        this.category = proposal.getCategory();
        this.detail = proposal.getDetail();
        this.deadline = proposal.getDeadline();
        this.purpose = proposal.getPurpose();
        this.mainColor = proposal.getMainColor();

        // , 기준으로 split해서 subColors 리스트 반환
        if(proposal.getSubColors() != null && !proposal.getSubColors().isEmpty()) {
            this.subColors.addAll(List.of(proposal.getSubColors().split(",")));
        }
        this.additionalDesc = proposal.getRequirements();
    }

    // 레퍼런스 파일 관련 정보 리스트에 추가
    public void addReferenceFile(Reference reference) {
        this.referenceFile.add(new ReferenceFile(reference.getFileUrl(), reference.getFileName(), reference.getDescription()));
    }

    // 기타 파일 관련 정보 리스트에 추가
    public void addEtcFile(Otherfile otherfile) {
        this.etcFile.add(new EtcFile(otherfile.getFileUrl(), otherfile.getFileName()));
    }

    // 레퍼런스 파일 관련 정보
    @AllArgsConstructor
    @Getter
    public static class ReferenceFile {
        private String url;
        private String fileName;
        private String description;
    }

    // 기타 파일 관련 정보
    @AllArgsConstructor
    @Getter
    public static class EtcFile {
        private String url;
        private String fileName;
    }
}
