package com.whale.web.utils;

import com.whale.web.exceptions.domain.FileIsNullException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadFiles {

    private UploadFiles() {
        throw new IllegalStateException("UploadFiles");
    }
    public static List<MultipartFile> fileUploadAndValidation(List<MultipartFile> files) throws FileIsNullException {

        List<MultipartFile> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new FileIsNullException("File cannot be null");
            }

            try {
                byte[] bytes = file.getBytes();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                uploadedFiles.add(new CustomMultipartFile(file.getOriginalFilename(), file.getContentType(), inputStream));
            } catch (IOException ex) {
                throw new FileIsNullException(ex.getMessage());
            }
        }

        return uploadedFiles;
    }
}
