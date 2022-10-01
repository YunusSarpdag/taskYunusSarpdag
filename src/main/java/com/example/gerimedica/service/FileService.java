package com.example.gerimedica.service;

import com.example.gerimedica.model.File;
import com.example.gerimedica.model.FileModel;
import com.example.gerimedica.model.FileModelRepository;
import com.example.gerimedica.model.FileRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class FileService {


    /*
    Hello dear code reader ı write this code for url based document save and response
    ,client gives URL of file and ı am saving file after that same style ı response back to client a URL
     */

    @Autowired
    private FileModelRepository fileModelRepository;

    @Autowired FileRepository fileRepository;

    @PostMapping("/addFile")
    public void addFile(String filePath , String fileName)  {
        File file = new File();
        file.setName(fileName);
        List<FileModel> fileModelList = new ArrayList<>();
        try {

            // Create an object of filereader class
            // with CSV file as a parameter.
            FileReader filereader = new FileReader(filePath);

            // create csvReader object passing
            // filereader as parameter
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            String[] header = csvReader.readNext();
            validateHeader(header);
            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                FileModel fileModel = new FileModel();
                fileModel.setSource(nextRecord[0]);
                fileModel.setCodeListCode(nextRecord[1]);
                fileModel.setCode(nextRecord[2]);
                fileModel.setDisplayValue(nextRecord[3]);
                fileModel.setLongDescription(nextRecord[4]);
                fileModel.setFromDate(getDate(nextRecord[5]));
                fileModel.setToDate(getDate(nextRecord[6]));
                fileModel.setSortingPriority(nextRecord[7].isEmpty() ? null : Integer.parseInt(nextRecord[7]));
                fileModel.setFile(file);
                fileModelList.add(fileModel);
                System.out.println();
            }
    } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
        fileRepository.save(file);

        fileModelRepository.saveAll(fileModelList);

    }

    private void validateHeader(String[] arr){
        if(!arr[0].equals("source")){
            throw new RuntimeException("header error");
        }
        if(!arr[1].equals("codeListCode")){
            throw new RuntimeException("header error");
        }
        if(!arr[2].equals("code")){
            throw new RuntimeException("header error");
        }
        if(!arr[3].equals("displayValue")){
            throw new RuntimeException("header error");
        }
        if(!arr[4].equals("longDescription")){
            throw new RuntimeException("header error");
        }
        if(!arr[5].equals("fromDate")){
            throw new RuntimeException("header error");
        }
        if(!arr[6].equals("toDate")){
            throw new RuntimeException("header error");
        }
        if(!arr[7].equals("sortingPriority")){
            throw new RuntimeException("header error");
        }
    }

    private Date getDate(String strDate){
        if(strDate.isEmpty() || strDate == "" || strDate == null){
            return null;
        }

        Date date= null;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(strDate);
        } catch (ParseException e) {
            System.out.println("error on parse" + e.getMessage());
        }
        return date;
    }


    @GetMapping("/getFile{fileName}")
    public String getFile(@RequestParam("fileName") String fileName) throws IOException {
        File file = fileRepository.getById(fileName);
        List<FileModel> fileModelList = fileModelRepository.findByFile(file);
        List<String[]> csvData = getListData(fileModelList);

         // /Users/yunussarpdag/Desktop/zz/exerciseResponse.csv
        try (CSVWriter writer = new CSVWriter(new FileWriter("/Users/yunussarpdag/Desktop/zz/exerciseResponse.csv"))) {
            writer.writeAll(csvData);
        }

        return "/Users/yunussarpdag/Desktop/zz/exerciseResponse.csv";
    }

    private List<String[]> getListData(List<FileModel> fileModels){
        List<String[]> strings = new ArrayList<>();
        String[] entries = {"source","codeListCode"	,"code","displayValue",	"longDescription","fromDate","toDate","sortingPriority"};
        strings.add(entries);
        for (FileModel fileModel : fileModels){
            String[] arr = new String[8];
            arr[0] = fileModel.getSource();
            arr[1] = fileModel.getCodeListCode();
            arr[2] = fileModel.getCode();
            arr[3] = fileModel.getDisplayValue();
            arr[4] = fileModel.getLongDescription();
            arr[5] = getStrDate(fileModel.getFromDate());
            arr[6] = getStrDate(fileModel.getToDate());
            arr[7] = fileModel.getSortingPriority() == null ? "" : fileModel.getSortingPriority().toString();
            strings.add(arr);
        }
        return strings;
    }

    private String getStrDate(Date date){
        if(date == null || date == null){
            return "";
        }
        String pattern = "dd-MM-yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        String returnDay = df.format(date);
        return returnDay;
    }

}
