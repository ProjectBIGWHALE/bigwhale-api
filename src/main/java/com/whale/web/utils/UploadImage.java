package com.whale.web.utils;

import com.whale.web.exceptions.domain.ImageIsNullException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class UploadImage {
    public MultipartFile uploadImage(MultipartFile file) throws ImageIsNullException {

        if(file.isEmpty()) {
            throw new ImageIsNullException("Image cannot be null or empty");
        }

        try{
            byte[] bytes = file.getBytes();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            return new CustomMultipartFile(file.getOriginalFilename(), file.getContentType(), inputStream);
        }catch (IOException ex){
            throw new ImageIsNullException("Error occurred while uploading the image: " + ex.getMessage());
        }
    }
}
