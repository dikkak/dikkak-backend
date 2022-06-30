package com.dikkak.dto.workplace;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class WorkplaceRes {

    private List<ProposalRes> proposals = new ArrayList<>();
    private List<WorkRes> works = new ArrayList<>();
}
