package com.whale.web.documents.certificategenerator.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.whale.web.documents.certificategenerator.dto.WorksheetRecordDto;
import com.whale.web.documents.certificategenerator.model.Worksheet;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessWorksheetService {

    public List<String> savingNamesInAList(MultipartFile csvFileDto) throws IOException {

        if (csvFileDto.isEmpty()) {
            throw new IOException();
        }

        var worksheetDto = new WorksheetRecordDto(csvFileDto);
        var worksheet = new Worksheet();
        BeanUtils.copyProperties(worksheetDto, worksheet);

        List<String> names = new ArrayList<>();

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(worksheet.getCsvFile().getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> lines = reader.readAll();
            for (String[] line : lines) {
                if (line.length > 0) {
                    names.add(line[0]);
                }
            }
        } catch (CsvException e) {
            throw new IllegalArgumentException("Unable to read sent file");
        }

        return names;
    }
}
