package com.whale.web.design.altercolor;

import com.whale.web.utils.ImageService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class AlterColorTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void shouldReturnAValidPNGProcessedImage() throws Exception {
        MockMultipartFile file = ImageService.createTestImage("png", "image");
        mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:8080/api/v1/design/alter-color")
                        .file(file)
                        .param("colorForAlteration", "#FFFFFF")
                        .param("colorOfImage", "#000000")
                        .param("margin", "4.0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", Matchers.containsString("attachment; filename=ModifiedImage.png")))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    @Order(2)
    void shouldReturnAInternalServerError() throws Exception {
        MockMultipartFile file = ImageService.createTestNullImage();
        mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:8080/api/v1/design/alter-color")
                        .file(file)
                        .param("colorForAlteration", "#FF0000")
                        .param("colorOfImage", "#00FF00")
                        .param("margin", "4.0"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

    }

    @Test
    @Order(3)
    void testAlterColorWithoutColor() throws Exception {
        MockMultipartFile file = ImageService.createTestImage("png", "image");
        mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:8080/api/v1/design/alter-color")
                        .file(file)
                        .param("colorForAlteration", "#FF0000")
                        .param("colorOfImage", "")
                        .param("margin", "4.0"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

}
