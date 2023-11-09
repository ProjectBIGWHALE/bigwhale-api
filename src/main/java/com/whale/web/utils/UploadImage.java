package com.whale.web.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.whale.web.utils.CustomMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadImage {
    public MultipartFile uploadImage(MultipartFile file) throws IOException {

        byte[] bytes = file.getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        return new CustomMultipartFile(file.getOriginalFilename(), file.getContentType(), inputStream);
    }
}
