package com.dikkak.controller;

import com.dikkak.common.BaseException;
import com.dikkak.config.UserPrincipal;
import com.dikkak.dto.mail.MailDto;
import com.dikkak.dto.proposal.DeleteProposalReq;
import com.dikkak.dto.proposal.GetProposalRes;
import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.dto.proposal.PostProposalRes;
import com.dikkak.entity.proposal.Otherfile;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.proposal.Reference;
import com.dikkak.entity.user.User;
import com.dikkak.s3.S3Downloader;
import com.dikkak.s3.S3Uploader;
import com.dikkak.service.MailService;
import com.dikkak.service.OtherFileService;
import com.dikkak.service.ProposalService;
import com.dikkak.service.ReferenceService;
import com.dikkak.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dikkak.common.ResponseMessage.FILE_UPLOAD_FAILED;
import static com.dikkak.common.ResponseMessage.REQUEST_ERROR;

@RestController
@RequestMapping("/proposal")
@RequiredArgsConstructor
@Slf4j
public class ProposalController {

    private final ProposalService proposalService;
    private final UserService userService;
    private final ReferenceService referenceService;
    private final OtherFileService otherFileService;
    private final S3Uploader s3Uploader;
    private final S3Downloader s3Downloader;
    private final MailService mailService;

    @Value("${dikkak.proposal.url}")
    private String proposalUrl;

    /**
     * 제안서 생성 api
     * @param principal 회원 id, 타입
     * @param jsonData 제안서 정보
     * @param referenceFile 레퍼런스 파일
     * @param etcFile 기타 파일
     * @return proposalId 생성된 제안서 id
     */
    @PostMapping("")
    public PostProposalRes createProposal(@LoginUser UserPrincipal principal,
                                          @RequestPart PostProposalReq jsonData,
                                          @RequestPart(required = false) List<MultipartFile> referenceFile,
                                          @RequestPart(required = false) List<MultipartFile> etcFile) {
        try {
            User user = userService.getUser(principal.getUserId());

            // 제안서 저장
            Proposal savedProposal = proposalService.create(user, jsonData);

            // 키워드 저장
            if (jsonData.getKeywords() != null && !jsonData.getKeywords().isEmpty()) {
                for (String keywordName : jsonData.getKeywords()) {
                    // 1. 저장되지 않은 키워드는 저장한다.
                    // 2. 제안서와 키워드를 매핑한다.
                    proposalService.saveKeyword(savedProposal, keywordName);
                }
            }

            // 레퍼런스 파일 저장
            if (referenceFile != null && !referenceFile.isEmpty()) {
                // s3에 파일 업로드
                List<String> referenceUrls = s3Uploader.uploadFiles(referenceFile, "reference");
                List<String> referenceNames = referenceFile.stream().map(MultipartFile::getOriginalFilename).collect(Collectors.toList());

                // db에 저장
                for (int i = 0; i < referenceUrls.size(); i++) {
                    referenceService.create(Reference.builder()
                            .proposal(savedProposal)
                            .fileUrl(referenceUrls.get(i))
                            .fileName(referenceNames.get(i))
                            .description(jsonData.getReferenceDesc().get(i))
                            .build()
                    );
                }
            }

            // 기타 파일 저장
            if (etcFile != null && !etcFile.isEmpty()) {
                // s3에 파일 업로드
                List<String> otherUrls = s3Uploader.uploadFiles(etcFile, "otherFile");
                List<String> names = etcFile.stream().map(MultipartFile::getOriginalFilename).collect(Collectors.toList());

                // db에 저장
                for (int i = 0; i < etcFile.size(); i++) {
                    otherFileService.create(Otherfile.builder()
                            .proposal(savedProposal)
                            .fileUrl(otherUrls.get(i))
                            .fileName(names.get(i))
                            .build()
                    );
                }
            }

            // 관리자에게 메일 전송
            sendMailToAdmin(savedProposal);

            return new PostProposalRes(savedProposal.getId());

        } catch (IOException e) {
            throw new BaseException(FILE_UPLOAD_FAILED);
        }
    }

    // 관리자에게 메일 전송
    private void sendMailToAdmin(Proposal proposal) {
        mailService.sendMail(
                MailDto.builder()
                        .emailList(userService.getAdminEmail())
                        .title("[제안서 생성] "+ proposal.getTitle())
                        .content(
                                "클라이언트 이름 : " + proposal.getClient().getName() + "\n" +
                                "클라이언트 이메일 : " + proposal.getClient().getEmail() + "\n" +
                                "클라이언트 연락처 : " + proposal.getClient().getPhoneNumber() + "\n" +
                                "제안서가 생성되었습니다.\n" +
                                proposalUrl+ proposal.getId())
                        .build()
        );
    }

    /**
     * 제안서 상세 조회 api
     * @param proposalId 제안서 id
     */
    @GetMapping("/{proposalId}")
    public GetProposalRes getProposals(@PathVariable Long proposalId) {
        return proposalService.getProposal(proposalId);
    }

    /**
     * 제안서 삭제 API
     * @param req 제안서 id 리스트
     * @return 삭제된 제안서 개수
     */
    @PatchMapping("/inactive")
    public Map<String,Long> deleteProposals(@LoginUser UserPrincipal principal, @RequestBody DeleteProposalReq req) {
        if (req.getProposalList() == null || req.getProposalList().isEmpty()) {
            throw new BaseException(REQUEST_ERROR);
        }

        long count = proposalService.deleteProposalList(req.getProposalList(), principal.getUserId());
        return Map.of("count", count);
    }

    /**
     * 레퍼런스 파일 다운로드 API
     * @param fileName 레퍼런스 파일 이름
     * @return byte 배열
     */
    @GetMapping("/file/reference/{fileName}")
    public byte[] getReferenceFile(@PathVariable String fileName) {
        return s3Downloader.downloadFile("reference/" + fileName);
    }

    /**
     * 기타 파일 다운로드 API
     * @param fileName 기타 파일 이름
     * @return blob
     */
    @GetMapping("/file/otherFile/{fileName}")
    public byte[] getOtherFile(@PathVariable String fileName) {
        return s3Downloader.downloadFile("otherFile/" + fileName);
    }
}
