package com.whale.web.design.altercolor.service;

import com.whale.web.exceptions.domain.ImageIsNullException;
import com.whale.web.utils.UploadImage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
<<<<<<< HEAD

/*
 * Class to read the pixels of an image and replace the pixels that are in
 * a certain color spectrum. It is possible to change the edge of the spectrum and change it by a color
 * any other than transparency
 */
=======
>>>>>>> eecfbb41c00a9f942f1480b26fe210a8d2609a4f

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

<<<<<<< HEAD
        int r = markedColor.getRed();
        int g = markedColor.getGreen();
        int b = markedColor.getBlue();
        double porcentagemMargin = marginValue / 100;
        int intensity = (r + g + b) / 3; // Intensidade média da cor original
        int margin = (int) (intensity * porcentagemMargin); // 1% da intensidade como margem (ajuste conforme necessário)

        // Sets the top and bottom margin for each RGB component
        int delta = (int) Math.round(255 * (margin / 100.0)); // Margin percentage

        int newRmin = Math.max(0, r - delta); // Lower limit for the R component
        int newRmax = Math.min(255, r + delta); // Lower limit for the R component
        int newGmin = Math.max(0, g - delta); // Lower limit for the G component
        int newGmax = Math.min(255, g + delta); // Upper limit for the G component
        int newBmin = Math.max(0, b - delta); // Lower limit for the B component
        int newBmax = Math.min(255, b + delta); // Upper limit for the B component

        // Sets current color and new color
        Color newColor;
=======
    private Color getReplacementColor(String replacementColor) {
>>>>>>> eecfbb41c00a9f942f1480b26fe210a8d2609a4f
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
