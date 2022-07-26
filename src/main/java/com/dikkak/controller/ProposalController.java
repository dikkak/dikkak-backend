package com.dikkak.controller;

import com.dikkak.dto.common.BaseException;
import com.dikkak.dto.common.BaseResponse;
import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.dto.proposal.PostProposalRes;
import com.dikkak.entity.User;
import com.dikkak.entity.proposal.*;
import com.dikkak.s3.S3Uploader;
import com.dikkak.service.OtherFileService;
import com.dikkak.service.ProposalService;
import com.dikkak.service.ReferenceService;
import com.dikkak.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.dikkak.dto.common.ResponseMessage.FILE_UPLOAD_FAILED;
import static com.dikkak.dto.common.ResponseMessage.INVALID_ACCESS_TOKEN;

@RestController
@RequestMapping("/proposal")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;
    private final UserService userService;
    private final ReferenceService referenceService;
    private final OtherFileService otherFileService;
    private final S3Uploader s3Uploader;

    /**
     * 제안서 생성 api
     * @param userId 회원 id
     * @param jsonData 제안서 정보
     * @param referenceFile 레퍼런스 파일
     * @param etcFile 기타 파일
     */
    @PostMapping("")
    public ResponseEntity<?> createProposal(
            @AuthenticationPrincipal Long userId,
            @RequestPart PostProposalReq jsonData,
            @RequestPart(required = false) List<MultipartFile> referenceFile,
            @RequestPart(required = false) List<MultipartFile> etcFile) {

        try {

            if(userId == null)
                throw new BaseException(INVALID_ACCESS_TOKEN);
            User user = userService.getUser(userId);

            // 제안서 저장
            Proposal savedProposal = proposalService.create(user, jsonData);

            // 키워드 저장
            if(jsonData.getKeywords() != null && !jsonData.getKeywords().isEmpty()) {
                for (String keywordName : jsonData.getKeywords()) {
                    // 1. 저장되지 않은 키워드는 저장한다.
                    // 2. 제안서와 키워드를 매핑한다.
                    proposalService.saveKeyword(savedProposal, keywordName);
                }
            }

            // 레퍼런스 파일 저장
            if(referenceFile != null && !referenceFile.isEmpty()) {
                // s3에 파일 업로드
                List<String> referenceUrls = s3Uploader.uploadFiles(referenceFile, "reference");

                // db에 저장
                for(int i=0; i<referenceUrls.size(); i++) {
                    String url = referenceUrls.get(i);
                    referenceService.create(Reference.builder()
                                                    .proposal(savedProposal)
                                                    .fileUrl(url)
                                                    .description(jsonData.getReferenceDesc().get(i))
                                                    .build()
                    );
                }

            }

            // 기타 파일 저장
            if(etcFile != null && !etcFile.isEmpty()) {
                // s3에 파일 업로드
                List<String> otherUrls = s3Uploader.uploadFiles(etcFile, "otherFile");

                // db에 저장
                for (String url : otherUrls) {
                    otherFileService.create(Otherfile.builder()
                            .proposal(savedProposal)
                            .fileUrl(url)
                            .build()
                    );
                }
            }

            return ResponseEntity.ok().body(new PostProposalRes(savedProposal.getId()));
        } catch (IOException e){
            return ResponseEntity.internalServerError().body(FILE_UPLOAD_FAILED);
        } catch (BaseException e) {
            return ResponseEntity.badRequest().body(new BaseResponse(e));
        }
    }

    /**
     * 제안서 상세 조회 api
     * @param proposalId 제안서 id
     */
    @GetMapping("/{proposalId}")
    public ResponseEntity<?> getProposals(@PathVariable Long proposalId) {

        try {
            return ResponseEntity.ok().body(proposalService.getProposal(proposalId));
        } catch (BaseException e) {
            return ResponseEntity.badRequest().body(new BaseResponse(e));
        }

    }
}
