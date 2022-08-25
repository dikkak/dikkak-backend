package com.dikkak.dto.proposal;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeleteProposalReq {

    List<Long> proposalList = new ArrayList<>();
}
