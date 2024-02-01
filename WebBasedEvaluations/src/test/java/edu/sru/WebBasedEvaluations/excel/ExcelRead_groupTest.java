package edu.sru.WebBasedEvaluations.excel;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelRead_groupTest {
    @BeforeAll
    public static void setup() throws Exception {
    	//Calls function to build GroupsTestData.xlsx and User TestData.xlsx
        String relativePath = "Program Documents/RawData.xlsx";
        String filePath = System.getProperty("user.dir") + "/" + relativePath;
    	FileInputStream fileraw = new FileInputStream(filePath);
        XSSFWorkbook rawwb = new XSSFWorkbook(fileraw);

        ExcelRead_group.convertFileReal(rawwb);

    }

	
    @Test
    public void testFieldUserAdd() throws Exception {

        FileInputStream filenew = new FileInputStream("UserTestData.xlsx");
        XSSFWorkbook newwb = new XSSFWorkbook(filenew);
        System.out.println("file exists");

        String jobtitleex = newwb.getSheetAt(0).getRow(2).getCell(8).getStringCellValue();
        jobtitleex = jobtitleex.strip();


        assertEquals("FOREMAN", jobtitleex);
    }

    @Test
    public void testEvalUserAdd() throws Exception {

        FileInputStream filenew = new FileInputStream("UserTestData.xlsx");
        XSSFWorkbook newwb = new XSSFWorkbook(filenew);

        String evalemailex = newwb.getSheetAt(0).getRow(95).getCell(3).getStringCellValue();
        evalemailex = evalemailex.strip();

        assertEquals("Janoscod@Burns-Scalo.Com", evalemailex);
    }


    @Test
    public void testGroupReviewees() throws Exception {

        FileInputStream filenew = new FileInputStream("GroupsTestData.xlsx");
        XSSFWorkbook newwb = new XSSFWorkbook(filenew);
        System.out.println("file exists");

        String revieweenameex = newwb.getSheetAt(0).getRow(11).getCell(4).getStringCellValue();
        revieweenameex = revieweenameex.strip();

        assertEquals("DEMETRIUS WALDON", revieweenameex);
    }

}
