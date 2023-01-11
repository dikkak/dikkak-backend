package com.dikkak.controller.coworking;

import com.dikkak.config.UserPrincipal;
import com.dikkak.controller.LoginUser;
import com.dikkak.dto.coworking.GetFileRes;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.s3.S3Downloader;
import com.dikkak.service.coworking.CoworkingService;
import com.dikkak.service.coworking.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/coworking/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final CoworkingSupport coworkingSupport;
    private final CoworkingService coworkingService;
    private final S3Downloader s3Downloader;
    private static final String COWORKING_FILE_PATH = "coworking/";

    /**
     * 외주작업실 파일 목록 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     * @param pageable page, size, sort
     */
    @GetMapping
    public List<GetFileRes> getFileList(@LoginUser UserPrincipal principal,
                                        @RequestParam Long coworkingId,
                                        @PageableDefault(size=10, page=0, sort="createdAt", direction = DESC) Pageable pageable) {
        Coworking coworking = coworkingService.getCoworking(coworkingId);
        coworkingSupport.checkCoworkingUser(principal, coworking);
        return fileService.getFileList(coworking.getId(), pageable);
    }

    /**
     * 외주작업실 파일 다운로드
     * @param fileName 파일 이름
     */
    @GetMapping("/{fileName}")
    public byte[] getFile(@PathVariable String fileName) {
        return s3Downloader.downloadFile(COWORKING_FILE_PATH + fileName);
    }
}
