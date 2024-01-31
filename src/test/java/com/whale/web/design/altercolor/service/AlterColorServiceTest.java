package com.whale.web.design.altercolor.service;

import com.whale.web.exceptions.domain.WhaleCheckedException;
import com.whale.web.utils.ImageServiceUtilTest;
import com.whale.web.utils.UploadImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlterColorServiceTest {

    @Mock
    private UploadImage uploadImage;

    @Autowired
    @InjectMocks
    AlterColorService alterColorService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAlterColorResultAndLength() throws IOException, WhaleCheckedException {
        String colorOfImage = "#FF0000";
        String replacementColor = "#00FF00";
        double marginValue = 0.1;
        MultipartFile imageForm = ImageServiceUtilTest.createTestImage("png", "image");

        when(uploadImage.uploadImage(any())).thenReturn(imageForm);
        byte[] result = alterColorService.alterColor(imageForm, colorOfImage, replacementColor, marginValue);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }





}