package com.whale.web.utils;

import com.whale.web.exceptions.domain.WhaleInvalidFileException;
import com.whale.web.exceptions.domain.WhaleCheckedException;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class UploadFiles {

    private UploadFiles() {
        throw new IllegalStateException("UploadFiles");
    }
    public static List<MultipartFile> fileUploadAndValidation(List<MultipartFile> files) throws WhaleInvalidFileException, WhaleCheckedException {

        List<MultipartFile> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new WhaleInvalidFileException("Uploaded file is null or empty.");
            }

            try {
                byte[] bytes = file.getBytes();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                uploadedFiles.add(new CustomMultipartFile(file.getOriginalFilename(), file.getContentType(), inputStream));
            } catch (Exception e) {
                throw new WhaleCheckedException("An error occurred while uploading the file: " + e.getMessage());
            }
        }

        return uploadedFiles;
    }
}
