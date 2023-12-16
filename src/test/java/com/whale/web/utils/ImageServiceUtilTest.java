package com.whale.web.utils;

import com.whale.web.exceptions.domain.WhaleCheckedException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageServiceUtilTest {
    public static MockMultipartFile createTestImage(String inputFormat, String name) throws WhaleCheckedException {
        try {
            BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, 100, 100);
            graphics.dispose();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, inputFormat, baos);
            baos.toByteArray();
            return new MockMultipartFile(
                    name,
                    "test-image." + inputFormat,
                    MediaType.IMAGE_PNG_VALUE,
                    baos.toByteArray()
            );
        } catch (IOException ioe) {
            throw new WhaleCheckedException("Failed to create test image");
        }

    }

    public static MockMultipartFile createTestNullImage(){
        return new MockMultipartFile(
                "image",
                "white_image.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[0]
        );
    }

}
