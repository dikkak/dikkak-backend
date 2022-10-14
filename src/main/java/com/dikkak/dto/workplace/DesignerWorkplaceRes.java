package com.dikkak.dto.workplace;

import com.dikkak.entity.coworking.StepType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class DesignerWorkplaceRes {
    private List<WorkInfo> complete = new ArrayList<>();
    private List<WorkInfo> progress = new ArrayList<>();

    @Getter
    public static class WorkInfo {
        private Long proposalId;
        private String proposalTitle;
        private String clientName;
        private Long coworkingId;
        private StepType coworkingStep;

        @QueryProjection
        public WorkInfo(Long proposalId, String proposalTitle, String clientName, Long coworkingId, StepType coworkingStep) {
            this.proposalId = proposalId;
            this.proposalTitle = proposalTitle;
            this.clientName = clientName;
            this.coworkingId = coworkingId;
            this.coworkingStep = coworkingStep;
        }
    }
}
