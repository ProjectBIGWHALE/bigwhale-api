package com.whale.web.documents.compactconverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class CompactConverterService {

    @Autowired
    ConvertToZipService convertToZip;

    @Autowired
    ConvertToTarService convertToTar;

    @Autowired
    ConvertTo7zService converterTo7z;

    @Autowired
    ConvertToTarGzService convertToTarGz;


    public List<byte[]> converterFile(List<MultipartFile> files, String outputFormat) throws IOException {

        if (files == null || files.isEmpty() || !areAllFilesZip(files)) {
            throw new IllegalArgumentException("The input is not a valid zip file");
        } else {
            return switch (outputFormat.toLowerCase()) {
                case "zip" -> convertToZip.convertToZip(files);
                case "tar.gz" -> convertToTarGz.convertToTarGz(files);
                case "7z" -> converterTo7z.convertTo7z(files);
                case "tar" -> convertToTar.convertToTar(files);
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


