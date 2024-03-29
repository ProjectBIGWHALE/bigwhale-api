package com.whale.web.design.altercolor.service;

import com.whale.web.exceptions.domain.WhaleInvalidImageException;
import com.whale.web.exceptions.domain.WhaleCheckedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

@Service
public class AlterColorService {

    public byte[] alterColor(MultipartFile imageForm, String colorOfImage, String replacementColor, double marginValue) throws WhaleCheckedException, WhaleInvalidImageException {
        try {
            isValidImageFormat(imageForm);
            BufferedImage img = ImageIO.read(imageForm.getInputStream());

            ColorRange colorRange = calculateColorRange(colorOfImage, marginValue);
            Color newColor = getReplacementColor(replacementColor);
            BufferedImage newImg = applyColorTransformation(img, colorRange, newColor);

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                ImageIO.write(newImg, "png", bos);
                return bos.toByteArray();
            }
        }catch (WhaleInvalidImageException iie) {
            throw new WhaleInvalidImageException(iie.getMessage());
        }catch (IOException ioe) {
            throw new WhaleCheckedException("A failure occurred in the color change of the image: " + ioe.getMessage());
        }

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

    private void isValidImageFormat(MultipartFile imageFile) throws WhaleInvalidImageException {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new WhaleInvalidImageException("Image cannot be null or empty");
        }

        if (!Arrays.asList("bmp", "jpg", "jpeg", "gif", "png").contains(getFileExtension(imageFile))) {
            throw new WhaleInvalidImageException("Please choose a valid image format: bmp, jpg, jpeg, png or gif file.");
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

}
