package com.whale.web.utils;

import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipServiceUtilTest {
    public static MockMultipartFile createTestZipFile() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(baos)) {

            ZipEntry entry = new ZipEntry("test.txt");
            zipOut.putNextEntry(entry);

            byte[] fileContent = "This is a test file content.".getBytes();
            zipOut.write(fileContent, 0, fileContent.length);

            zipOut.closeEntry();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            return new MockMultipartFile("files", "zip-test.zip", "application/zip", bais);
        }
    }


}
