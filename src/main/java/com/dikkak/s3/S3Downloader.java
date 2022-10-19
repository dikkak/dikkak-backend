package com.dikkak.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.dikkak.common.BaseException;
import com.dikkak.common.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.dikkak.common.ResponseMessage.FILE_DOWNLOAD_FAILED;

@Component
@RequiredArgsConstructor
public class S3Downloader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public byte[] downloadFile(String filePath) throws BaseException {
        try {
            S3Object object = amazonS3Client.getObject(new GetObjectRequest(bucketName, filePath));
            S3ObjectInputStream s3ObjectInputStream = object.getObjectContent();
            return IOUtils.toByteArray(s3ObjectInputStream);
        } catch (Exception e) {
            throw new BaseException(FILE_DOWNLOAD_FAILED);
        }

    }


}
