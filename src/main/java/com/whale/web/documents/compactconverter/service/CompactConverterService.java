package com.whale.web.documents.compactconverter.service;

import com.whale.web.exceptions.domain.FileIsNullException;
import com.whale.web.exceptions.domain.WhaleRunTimeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CompactConverterService {
    private final ConvertToZipService convertToZip;
    private final ConvertToTarService convertToTar;
    private final ConvertTo7zService converterTo7z;
    private final ConvertToTarGzService convertToTarGz;

    public CompactConverterService(ConvertToZipService convertToZip, ConvertToTarService convertToTar,
            ConvertTo7zService converterTo7z, ConvertToTarGzService convertToTarGz) {
        this.convertToZip = convertToZip;
        this.convertToTar = convertToTar;
        this.converterTo7z = converterTo7z;
        this.convertToTarGz = convertToTarGz;
    }

    public List<byte[]> converterFile(List<MultipartFile> files, String outputFormat) {
        if (!areAllFilesZip(files)) {
            throw new WhaleRunTimeException("File is null or not a valid zip file");
        } else {
            return switch (outputFormat.toLowerCase()) {
                case "zip" -> convertToZip.convertToZip(files);
                case "tar.gz" -> convertToTarGz.convertToTarGz(files);
                case "7z" -> converterTo7z.convertTo7z(files);
                case "tar" -> convertToTar.convertToTar(files);
                default -> throw new WhaleRunTimeException("Input is null or the format entered is invalid");
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

