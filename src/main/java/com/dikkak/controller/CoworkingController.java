package com.dikkak.controller;

import com.dikkak.common.BaseException;
import com.dikkak.config.UserPrincipal;
import com.dikkak.dto.coworking.*;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.StepType;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.s3.S3Downloader;
import com.dikkak.service.CoworkingService;
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
@RequestMapping("/coworking")
@RequiredArgsConstructor
public class CoworkingController {

    private final CoworkingService coworkingService;
    private final S3Downloader s3Downloader;

    /**
     * 외주작업실의 해당 step의 채팅 목록 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     */
    @GetMapping("/chat")
    public List<Message<GetChattingRes>> getChatList(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long coworkingId) throws BaseException {

        if(principal == null) throw new BaseException(INVALID_ACCESS_TOKEN);
        if(!checkUser(principal, coworkingId)) throw new BaseException(UNAUTHORIZED_REQUEST);

        return coworkingService.getMessageList(coworkingId);
    }

    /**
     * 외주작업실 task 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     */
    @GetMapping("/task")
    public List<Message<GetTaskRes>> getTask(@AuthenticationPrincipal UserPrincipal principal,
                                             @RequestParam Long coworkingId) throws BaseException {
        if(principal == null) throw new BaseException(INVALID_ACCESS_TOKEN);
        if(!checkUser(principal, coworkingId)) throw new BaseException(UNAUTHORIZED_REQUEST);

        return coworkingService.getTaskList(coworkingId);
    }

    /**
     * 외주작업실 파일 목록 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     * @param pageable page, size, sort
     */
    @GetMapping("/file")
    public List<GetFileRes> getFileList(@AuthenticationPrincipal UserPrincipal principal,
                                        @RequestParam Long coworkingId,
                                        @PageableDefault(
                                                size=10, page=0,
                                                sort="createdAt", direction = DESC) Pageable pageable) throws BaseException {
        if(principal == null) throw new BaseException(INVALID_ACCESS_TOKEN);
        if(!checkUser(principal, coworkingId)) throw new BaseException(UNAUTHORIZED_REQUEST);
        return coworkingService.getFileList(coworkingId, pageable);
    }

    /**
     * 외주작업실 파일 다운로드
     * @param fileName 파일 이름
     */
    @GetMapping("/file/{fileName}")
    public byte[] getFile(@PathVariable String fileName) throws BaseException {
        return s3Downloader.downloadFile("coworking/" + fileName);
    }


    /**
     * 외주작업실 schedule 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     * @param step 외주 작업 step
     */
    @GetMapping("/schedule")
    public GetScheduleRes getSchedule(@AuthenticationPrincipal UserPrincipal principal,
                                      @RequestParam Long coworkingId,
                                      @RequestParam StepType step) throws BaseException {
        if(principal == null) throw new BaseException(INVALID_ACCESS_TOKEN);
        if(!checkUser(principal, coworkingId)) throw new BaseException(UNAUTHORIZED_REQUEST);
        return coworkingService.getSchedule(coworkingId, step);
    }


    // 작업실 접근권한이 있는 회원인지 검사
    private boolean checkUser(UserPrincipal principal, Long coworkingId) throws BaseException {
        UserTypeEnum type = principal.getType();
        if (type.equals(UserTypeEnum.ADMIN)) return true;
        if (type.equals(UserTypeEnum.UNDEFINED)) return false;
        else {
            Coworking coworking = coworkingService.getCoworking(coworkingId);
            if (type.equals(UserTypeEnum.CLIENT))
                return principal.getUserId().equals(coworking.getProposal().getClient().getId());
            else if (type.equals(UserTypeEnum.DESIGNER))
                return principal.getUserId().equals(coworking.getDesigner().getId());
        }
        return false;
    }

}
