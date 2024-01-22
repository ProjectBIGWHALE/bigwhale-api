package com.whale.web.utils;

import com.whale.web.exceptions.domain.WhaleInvalidImageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class UploadImage {
    public MultipartFile uploadImage(MultipartFile file) throws WhaleInvalidImageException {

        if(file.isEmpty()) {
            throw new WhaleInvalidImageException("Image cannot be null or empty");
        }

        try{
            byte[] bytes = file.getBytes();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            return new CustomMultipartFile(file.getOriginalFilename(), file.getContentType(), inputStream);
        }catch (IOException ex){
            throw new WhaleInvalidImageException("Error occurred while uploading the image: " + ex.getMessage());
        }
    }
}
