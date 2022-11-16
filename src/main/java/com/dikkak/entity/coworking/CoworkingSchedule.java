package com.dikkak.entity.coworking;

import com.dikkak.dto.coworking.GetScheduleRes;
import com.dikkak.dto.coworking.GetScheduleRes.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
public class CoworkingSchedule {

    @Id
    @GeneratedValue
    @Column(name = "coworking_schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coworking_step_id")
    private CoworkingStep coworkingStep;

    @Column(length = 280)
    @ColumnDefault("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0/0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0/0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0/0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0/0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0")
    private String clientSchedule; // 요일은 /로 구분, 시간은 ,로 구분

    @Column(length = 280)
    @ColumnDefault("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0/0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0/0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0/0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0/0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0")
    private String designerSchedule;

    private LocalDateTime deadline;

    public GetScheduleRes toDto() {
        return GetScheduleRes.builder().schedules(getScheduleList()).build();
    }

    // 문자열을 2차원 배열로 변환
    private List<List<Schedule>> getScheduleList() {
        List<List<Schedule>> schedules = new ArrayList<>();
        String[] client = clientSchedule.split("/");
        String[] designer = designerSchedule.split("/");
        for (int i = 0; i < client.length; i++) {
            List<Boolean> clientSchedule = Arrays.stream(client[i].split(",")).map(s -> s.equals("1")).collect(Collectors.toList());
            List<Boolean> designerSchedule = Arrays.stream(designer[i].split(",")).map(s -> s.equals("1")).collect(Collectors.toList());
            List<Schedule> daySchedule = new ArrayList<>();
            for (int j = 0; j < clientSchedule.size(); j++) {
                daySchedule.add(new Schedule(clientSchedule.get(j), designerSchedule.get(j)));
            }
            schedules.add(daySchedule);
        }
        return schedules;
    }
}
