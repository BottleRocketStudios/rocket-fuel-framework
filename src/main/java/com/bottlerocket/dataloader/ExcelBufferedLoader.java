package com.bottlerocket.dataloader;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ExcelBufferedLoader implements DataLoader {
    private final String fileName;
    private final int ROW_CACHE_SIZE = 100; // number of rows to keep in memory
    private final int BUFFER_SIZE = 4096;   // buffer size in bytes to use when reading InputStream to file

    public ExcelBufferedLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public ArrayList<ArrayList<Object>> loadData() throws IOException {
        FileInputStream stream = new FileInputStream(fileName);

        XSSFWorkbook wb = (XSSFWorkbook) StreamingReader.builder()
                .rowCacheSize(ROW_CACHE_SIZE)
                .bufferSize(BUFFER_SIZE)
                .open(stream);

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
