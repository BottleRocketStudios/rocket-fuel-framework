package com.bottlerocket.dataloader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ford.arnett on 12/6/16.
 */
public class ExcelLoader implements DataLoader {
    private final String fileName;

    public ExcelLoader(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Created with help from SO http://stackoverflow.com/questions/1516144/how-to-read-and-write-excel-file-in-java
     *
     */
    @Override
    public ArrayList<ArrayList<Object>> loadData() throws IOException {
        FileInputStream stream = new FileInputStream(fileName);
        XSSFWorkbook wb = new XSSFWorkbook(stream);
        XSSFSheet sheet = wb.getSheetAt(0);
        Row row;

        //Iterate through each rows one by one
        int rowIndex = 0;
        Iterator<Row> rowIterator = sheet.iterator();
        ArrayList<ArrayList<Object>> results = new ArrayList<>();
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            //For each row, iterate through all the columns
            Iterator<Cell> cellIterator = row.cellIterator();

            results.add(rowIndex, new ArrayList<>());
            while (cellIterator.hasNext()) {
                (results.get(rowIndex)).add(cellIterator.next());
            }
            rowIndex++;
        }
        stream.close();

        return results;
    }

}
