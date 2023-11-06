package com.whale.web.documents.filecompressor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.Deflater;

@Service
public class FileCompressorService {

    public byte[] compressFiles(List<MultipartFile> multipartFiles) {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            throw new IllegalArgumentException("The input file list is null or empty.");
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {

            zipOut.setLevel(Deflater.BEST_COMPRESSION);

            for (MultipartFile multipartFile : multipartFiles) {
                ZipEntry zipEntry = new ZipEntry(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                zipOut.putNextEntry(zipEntry);

                byte[] buffer = new byte[1024];
                int length;
                InputStream fileInputStream = multipartFile.getInputStream();

                while ((length = fileInputStream.read(buffer)) > 0) {
                    zipOut.write(buffer, 0, length);
                }

                zipOut.closeEntry();
            }

            zipOut.finish();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error compressing the files.", e);
        }
    }
}
