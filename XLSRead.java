package ipd.administration.advancedSearch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ipd.administration.advancedSearch.ExcelData;
import ipd.Base;

public class XLSRead {
    String path = "/src/test/java/ipd/administration/advancedSearch/search.properties";
    private List<ExcelData> data = new ArrayList<ExcelData>();
    private FileInputStream fis = null;
    private FileOutputStream fileOut = null;
    private static XSSFWorkbook workbook = null;
    private static XSSFSheet sheet = null;
    private static XSSFRow row, rown = null;
    private static XSSFCell cell = null;
    HashMap<Integer, ExcelData> mp = new HashMap<Integer, ExcelData>();
    Map<String, Integer> colmap = new HashMap<String, Integer>();
    private List<String> taxonomyTags = null;
    DataFormatter obj = new DataFormatter();

    public void setExcelFile(String Path, String SheetName) throws Exception {

        try {
            FileInputStream ExcelFile = new FileInputStream(Path);

            workbook = new XSSFWorkbook(ExcelFile);
            sheet = workbook.getSheet(SheetName);

        } catch (Exception e) {
            throw (e);
        }

        // pull no of rows in sheet and column, get column index in map
        int rowno = sheet.getLastRowNum();
        row = sheet.getRow(0);
        int colnoL = row.getLastCellNum();
        int colnoF = row.getFirstCellNum();

        for (int colix = colnoF; colix < colnoL; colix++) {
            cell = row.getCell(colix);
            colmap.put(cell.getStringCellValue(), cell.getColumnIndex());

        }

        // pull Patent no, Abstract text, Claim text and tags for Assignee
        // ABBOTT and store in list
        for (int i = 5; i < rowno; i++) {
            taxonomyTags = new ArrayList<String>();
            row = sheet.getRow(i);
            String value = row.getCell(colmap.get(ExcelData.assignees))
                    .getStringCellValue();
            if (value.equalsIgnoreCase("ABBOTT")) {
                String patentno = row.getCell(colmap.get(ExcelData.patent_no))
                        .getStringCellValue();
                String abstarct = row.getCell(colmap.get(ExcelData.abstract_F))
                        .getStringCellValue();
                String claims = row.getCell(colmap.get(ExcelData.claims))
                        .getStringCellValue();
                for (int j = 7; j < 36; j++) {
                    String tag = obj
                            .formatCellValue(sheet.getRow(i).getCell(j));
                    if (tag != "") {
                        taxonomyTags.add(tag);
                    }

                }

                // List taxonomyTags=
                ExcelData allData = new ExcelData(patentno, abstarct, claims,
                        taxonomyTags);
                data.add(allData);
            }
        }

    }

    public List<ExcelData> getData() {
        return data;
    }

}
