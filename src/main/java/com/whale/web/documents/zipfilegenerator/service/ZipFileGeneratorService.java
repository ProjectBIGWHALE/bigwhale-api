package com.whale.web.documents.zipfilegenerator.service;

import com.whale.web.exceptions.domain.WhaleIOException;
import com.whale.web.exceptions.domain.WhaleInvalidFileException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipFileGeneratorService {

    public byte[] compressFiles(List<MultipartFile> files) throws WhaleInvalidFileException, WhaleIOException {

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream);
            zipOut.setLevel(Deflater.BEST_COMPRESSION);

            for (MultipartFile file : files) {
                if (file.isEmpty()) throw new WhaleInvalidFileException("One or more uploaded files are invalid");
                ZipEntry zipEntry = new ZipEntry(Objects.requireNonNull(file.getOriginalFilename()));
                zipOut.putNextEntry(zipEntry);

                byte[] buffer = new byte[1024];
                int length;
                InputStream fileInputStream = file.getInputStream();

                while ((length = fileInputStream.read(buffer)) > 0) {
                    zipOut.write(buffer, 0, length);
                }

                zipOut.closeEntry();
                fileInputStream.close();
            }
            zipOut.finish();
            return byteArrayOutputStream.toByteArray();

        } catch (WhaleInvalidFileException e) {
            throw new WhaleInvalidFileException(e.getMessage());
        } catch (IOException e) {
            throw new WhaleIOException(e.getMessage());
        }


    }

}
