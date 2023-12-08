package com.whale.web.security.cryptograph;

import java.net.URI;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
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

import com.whale.web.security.cryptograph.service.EncryptService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class CryptographTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    EncryptService encryptService;

    @Test
    void shouldReturnEncryptedFile() throws Exception {
        URI uri = new URI("http://localhost:8080/api/v1/security/cryptograph");

        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                        .file(file)
                        .param("key", "TEST")
                        .param("action", String.valueOf(true)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", Matchers.containsString("attachment")))
                .andExpect(result -> {
                    byte[] encryptedContent = result.getResponse().getContentAsByteArray();
                    byte[] expectedEncryptedContent = encryptService.encryptFile(file, "TEST");
                    Assertions.assertArrayEquals(expectedEncryptedContent, encryptedContent);
                });
    }

    @Test
    void shouldReturnDecryptedFile() throws Exception {
        URI uri = new URI("http://localhost:8080/api/v1/security/cryptograph");

        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());

        byte[] encryptedContent = encryptService.encryptFile(file, "TEST");
        MockMultipartFile encryptedFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, encryptedContent);

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                        .file(encryptedFile)
                        .param("key", "TEST")
                        .param("action", String.valueOf(false)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", Matchers.containsString("attachment")))
                .andExpect(result -> {
                    byte[] decryptedContent = result.getResponse().getContentAsByteArray();
                    byte[] expectedDecryptedContent = encryptService.decryptFile(encryptedFile, "TEST");
                    Assertions.assertArrayEquals(expectedDecryptedContent, decryptedContent);
                });
    }

    @Test
    void shouldReturnStatus500ForWrongKey() throws Exception {
        URI uri = new URI("http://localhost:8080/api/v1/security/cryptograph");

        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());

        byte[] encryptedContent = encryptService.encryptFile(file, "TEST");
        MockMultipartFile encryptedFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, encryptedContent);

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                        .file(encryptedFile)
                        .param("key", "WRONG_KEY")
                        .param("action", String.valueOf(false)))
                .andExpect(MockMvcResultMatchers.status().is(500));

    }
}
