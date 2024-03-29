package com.whale.web.documents.zipfileconverter.service;

import com.whale.web.exceptions.domain.WhaleRunTimeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ZipFileConverterService {
    private final ConvertToZipService convertToZip;
    private final ConvertToTarService convertToTar;
    private final ConvertTo7zService converterTo7z;
    private final ConvertToTarGzService convertToTarGz;

    public ZipFileConverterService(ConvertToZipService convertToZip, ConvertToTarService convertToTar,
                                   ConvertTo7zService converterTo7z, ConvertToTarGzService convertToTarGz) {
        this.convertToZip = convertToZip;
        this.convertToTar = convertToTar;
        this.converterTo7z = converterTo7z;
        this.convertToTarGz = convertToTarGz;
    }

    public List<byte[]> converterFile(List<MultipartFile> files, String outputFormat) throws WhaleRunTimeException {
        if (!areAllFilesZip(files)) {
            throw new WhaleRunTimeException("Upload file not a valid zip file");
        } else {
            return switch (outputFormat.toLowerCase()) {
                case "zip" -> convertToZip.convertToZip(files);
                case "tar.gz" -> convertToTarGz.convertToTarGz(files);
                case "7z" -> converterTo7z.convertTo7z(files);
                case "tar" -> convertToTar.convertToTar(files);
                default -> throw new WhaleRunTimeException("Invalid format for conversion. Supported formats: 'zip', 'tar.gz', '7z' or 'tar'");
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
        if (originalFilename != null && !file.isEmpty()) {
            return originalFilename.toLowerCase().endsWith(".zip");
        }
        return false;
    }
}
