package com.whale.web.documents.certificategenerator.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.whale.web.documents.certificategenerator.dto.WorksheetRecordDto;
import com.whale.web.documents.certificategenerator.model.Worksheet;
import com.whale.web.exceptions.domain.WhaleRunTimeException;
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

    public List<String> savingNamesInAList(MultipartFile csvFileDto){

        if (csvFileDto.isEmpty()) {
            throw new WhaleRunTimeException("csvFileDto cannot be null.");
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
        } catch (IOException | CsvException e) {
            throw new WhaleRunTimeException(e.getMessage());
        }

        return names;
    }
}
