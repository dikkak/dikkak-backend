package com.dikkak.controller.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.config.UserPrincipal;
import com.dikkak.dto.coworking.GetFileRes;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.s3.S3Downloader;
import com.dikkak.service.coworking.CoworkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dikkak.common.ResponseMessage.INVALID_ACCESS_TOKEN;
import static com.dikkak.common.ResponseMessage.UNAUTHORIZED_REQUEST;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/coworking/file")
@RequiredArgsConstructor
public class FileController {

    private final CoworkingService coworkingService;
    private final CoworkingSupport coworkingSupport;
    private final S3Downloader s3Downloader;

    /**
     * 외주작업실 파일 목록 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     * @param pageable page, size, sort
     */
    @GetMapping("/file")
    public List<GetFileRes> getFileList(@AuthenticationPrincipal UserPrincipal principal,
                                        @RequestParam Long coworkingId,
                                        @PageableDefault(size=10, page=0, sort="createdAt", direction = DESC) Pageable pageable) {
        if(principal == null) {
            throw new BaseException(INVALID_ACCESS_TOKEN);
        }

        Coworking coworking = coworkingSupport.checkUserAndGetCoworking(principal, coworkingId);
        if(coworking == null) {
            throw new BaseException(UNAUTHORIZED_REQUEST);
        }

        return coworkingService.getFileList(coworking.getId(), pageable);
    }

    /**
     * 외주작업실 파일 다운로드
     * @param fileName 파일 이름
     */
    @GetMapping("/file/{fileName}")
    public byte[] getFile(@PathVariable String fileName) {
        return s3Downloader.downloadFile("coworking/" + fileName);
    }
}
