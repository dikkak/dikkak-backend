package com.dikkak.service.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.controller.coworking.CoworkingSupport;
import com.dikkak.dto.message.FileMessage;
import com.dikkak.dto.message.FileReq;
import com.dikkak.dto.message.TextReq;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.CoworkingFile;
import com.dikkak.entity.coworking.CoworkingMessage;
import com.dikkak.entity.user.User;
import com.dikkak.repository.UserRepository;
import com.dikkak.repository.coworking.file.CoworkingFileRepository;
import com.dikkak.repository.coworking.message.CoworkingMessageRepository;
import com.dikkak.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import static com.dikkak.common.ResponseMessage.*;
import static org.apache.http.entity.ContentType.*;

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
     * 텍스트 메시지 저장
     */
    @Transactional
    public CoworkingMessage saveTextMessage(TextReq req, Coworking coworking) {
        User user = getUser(req.getEmail());

        if (!coworkingSupport.checkUser(user, coworking)) {
            throw new BaseException(UNAUTHORIZED_REQUEST);
        }

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
            fileUrl = s3Uploader.uploadFile(file, "coworking");
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
