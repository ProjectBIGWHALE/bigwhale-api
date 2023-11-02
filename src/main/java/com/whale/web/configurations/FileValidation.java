package com.whale.web.configurations;

import com.whale.web.documents.imageconverter.exception.InvalidFileFormatException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class FileValidation {

    public void validateAction(String action) {
        String[] allowedFormats = { ".zip", ".tar", ".tar.gz", ".7z" };
        if (!Arrays.asList(allowedFormats).contains(action)) {
            throw new InvalidFileFormatException("Unsupported file format. Please choose a ZIP, TAR, TAR.GZ or 7Z format.");
        }
    }

    public void validateInputFile(List<MultipartFile> files){
        for (MultipartFile file: files) {
            if (!isValidZipFile(file)) {
                throw new InvalidFileFormatException("The file is not a valid zip file.");
            }
        }
    }

    private boolean isValidZipFile(MultipartFile file) {

        return Objects.equals(file.getContentType(), "application/zip");
    }
}

