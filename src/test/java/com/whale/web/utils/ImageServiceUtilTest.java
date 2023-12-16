package com.whale.web.utils;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageServiceUtilTest {
    public static MockMultipartFile createTestImage(String inputFormat, String name) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 100, 100);
        graphics.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, inputFormat, baos);
        baos.toByteArray();

        return new MockMultipartFile(
                name,
                "test-image." + inputFormat,
                "image/" + inputFormat,
                baos.toByteArray()
        );
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
