package com.whale.web.documents.certificategenerator.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.whale.web.exceptions.domain.WhaleCheckedException;
import com.whale.web.exceptions.domain.WhaleRunTimeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProcessWorksheetService {

    public List<String> savingNamesInAList(MultipartFile csvFile) throws WhaleCheckedException {
        if(!Objects.requireNonNull(csvFile.getOriginalFilename()).endsWith(".csv")) {
            throw new WhaleRunTimeException("The file with of names cannot be different as csv.");
        }
        List<String> names = new ArrayList<>();
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> lines = reader.readAll();
            for (String[] line : lines) {
                if (line.length > 0) {
                    names.add(line[0]);
                }
            }
        } catch (IOException | CsvException e) {
            throw new WhaleCheckedException(e.getMessage());
        }
        return names;
    }
}