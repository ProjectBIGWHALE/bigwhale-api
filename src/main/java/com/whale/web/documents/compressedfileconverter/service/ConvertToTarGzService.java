package com.whale.web.documents.compressedfileconverter.service;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConvertToTarGzService {
    public List<byte[]> convertToTarGz(List<MultipartFile> files) throws IOException {
        List<byte[]> filesConverted = new ArrayList<>();

        for (MultipartFile file : files) {

            ByteArrayOutputStream tarGzOutputStream = new ByteArrayOutputStream();

            try (TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(tarGzOutputStream)) {
                InputStream zipInputStream = file.getInputStream();
                ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(zipInputStream);

                ZipArchiveEntry zipEntry;
                while ((zipEntry = zipArchiveInputStream.getNextZipEntry()) != null) {
                    TarArchiveEntry tarEntry = new TarArchiveEntry(zipEntry.getName());
                    tarEntry.setSize(zipEntry.getSize());
                    tarArchiveOutputStream.putArchiveEntry(tarEntry);
                    IOUtils.copy(zipArchiveInputStream, tarArchiveOutputStream);
                    tarArchiveOutputStream.closeArchiveEntry();
                }
            }

            try (GzipCompressorOutputStream gzipOutputStream = new GzipCompressorOutputStream(tarGzOutputStream)) {
                gzipOutputStream.write(tarGzOutputStream.toByteArray());
            }
            filesConverted.add(tarGzOutputStream.toByteArray());
        }
        return filesConverted;
    }
}
