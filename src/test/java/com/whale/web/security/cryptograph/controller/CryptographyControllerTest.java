package com.whale.web.security.cryptograph.controller;

import java.net.URI;

import com.whale.web.security.cryptograph.model.EncryptModel;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.whale.web.security.cryptograph.service.EncryptService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class CryptographyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    EncryptService encryptService;

    @Test
    @Order(1)
    void shouldReturnEncryptedFile() throws Exception {
        URI uri = new URI("http://localhost:8080/api/v1/security/cryptograph");
        boolean action = true;
        String key = "TEST";

        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                        .file(file)
                        .param("key", key)
                        .param("action", String.valueOf(action)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", Matchers.containsString("attachment")))
                .andExpect(result -> {
                    byte[] encryptedContent = result.getResponse().getContentAsByteArray();
                    EncryptModel expectedEncryptedContent = encryptService.choiceEncryptService(action, key, file);
                    Assertions.assertArrayEquals(expectedEncryptedContent.getFile(), encryptedContent);
                });
    }

    @Test
    @Order(2)
    void shouldReturnDecryptedFile() throws Exception {
        URI uri = new URI("http://localhost:8080/api/v1/security/cryptograph");
        boolean action = false;
        String key = "TEST";

        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());

        EncryptModel encryptedContent = encryptService.choiceEncryptService(true, key, file);
        MockMultipartFile encryptedFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, encryptedContent.getFile());

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                        .file(encryptedFile)
                        .param("key", key)
                        .param("action", String.valueOf(action)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", Matchers.containsString("attachment")))
                .andExpect(result -> {
                    byte[] decryptedContent = result.getResponse().getContentAsByteArray();
                    Assertions.assertArrayEquals(file.getBytes(), decryptedContent);
                });
    }

    @Test
    @Order(3)
    void shouldReturnStatus401ForWrongKey() throws Exception {
        URI uri = new URI("http://localhost:8080/api/v1/security/cryptograph");
        boolean action = false;
        String key = "TEST";

        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());

        EncryptModel encryptedContent = encryptService.choiceEncryptService(true, key, file);
        MockMultipartFile encryptedFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, encryptedContent.getFile());

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                        .file(encryptedFile)
                        .param("key", "WRONG_KEY")
                        .param("action", String.valueOf(action)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
