package com.whale.web.documents.compressedfileconverter;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class ConvertToZipService {
    public List<byte[]> convertToZip(List<MultipartFile> files) throws IOException {
        List<byte[]> zipFiles = new ArrayList<>();

        for (MultipartFile file : files) {

            ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
            ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(zipOutputStream);
            ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(file.getInputStream());

            ZipArchiveEntry zipEntry;
            while ((zipEntry = zipArchiveInputStream.getNextZipEntry()) != null) {

                ZipArchiveEntry newZipEntry = new ZipArchiveEntry(zipEntry.getName());
                zipArchiveOutputStream.putArchiveEntry(newZipEntry);
                IOUtils.copy(zipArchiveInputStream, zipArchiveOutputStream);
                zipArchiveOutputStream.closeArchiveEntry();
            }

            zipArchiveInputStream.close();
            zipArchiveOutputStream.close();
            zipFiles.add(zipOutputStream.toByteArray());


        }
        return zipFiles;
    }
}
