package com.whale.web.design.imageconverter.service;

import com.whale.web.exceptions.domain.WhaleIOException;
import com.whale.web.exceptions.domain.WhaleRunTimeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Service
public class ImageConverterService {

    public byte[] convertImageFormat(String outputFormat, MultipartFile imageFile) throws WhaleIOException {

        isValidImageFormat(imageFile);
        isValidOutputFormat(outputFormat);

        try (InputStream fileInputStream = imageFile.getInputStream()) {
            BufferedImage image = ImageIO.read(fileInputStream);
            ByteArrayOutputStream convertedImage = new ByteArrayOutputStream();
            ImageIO.write(image, outputFormat.toLowerCase(), convertedImage);
            convertedImage.flush();
            return convertedImage.toByteArray();

        }catch (IOException e){
            throw new WhaleIOException("Error converting image: " + e.getMessage());
        }
    }

    private void isValidImageFormat(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new WhaleRunTimeException("Uploaded image file is null, empty or image format is not supported");
        }

        if (!Arrays.asList("bmp", "jpg", "jpeg", "gif").contains(getFileExtension(imageFile))) {
            throw new WhaleRunTimeException("Please choose a valid image format: bmp, jpg, jpeg, or gif file.");
        }
    }

    private String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            int lastIndex = fileName.lastIndexOf('.');
            if (lastIndex >= 0) {
                return fileName.substring(lastIndex + 1).toLowerCase();
            }
        }
        return null;
    }

    private void isValidOutputFormat(String outputFOrmat) {
        if (!Arrays.asList("bmp", "jpg", "jpeg", "gif", "png", "tiff").contains(outputFOrmat.toLowerCase())) {
            throw new WhaleRunTimeException("Please choose a valid image format: bmp, jpg, jpeg, gif, png, tiff.");
        }
    }
}
