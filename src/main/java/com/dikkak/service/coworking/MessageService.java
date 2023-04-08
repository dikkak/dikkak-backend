package com.dikkak.service.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.config.UserPrincipal;
import com.dikkak.controller.coworking.CoworkingSupport;
import com.dikkak.dto.PageCustom;
import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.dto.coworking.message.FileMessage;
import com.dikkak.dto.coworking.FileReq;
import com.dikkak.dto.coworking.message.Message;
import com.dikkak.dto.coworking.message.MessageType;
import com.dikkak.dto.coworking.TextReq;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.CoworkingFile;
import com.dikkak.entity.coworking.CoworkingMessage;
import com.dikkak.entity.user.User;
import com.dikkak.repository.UserRepository;
import com.dikkak.repository.coworking.file.CoworkingFileRepository;
import com.dikkak.repository.coworking.message.CoworkingMessageRepository;
import com.dikkak.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.dikkak.common.Const.COWORKING_FILE_PATH;
import static com.dikkak.common.ResponseMessage.FILE_UPLOAD_FAILED;
import static com.dikkak.common.ResponseMessage.NON_EXISTENT_EMAIL;
import static org.apache.http.entity.ContentType.IMAGE_BMP;
import static org.apache.http.entity.ContentType.IMAGE_GIF;
import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;
import static org.apache.http.entity.ContentType.IMAGE_TIFF;
import static org.apache.http.entity.ContentType.IMAGE_WEBP;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {

    private final CoworkingMessageRepository messageRepository;
    private final CoworkingFileRepository fileRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final CoworkingSupport coworkingSupport;

    /**
     * 채팅 목록 조회
     */
    public PageCustom<Message<GetChattingRes>> getMessageList(Coworking coworking, Pageable pageable) {
        Page<GetChattingRes> messages = messageRepository.getCoworkingMessage(coworking, pageable);
        return PageCustom.<Message<GetChattingRes>>builder()
                .content(mapToMessage(coworking, messages.getContent()))
                .hasNext(messages.hasNext())
                .hasPrev(messages.hasPrevious())
                .next(pageable.getPageNumber()+1)
                .prev(pageable.getPageNumber()-1)
                .build();
    }

    private static List<Message<GetChattingRes>> mapToMessage(Coworking coworking, List<GetChattingRes> messages) {
        return messages
                .stream()
                .map(res -> Message.<GetChattingRes>builder()
                        .type((res.getFileName() == null) ? MessageType.TEXT : MessageType.FILE)
                        .coworkingId(coworking.getId())
                        .data(res)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 텍스트 메시지 저장
     */
    @Transactional
    public CoworkingMessage saveTextMessage(TextReq req, Coworking coworking) {
        User user = getUser(req.getEmail());
        coworkingSupport.checkCoworkingUser(new UserPrincipal(user), coworking);

        // coworking message 저장
        return messageRepository.save(
                CoworkingMessage.builder()
                        .coworking(coworking)
                        .user(user)
                        .content(req.getContent())
                        .build());
    }

    /**
     * 파일 메시지 저장
     */
    @Transactional
    public FileMessage saveFileMessage(FileReq request, Coworking coworking, MultipartFile file) {
        User user = getUser(request.getEmail());

        String fileUrl;
        try {
            fileUrl = s3Uploader.uploadFile(file, COWORKING_FILE_PATH);
        } catch (IOException e) {
            throw new BaseException(FILE_UPLOAD_FAILED);
        }

        // 이미지 파일 여부
        boolean imageFile = isImageFile(Objects.requireNonNull(file.getContentType()));


        // coworking file 저장
        CoworkingFile coworkingFile = fileRepository.save(
                CoworkingFile.builder()
                        .coworking(coworking)
                        .fileName(file.getOriginalFilename())
                        .fileUrl(fileUrl)
                        .isImageFile(imageFile)
                        .build()
        );

        // coworking message 저장
        CoworkingMessage coworkingMessage = messageRepository.save(
                CoworkingMessage.builder()
                        .coworking(coworking)
                        .user(user)
                        .coworkingFile(coworkingFile)
                        .build()
        );

        return FileMessage.builder()
                .email(request.getEmail())
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .createdAt(coworkingMessage.getCreatedAt())
                .isImageFile(coworkingFile.isImageFile())
                .build();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(NON_EXISTENT_EMAIL));
    }

    private boolean isImageFile(String contentType) {
        return contentType.equals(IMAGE_JPEG.getMimeType())
                || contentType.equals(IMAGE_PNG.getMimeType())
                || contentType.equals(IMAGE_GIF.getMimeType())
                || contentType.equals(IMAGE_BMP.getMimeType())
                || contentType.equals(IMAGE_TIFF.getMimeType())
                || contentType.equals(IMAGE_WEBP.getMimeType());
    }
}
