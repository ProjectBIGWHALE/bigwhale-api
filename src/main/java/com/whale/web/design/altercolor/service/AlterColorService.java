package com.whale.web.design.altercolor.service;

import com.whale.web.utils.UploadImage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class AlterColorService {

    private final UploadImage uploadImage;

    public AlterColorService(UploadImage uploadImage) {
        this.uploadImage = uploadImage;
    }

    public byte[] alterColor(MultipartFile imageForm, String colorOfImage, String replacementColor, double marginValue) throws IOException {

        MultipartFile upload = uploadImage.uploadImage(imageForm);
        BufferedImage img = ImageIO.read(upload.getInputStream());

        // Calculate color range
        ColorRange colorRange = calculateColorRange(colorOfImage, marginValue);

        // Get the replacement color
        Color newColor = getReplacementColor(replacementColor);

        // Apply color transformation
        BufferedImage newImg = applyColorTransformation(img, colorRange, newColor);

        // Convert BufferedImage to byte array
        return convertImageToByteArray(newImg);
    }

    private ColorRange calculateColorRange(String colorOfImage, double marginValue) {
        Color markedColor = Color.decode(colorOfImage);
        int intensity = (markedColor.getRed() + markedColor.getGreen() + markedColor.getBlue()) / 3;
        int delta = (int) Math.round(255 * (marginValue / 100.0));
        return new ColorRange(markedColor, intensity, delta);
    }

    private Color getReplacementColor(String replacementColor) {
        if (replacementColor == null || replacementColor.isEmpty()) {
            return new Color(0, 0, 0, 0);
        } else {
            Color color = Color.decode(replacementColor);
            return new Color(color.getRed(), color.getGreen(), color.getBlue());
        }
    }

    private BufferedImage applyColorTransformation(BufferedImage img, ColorRange colorRange, Color newColor) {
        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color pixelColor = new Color(img.getRGB(x, y));
                if (colorRange.isWithinRange(pixelColor)) {
                    newImg.setRGB(x, y, newColor.getRGB());
                } else {
                    newImg.setRGB(x, y, img.getRGB(x, y));
                }
            }
        }

        return newImg;
    }

    private byte[] convertImageToByteArray(BufferedImage img) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", bos);
            return bos.toByteArray();
        }
    }

    private static class ColorRange {
        private final int rMin, rMax, gMin, gMax, bMin, bMax;

        public ColorRange(Color color, int intensity, int delta) {
            this.rMin = Math.max(0, color.getRed() - delta);
            this.rMax = Math.min(255, color.getRed() + delta);
            this.gMin = Math.max(0, color.getGreen() - delta);
            this.gMax = Math.min(255, color.getGreen() + delta);
            this.bMin = Math.max(0, color.getBlue() - delta);
            this.bMax = Math.min(255, color.getBlue() + delta);
        }

        public boolean isWithinRange(Color color) {
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            return r >= rMin && r <= rMax &&
                   g >= gMin && g <= gMax &&
                   b >= bMin && b <= bMax;
        }
    }
}
