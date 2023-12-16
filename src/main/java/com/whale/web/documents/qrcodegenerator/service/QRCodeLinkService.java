package com.whale.web.documents.qrcodegenerator.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.whale.web.documents.qrcodegenerator.model.QRCodeLinkModel;
import com.whale.web.exceptions.domain.WhaleRunTimeException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

@Service
public class QRCodeLinkService {

    public byte[] generateQRCode(QRCodeLinkModel qrCodeLinkRecordModel) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeLinkRecordModel.getLink(), BarcodeFormat.QR_CODE, 350, 350, hints);
            BufferedImage qrCodeImage = new BufferedImage(350, 350, BufferedImage.TYPE_INT_RGB);
            qrCodeImage.createGraphics();

            int pixelColorValue = convertColor(qrCodeLinkRecordModel.getPixelColor());

            for (int x = 0; x < 350; x++) {
                for (int y = 0; y < 350; y++) {
                    qrCodeImage.setRGB(x, y, bitMatrix.get(x, y) ? pixelColorValue : 0xFFFFFFFF);
                }
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImage, "png", bos);

            bos.flush();
            byte[] imageBytes = bos.toByteArray();
            bos.close();

            return imageBytes;
        } catch (WriterException | IOException e) {
            throw new WhaleRunTimeException("An error occurred when trying to generate the qrcode: "+e.getMessage());
        }
    }


    private static int convertColor(String colorString) {
        // Checks if it is a hexadecimal representation
        if (colorString.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            Color color = Color.decode(colorString);
            return color.getRGB();
        }

        // Checks if it is an RGB representation
        if (colorString.matches("^rgb\\(\\d{1,3},\\s*\\d{1,3},\\s*\\d{1,3}\\)$")) {
            String[] components = colorString.substring(4, colorString.length() - 1).split(",");
            if (components.length == 3) {
                int r = Integer.parseInt(components[0].trim());
                int g = Integer.parseInt(components[1].trim());
                int b = Integer.parseInt(components[2].trim());
                return new Color(r, g, b).getRGB();
            }
        }

        //Checks if it is a color name representation
        Color colorByName = getColorByName(colorString);
        return colorByName.getRGB();
    }


    private static Color getColorByName(String name) {
        name = name.toLowerCase();
        return switch (name) {
            case "red" -> Color.RED;
            case "blue" -> Color.BLUE;
            case "green" -> Color.GREEN;
            case "yellow" -> Color.YELLOW;
            case "orange" -> Color.ORANGE;
            case "pink" -> Color.PINK;
            case "cyan" -> Color.CYAN;
            case "magenta" -> Color.MAGENTA;
            case "dark_gray" -> Color.DARK_GRAY;
            case "light_gray" -> Color.LIGHT_GRAY;
            default -> Color.BLACK;
        };
    }
}
