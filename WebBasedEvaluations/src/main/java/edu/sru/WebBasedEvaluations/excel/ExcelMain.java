package edu.sru.WebBasedEvaluations.excel;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ExcelMain {

    public static void main(String[] args) throws Exception {
        System.out.println("running Excel Main from directory: ");
        System.out.println(System.getProperty("user.dir"));

        // as long as the program continues to run from Group 5 - Evaluation System, relative path is valid
        FileInputStream fileraw = new FileInputStream("WebBasedEvaluations\\Program Documents\\RawData.xlsx");

        XSSFWorkbook rawwb = new XSSFWorkbook(fileraw);

        ExcelRead_group.convertFileReal(rawwb);

        System.out.println("\nCreated UserTestData.xlsx and GroupsTestData.xlsx in directory:");
        System.out.println(System.getProperty("user.dir"));
        System.out.println("Run Excelread_groupTest.java or refer to the created sheets for clarity.");
    }
}
