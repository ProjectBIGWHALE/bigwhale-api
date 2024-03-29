package com.whale.web.design.colorspalette.service;

import com.whale.web.exceptions.domain.WhaleInvalidImageException;
import com.whale.web.exceptions.domain.WhaleIOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.*;
import java.util.List;

@Service
public class CreateColorsPaletteService {

    private static final int NUM_COLORS = 6; // Number of predominant colors to be extracted
    private static final int MAX_COLOR_DISTANCE = 70; // Maximum allowed distance between colors

    public List<Color> createColorPalette(MultipartFile multipartFile) throws WhaleInvalidImageException, WhaleIOException {
        isValidImageFormat(multipartFile);
        try {
            BufferedImage image = ImageIO.read(multipartFile.getInputStream());
            int width = image.getWidth();
            int height = image.getHeight();

            Map<Integer, Integer> colorCount = new HashMap<>();

            // Iterate through all pixels of the image
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int alpha = (rgb >> 24) & 0xFF;

                    // Consider only fully opaque pixels
                    if (alpha == 255) {
                        int pixel = rgb & 0xFFFFFF;

                        // Check distance to existing colors in the palette
                        boolean isDuplicate = colorCount.keySet().stream()
                                .anyMatch(existingColor -> getColorDistance(existingColor, pixel) <= MAX_COLOR_DISTANCE);

                        if (!isDuplicate) {
                            // Increment the color count
                            colorCount.merge(pixel, 1, Integer::sum);
                        }
                    }
                }
            }

            // Sort the colors by count in descending order
            return colorCount.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                    .limit(NUM_COLORS)
                    .map(entry -> new Color(entry.getKey(), true))
                    .toList();

        } catch (NullPointerException e) {
            throw new WhaleInvalidImageException("This file is not a valid image");
        } catch (IOException ioe) {
            throw new WhaleIOException("Failed to created colors pallete from image");
        }
    }

    private double getColorDistance(int color1, int color2) {
        int rDiff = (color1 >> 16 & 0xFF) - (color2 >> 16 & 0xFF);
        int gDiff = (color1 >> 8 & 0xFF) - (color2 >> 8 & 0xFF);
        int bDiff = (color1 & 0xFF) - (color2 & 0xFF);
        return Math.sqrt((rDiff * rDiff) + (gDiff * gDiff) + (bDiff * bDiff));
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
