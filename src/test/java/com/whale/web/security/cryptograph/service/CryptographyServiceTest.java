package com.whale.web.security.cryptograph.service;

import com.whale.web.exceptions.domain.*;
import com.whale.web.security.cryptograph.model.EncryptModel;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CryptographyServiceTest {

    @Autowired
    @InjectMocks
    private EncryptService encryptService;

    @Mock
    private EncryptModel encryptModel;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Order(1)
    void testChoiceEncryptServiceWithValidFileAndKey() throws WhaleInvalidFileException, WhaleCheckedException {
        Boolean action = true;
        String key = "mySecretKey";
        MockMultipartFile file = new MockMultipartFile("file",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes());

        encryptModel = encryptService.choiceEncryptService(action, key, file);
        assertNotNull(encryptModel);
        assertEquals("test.txt.encrypted", encryptModel.getFileName());
    }

    @Test
    @Order(2)
    void testChoiceEncryptServiceWithInvalidFile() {
        Boolean action = true;
        String key = "mySecretKey";
        MockMultipartFile emptyFile = new MockMultipartFile("file",
                "test.txt",
                "text/plain",
                new byte[0]);

        WhaleInvalidFileException exception = assertThrows(WhaleInvalidFileException.class, () -> {
            encryptService.choiceEncryptService(action, key, emptyFile);
        });
        assertEquals("An invalid file was sent", exception.getMessage());
    }

    @Test
    @Order(3)
    void testChoiceEncryptServiceWithInvalidKey() {
        Boolean action = true;
        String key = "";
        MockMultipartFile emptyFile = new MockMultipartFile("file",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes());

        WhaleRunTimeException exception = assertThrows(WhaleRunTimeException.class, () -> {
            encryptService.choiceEncryptService(action, key, emptyFile);
        });
        assertEquals("The key field is blank", exception.getMessage());
    }

}
