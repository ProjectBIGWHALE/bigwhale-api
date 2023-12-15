package com.whale.web.design;

import com.whale.web.design.altercolor.model.AlterColorForm;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class DesignTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAValidPNGProcessedImage() throws Exception {

        AlterColorForm alterColorForm = new AlterColorForm(null, "#000000", "#FFFFFF", 4.0);
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 100, 100);
        graphics.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "white_image.png",
                MediaType.IMAGE_PNG_VALUE,
                imageBytes
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:8080/api/v1/design/alter-color")
                        .file(file)
                        .flashAttr("alterColorForm", alterColorForm)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", Matchers.containsString("attachment; filename=ModifiedImage.png")))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    void shouldReturnAExceptionStatusCode400() throws Exception {

        byte[] imageBytes = null;

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "white_image.png",
                MediaType.IMAGE_PNG_VALUE,
                imageBytes
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:8080/api/v1/design/alter-color")
                        .file(file)
                        .param("colorForAlteration", "#FF0000")
                        .param("colorOfImage", "")
                        .param("margin", "4.0"))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    void shouldReturnAPaletteColorsList() throws Exception {

        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 100, 100);
        graphics.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "white_image.png",
                MediaType.IMAGE_PNG_VALUE,
                imageBytes
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:8080/api/v1/design/colors-palette")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
