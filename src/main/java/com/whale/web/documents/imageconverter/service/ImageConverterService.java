package com.whale.web.documents.imageconverter.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

import com.whale.web.documents.imageconverter.exception.*;
import com.whale.web.documents.imageconverter.model.ImageConversionForm;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageConverterService {

    public byte[] convertImageFormat(ImageConversionForm imageConversionForm, MultipartFile imageFile) throws IOException {

        isValidImageFormat(imageFile);
        isValidOutputFormat(imageConversionForm.getOutputFormat());

        try (InputStream fileInputStream = imageFile.getInputStream()) {
            BufferedImage image = ImageIO.read(fileInputStream);

            ByteArrayOutputStream convertedImage = new ByteArrayOutputStream();
            boolean successfullyConverted = ImageIO.write(image, imageConversionForm.getOutputFormat(), convertedImage);
            convertedImage.flush();

            if (!successfullyConverted) {
                throw new UnsuccessfulFileConversionException("Could not convert an image.");
            } else {
                return convertedImage.toByteArray();
            }
        }
    }


    private void isValidImageFormat(MultipartFile imageFile){
        if (imageFile == null || imageFile.isEmpty()) {
            throw new InvalidUploadedFileException("Uploaded image file is null, empty or image format is not supported");
        }

        if (!Arrays.asList("bmp", "jpg", "jpeg", "gif").contains(getFileExtension(imageFile))) {
            throw new InvalidFileFormatException("Unsupported file format. Please choose a BMP, JPG, JPEG or GIF file.");
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

    private void isValidOutputFormat(String outputFOrmat){
        if (!Arrays.asList("bmp", "jpg", "jpeg", "gif", "png", "tiff").contains(outputFOrmat)) {
            throw new InvalidFileFormatException("Unsupported file output format. Please choose a BMP, JPG, JPEG , GIF, PNG, TIFF.");
        }
    }
	
}
