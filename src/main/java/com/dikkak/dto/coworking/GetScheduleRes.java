package com.dikkak.dto.coworking;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetScheduleRes {

    private List<List<Schedule>> schedules;
    private LocalDateTime deadline;

    @Data
    public static class Schedule {
        private boolean client;
        private boolean designer;

        public Schedule(boolean client, boolean designer) {
            this.client = client;
            this.designer = designer;
        }
    }

    @Builder
    public GetScheduleRes(List<List<Schedule>> schedules, LocalDateTime deadline) {
        this.schedules = schedules;
        this.deadline = deadline;
    }
}
