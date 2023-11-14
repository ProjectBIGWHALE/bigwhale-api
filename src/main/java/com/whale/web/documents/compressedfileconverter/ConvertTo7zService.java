package com.whale.web.documents.compressedfileconverter;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConvertTo7zService {
    public List<byte[]> convertTo7z(List<MultipartFile> files) throws IOException {
        List<byte[]> filesConverted = new ArrayList<>();

        for (MultipartFile file : files) {

            File tempFile = File.createTempFile("temp", ".7z");
            tempFile.deleteOnExit();
            try (SevenZOutputFile sevenZOutputFile = new SevenZOutputFile(tempFile)) {
                try (InputStream zipInputStream = file.getInputStream();
                     ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(zipInputStream)) {

                    ZipArchiveEntry zipEntry;
                    while ((zipEntry = zipArchiveInputStream.getNextZipEntry()) != null) {
                        SevenZArchiveEntry sevenZEntry = sevenZOutputFile.createArchiveEntry(new File(zipEntry.getName()), zipEntry.getName());
                        sevenZOutputFile.putArchiveEntry(sevenZEntry);

                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = zipArchiveInputStream.read(buffer)) != -1) {
                            sevenZOutputFile.write(buffer, 0, bytesRead);
                        }
                        sevenZOutputFile.closeArchiveEntry();
                    }
                }
            }

            byte[] sevenZBytes = Files.readAllBytes(tempFile.toPath());
            filesConverted.add(sevenZBytes);
        }
        return filesConverted;
    }
}
