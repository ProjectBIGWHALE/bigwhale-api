package com.whale.web.documents.zipfileconverter.service;

import com.whale.web.exceptions.domain.WhaleRunTimeException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConvertToTarService {
    public List<byte[]> convertToTar(List<MultipartFile> files) {
        List<byte[]> filesConverted = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                TarArchiveOutputStream tarOutputStream = new TarArchiveOutputStream(outputStream);
                InputStream zipInputStream = file.getInputStream();
                ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(zipInputStream);
                ZipArchiveEntry zipEntry;
                while ((zipEntry = zipArchiveInputStream.getNextZipEntry()) != null) {
                    TarArchiveEntry tarEntry = new TarArchiveEntry(zipEntry.getName());
                    tarEntry.setSize(zipEntry.getSize());
                    tarOutputStream.putArchiveEntry(tarEntry);
                    IOUtils.copy(zipArchiveInputStream, tarOutputStream);
                    tarOutputStream.closeArchiveEntry();
                }
                filesConverted.add(outputStream.toByteArray());
            }
        } catch (IOException e) {
            throw new WhaleRunTimeException(e.getMessage());
        }
        return filesConverted;
    }
}
