package com.dikkak.controller;

import com.dikkak.dto.common.BaseException;
import com.dikkak.dto.common.BaseResponse;
import com.dikkak.dto.proposal.GetProposalRes;
import com.dikkak.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dikkak.dto.common.ResponseMessage.INVALID_ACCESS_TOKEN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/proposal")
public class ProposalController {

    private final ProposalService proposalService;

    @GetMapping("/list")
    public ResponseEntity<?> getProposalList(@AuthenticationPrincipal Long userId) {

        if(userId == null)
            return ResponseEntity.badRequest().body(new BaseResponse<>(INVALID_ACCESS_TOKEN));

        try {
            List<GetProposalRes> proposals = proposalService.getUserProposal(userId);
            return ResponseEntity.ok().body(new BaseResponse<>(proposals));
        } catch (BaseException e) {
            return ResponseEntity.badRequest().body(e.getResponseMessage());
        }


    }
}
