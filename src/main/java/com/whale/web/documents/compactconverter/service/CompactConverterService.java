package com.whale.web.documents.compactconverter.service;

import java.io.*;
import java.util.*;

import com.whale.web.documents.compactconverter.model.CompactConverterModel;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class CompactConverterService {

    @Autowired
    ConvertToZipService convertToZip;

    @Autowired
    ConvertToTarService convertToTar;

    @Autowired
    ConverterTo7zService converterTo7z;

    @Autowired
    ConvertToTarGzService convertToTarGz;


    public List<byte[]> converterFile(List<MultipartFile> files, CompactConverterModel form) throws IOException {

        if (files == null || files.isEmpty() || !areAllFilesZip(files)) {
            throw new IllegalArgumentException("The input is not a valid zip file");
        } else {
            return switch (form.getAction()) {
                case ".zip" -> convertToZip.convertToZip(files);
                case ".tar.gz" -> convertToTarGz.convertToTarGz(files);
                case ".7z" -> converterTo7z.convertTo7z(files);
                case ".tar" -> convertToTar.convertToTar(files);
                default -> throw new IllegalArgumentException("Invalid compression format");
            };
        }
    }

    public static boolean areAllFilesZip(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (!isZipFileByFilename(file)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isZipFileByFilename(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {

            return originalFilename.toLowerCase().endsWith(".zip");
        }
        return false;
    }

}


