package edu.sru.WebBasedEvaluations.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.time.*;

import org.apache.poi.hpsf.Date;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;


public class ExcelRead_group {





	//TBD for where to plug in, other logistics: Change "raw file" to "final file"
	/**
	 * Converts the specified format of raw data file to the specified format of finalized Excel files.
	 * @param wb is the workbook which contains raw data
	 * @throws Exception
	 */
	public static void convertFileReal(XSSFWorkbook wb) throws Exception {
		//raw/old file and sheets:
//		XSSFWorkbook wb = loadFile(file);
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFSheet sheet1 = wb.getSheetAt(1);
		XSSFSheet sheet2 = wb.getSheetAt(2);

		//new/final file and sheets:
		XSSFWorkbook groups = new XSSFWorkbook();
		XSSFSheet rvees = groups.createSheet("Group Reviewees");
		XSSFSheet rvrs = groups.createSheet("Reviewer's Levels");
		XSSFSheet lvls = groups.createSheet("Level Names");

		XSSFWorkbook users = new XSSFWorkbook();
		XSSFSheet newsheet = users.createSheet("Sheet1");

		//users sheet

		Row anomalyfirstrow = newsheet.createRow(0);
		anomalyfirstrow.createCell(0).setCellValue("File Type");
		anomalyfirstrow.createCell(1).setCellValue("User Upload");

		int newrow = 1;
		int newcol = 0;

		//title row
		String[] names = {"FIRST NAME", "LAST NAME", "TITLE", "EMAIL", "PASSWORD", "ROLE", "RESET", "DATE OF HIRE",
				"JOB TITLE", "DEPT", "DEPT MANAGER", "COMPANY NAME", "DIVISION/BRANCH"};
		Row nrow = newsheet.createRow(newrow);
		for (newcol = 0; newcol < names.length; newcol++) {
			nrow.createCell(newcol).setCellValue(names[newcol]);
		}
		newcol = 0; //reset

		int r = convFieldUsers(newsheet, sheet1);
		r = r+1;  //due to two title rows
//		System.out.println(r);

		convEvalUsers(newsheet, sheet2, r);


		try (FileOutputStream outputStream = new FileOutputStream("UserTestData.xlsx")) {
			users.write(outputStream);
		}

		//groups workbook:
		//group reviewees sheet
		convGroupReviewees(rvees, sheet);

		//reviewer levels sheet
		findGroupEvals(rvrs, sheet, sheet2);

		//level names sheet
		lvlNameSheet(lvls);


		try (FileOutputStream outputStream = new FileOutputStream("GroupsTestData.xlsx")) {
			groups.write(outputStream);
		}




	}




	/**
	 * This method parses/extracts evaluator group and level data from the raw file
	 * @param rvrs new sheet which will contain the reviewers data, formatted as specified
	 * @param sheet contains raw group data
	 * @param sheet2 contains a raw list of evaluators
	 * @throws Exception
	 */
	public static void findGroupEvals(XSSFSheet rvrs, XSSFSheet sheet, XSSFSheet sheet2) throws Exception {

		//create evaluator list
		List<String> evalnames = new ArrayList<String>();
		int i = 1; //zero is titles
		String lastname;
		String firstname;

		while (!(sheet2.getRow(i) == null)) {
			//to upper, strip to compare
			Row row = sheet2.getRow(i);
			lastname = row.getCell(0).getStringCellValue();
			firstname = row.getCell(1).getStringCellValue();

			lastname = (lastname.strip());
			firstname = (firstname.strip());

			String name = firstname + " " + lastname;
			evalnames.add(name);

			i++;
		}


		//create fillable hashes

		HashMap<String, HashMap<List<String>, List<Integer>>> container = new HashMap<String, HashMap<List<String>, List<Integer>>>();
		int oldendcell = sheet.getRow(0).getLastCellNum() -1;

		for (String name : evalnames) {

			HashMap<List<String>, List<Integer>> levelsgroups = new HashMap<List<String>, List<Integer>>();
			//put in empty levelsgroups hashmap to be filled in next loop
			container.put(name, levelsgroups);

			int groupcol = 1;

			for (groupcol = 1; groupcol < oldendcell; groupcol++) {

				int[] indicesOf = {3,4,6,7}; //row indices of the relevant levels
				boolean[] iseval = {false, false, false, false};
				int evalind = 0;
				List<String> list = new ArrayList<String>();

				//add levels:
				for (int ind : indicesOf) {
					if (!(sheet.getRow(ind).getCell(groupcol) == null)) {

						String compareName = sheet.getRow(ind).getCell(groupcol).getStringCellValue();
						if (name.equals(compareName)) {
							iseval[evalind] = true;

						}
					}
					//continue
					evalind++;

				}

				if (iseval[0]) {list.add("Level 1");}
				if (iseval[1]) {list.add("Level 2");}
				if (iseval[2]) {list.add("Consistency Review");}
				if (iseval[3]) {list.add("Face To Face");}
				//unmodifiable list is usable as valid hash key
				List<String> listlvl = Collections.unmodifiableList(list);

				//add to levelsgroups hashmap for this name:
				if (levelsgroups.get(listlvl) == null) {
					List<Integer> listgr = new ArrayList<Integer>();
					listgr.add(groupcol); //group number
					levelsgroups.put(listlvl, listgr);
				}
				else {
					levelsgroups.get(listlvl).add(groupcol);
				}


				container.put(name, levelsgroups);

//				System.out.println((container.get("jamie bower")).get(listlvl));
//
//				container.get("jamie bower").get(listlvl).add(45);
//
//				System.out.println((container.get("jamie bower")).get(listlvl));
//
//				List<String> listtest = new ArrayList<String>();
//				listtest.add("lvl1");
//
//				System.out.println((container.get("jamie bower")).get(listtest));
			}

		}

		//insert this structure in the requested format into evals rvrs sheet

		int rownum = 0;
		int colnum = 0;
		for (String name1 : container.keySet() ) {
			Row gnums = rvrs.createRow(rownum);
			Row levelnames = rvrs.createRow(rownum+1);

			gnums.createCell(0).setCellValue(name1);
			colnum++;

			HashMap<List<String>, List<Integer>> internal = container.get(name1);

//			System.out.println(internal.keySet());
//			System.out.println();

			//first pass gets the groups the eval is not in
			boolean antigroups = true;

			for (List<String> lvls : internal.keySet()) {

				if (!antigroups) {
					//levelnames.createcell.setcellval(key)
					//grnums.createcell.setcellvalue(value)
					String lvlstr = lvls.toString().replace("[", "").replace("]", "");
					levelnames.createCell(colnum).setCellValue(lvlstr); //.setStringCellValue(conglomeration of array contents)

					String groupstr = internal.get(lvls).toString();
					groupstr = groupstr.replace("[", "").replace("]", "");
					gnums.createCell(colnum).setCellValue(groupstr);
					colnum++;
				}
				antigroups = false;
			}

			rownum = rownum+2;
			colnum = 0;
		}


	}

	/**
	 * Sets the Excel sheet which defines our levels.
	 * @param lvls will contain level definitions
	 */
	public static void lvlNameSheet(XSSFSheet lvls) {
		Row titles = lvls.createRow(0);
		//differ from original titles as they were inaccurate
		titles.createCell(0).setCellValue("Level Name");
		titles.createCell(1).setCellValue("Level Number");
		titles.createCell(2).setCellValue("Company");

		String[] lvlnames = {"Level 1", "Level 2","Consistency Review", "Face to Face"};

		for (int i = 0; i < lvlnames.length; i++) {
			Row row1 = lvls.createRow(i+1);
			row1.createCell(0).setCellValue(lvlnames[i]);
			row1.createCell(1).setCellValue(i+1);
			//company
			row1.createCell(2).setCellValue("");
		}

	}


	/**
	 * this method is the first of the groups workbook sheets conversions.
	 * @param rvees is the new sheet which contains group info such as reviewees
	 * @param sheet is the old raw sheet containing dubious group information
	 * @throws Exception
	 */
	public static void convGroupReviewees(XSSFSheet rvees, XSSFSheet sheet) throws Exception {
		//first row is group names
		Row titlerow = rvees.createRow(0);
		Row oldtitlerow = sheet.getRow(1);
		int oldendcell = oldtitlerow.getLastCellNum();
		//titles
		for (int c = 0; c < oldendcell-1; c++) {
			titlerow.createCell(c).setCellValue(oldtitlerow.getCell(c+1).getStringCellValue());
		}
		Row evalform = rvees.createRow(1);
		for (int c = 0; c < oldendcell-1; c++) {
			evalform.createCell(c).setCellValue("EVAL-001");
		}
		Row yearrow = rvees.createRow(2);
		int y = Year.now().getValue();
		for (int c = 0; c < oldendcell-1; c++) {
			yearrow.createCell(c).setCellValue(y);
		}
		//self eval true appears to be default
		Row selfeval = rvees.createRow(3);
		for (int c = 0; c < oldendcell-1; c++) {
			selfeval.createCell(c).setCellValue("Self-Eval");
		}

		//sync and async not defined anywhere, setting a default
		Row a1 = rvees.createRow(4);
		Row a2 = rvees.createRow(5);
		Row a3 = rvees.createRow(6);

		for (int c = 0; c < oldendcell-1; c++) {
			a1.createCell(c).setCellValue("Async");
			a2.createCell(c).setCellValue("Async");
			a3.createCell(c).setCellValue("Async");
		}

		//preview vs nopreview not defined anywhere, setting a default
		Row p1 = rvees.createRow(7);
		Row p2 = rvees.createRow(8);
		Row p3 = rvees.createRow(9);
		Row p4 = rvees.createRow(10);

		for (int c = 0; c < oldendcell-1; c++) {
			p1.createCell(c).setCellValue("nopreview");
			p2.createCell(c).setCellValue("nopreview");
			p3.createCell(c).setCellValue("nopreview");
			p4.createCell(c).setCellValue("nopreview");
		}

		//11 onward: reviewees
		//on raw sheet, implies there cannot be more than 10 reviewees per group
		for(int i = 11; i < 21; i++) {
			rvees.createRow(i); //create max rows to hold reviewees
		}
		int col = 0; //col referring to rvees cols. sheet cols are rvees+1
		int i = 8; //row
		for (col = 0; col < oldendcell-1; col++) {
			while (sheet.getRow(i) != null) {
				Row oldrow = sheet.getRow(i);
				Row newrow = rvees.getRow(i+3);//8-11
				newrow.createCell(col).setCellValue(oldrow.getCell(col+1).getStringCellValue());
				i++;
			}
			i=8;
		}

	}

	/**
	 * users will come from field employee sheet and evaluator sheet combined, this gets eval-level employees
	 * @param newsheet sheet to be filled with users and data
	 * @param sheet2 old sheet containing dubious evaluator data
	 * @throws Exception
	 */
	private static void convEvalUsers(XSSFSheet newsheet, XSSFSheet sheet2, int startrow) throws Exception{
		//check last name, check first name in same row. if total match, change field to eval.
		//otherwise, not many addable fields due to no data available. can add email and name

		int i = 1; //zero is titles
		String lastname;
		String firstname;

		while (!(sheet2.getRow(i) == null)) {
			//to upper, strip to compare
			Row row = sheet2.getRow(i);
			lastname = row.getCell(0).getStringCellValue();
			firstname = row.getCell(1).getStringCellValue();

			lastname = (lastname.strip()).toUpperCase();
			firstname = (firstname.strip()).toUpperCase();

			int duprow = findDuplicateUserName(newsheet, lastname, firstname);

			if (duprow == 0) {
				//user is not in sheet yet, add them
				Row newrow = newsheet.createRow(startrow);
				newrow.createCell(0).setCellValue(firstname);
				newrow.createCell(1).setCellValue(lastname);
				newrow.createCell(3).setCellValue(row.getCell(2).getStringCellValue());
				newrow.createCell(4).setCellValue("$2y$12$.ahxo5UdngIuZdKSu91Jn.VtHjjYCh04.lpM5LNFdICjEjechMDQ.");
				newrow.createCell(5).setCellValue("EVALUATOR");
				newrow.createCell(6).setCellValue("TRUE");
				startrow++;
			}
			else {
				newsheet.getRow(duprow).getCell(5).setCellValue("EVALUATOR");
			}


			i++;
		}

	}

	/**
	 *
	 * @param newsheet sheet in which we are checking for users which already exist
	 * @param lastname string
	 * @param firstname string
	 * @return int representing the row where the duplicate is, or 0 if none found with the given names.
	 * @throws Exception
	 */
	private static int findDuplicateUserName(XSSFSheet newsheet, String lastname, String firstname) throws Exception{

		int i = 2;
		String last;
		String first;

		while (!(newsheet.getRow(i) == null)) {
			Row row = newsheet.getRow(i);
			//fn ln
			first = row.getCell(0).getStringCellValue();
			last = row.getCell(1).getStringCellValue();
			first = (first.strip()).toUpperCase();
			last = (last.strip()).toUpperCase();

			if (lastname.equals(last) && firstname.equals(first)) {

					return i;
			}

			i++;
		}

		return 0;
	}




	/**
	 * users will come from field employee sheet and evaluator sheet combined, this gets field employees.
	 * @param newsheet: sheet to be filled with users and data
	 * @param sheet1: old sheet containing dubious user data
	 * @return index of end row
	 * @throws Exception
	 */
	private static int convFieldUsers(XSSFSheet newsheet, XSSFSheet sheet1) throws Exception{

		String fullname = "";
		int i=1;
		//while cell has a value
		while (!(sheet1.getRow(i) == null)) {
			//setup
			int rawcellnum = 0;
			Row userrow = newsheet.createRow(i+1);
			//name
			fullname = sheet1.getRow(i).getCell(rawcellnum).getStringCellValue();
			rawcellnum++;
			String[] allnames = fullname.split(" ",2);
			userrow.createCell(0).setCellValue(allnames[0]);
			userrow.createCell(1).setCellValue(allnames[1]);

			//job title
			String jobtitle = sheet1.getRow(i).getCell(rawcellnum).getStringCellValue();
			rawcellnum++;
			userrow.createCell(8).setCellValue(jobtitle);

			//Employee ID, apparently not included in user sheet but likely should be
			rawcellnum++;

			//Company Code: placeholder of company code-- TODO currently no way to translate this to company name
			String compcode = sheet1.getRow(i).getCell(rawcellnum).getStringCellValue();
			rawcellnum++;
			userrow.createCell(11).setCellValue(compcode);

			//Division/branch --TODO no correlation: need to translate function to location
			//example vals: raw = Repair & Maintenance, users final = Slippery Rock PA
			String divbr = sheet1.getRow(i).getCell(rawcellnum).getStringCellValue();
			rawcellnum++;
			userrow.createCell(12).setCellValue(divbr);

			//date of hire
			Cell cell = userrow.createCell(7);
			cell.setCellValue(sheet1.getRow(i).getCell(rawcellnum).getDateCellValue());
			rawcellnum++;

			//group not used in users...
			rawcellnum++;

			//employee type/role:
			String role = sheet1.getRow(i).getCell(rawcellnum).getStringCellValue();
			rawcellnum++;
			if (role.equals("FIELD")) {
				userrow.createCell(5).setCellValue("USER");
			}
			else {
				throw new Exception("Unexpected value in Field Employee Type");
			}

			//Review Year is unused in Users
			rawcellnum++;

			//Email Address: TODO possible future conversion to company email
			Cell check = sheet1.getRow(i).getCell(rawcellnum);   //check for lack of email
			if (check == null || check.getCellType() == CellType.BLANK) {
				userrow.createCell(3).setBlank();
			}
			else {
				String emailraw = sheet1.getRow(i).getCell(rawcellnum).getStringCellValue();
				rawcellnum++;
				userrow.createCell(3).setCellValue(emailraw);
			}

			//supervisor unused in Users
			rawcellnum++;

			//user fields not specified in raw: title, password, role(partial), reset, dept, dept manager, company name, division/branch
			//title:
			userrow.createCell(2).setBlank();

			//password: use placeholder
			userrow.createCell(4).setCellValue("$2y$12$.ahxo5UdngIuZdKSu91Jn.VtHjjYCh04.lpM5LNFdICjEjechMDQ.");

			//reset: always true for users other than admin, who is not on this sheet
			userrow.createCell(6).setCellValue("TRUE");

			//dept seems to correlate to division/branch from raw
			String divtodept = sheet1.getRow(i).getCell(4).getStringCellValue();
			rawcellnum++;
			userrow.createCell(9).setCellValue(divtodept);

			//dept manager, placeholder set to false: TODO where does this value come from
			userrow.createCell(10).setCellValue("FALSE");

			i++;
		}
		return i;
	}





	// Upload File
	public static XSSFWorkbook loadFile(MultipartFile file) throws IOException {
		
		//FileInputStream thisxls;
		XSSFWorkbook wb;
		XSSFSheet sheet;
		wb = new XSSFWorkbook(file.getInputStream());
		sheet = wb.getSheetAt(0);

		return wb;
	}
	
	// checkStringType
	public static String checkStringType(XSSFCell testCell)
	{
		if (testCell != null) {
			if(testCell.getCellType() == CellType.NUMERIC)
			{
				return Integer.toString((int)testCell.getNumericCellValue());
			}
			
			return testCell.getStringCellValue();
		} else {
			return null;
		}
		
	}
	
	// checkStringType
		public static boolean checkBooleanType(XSSFCell testCell)
		{
			if (testCell != null) {
				if(testCell.getCellType() == CellType.BOOLEAN)
				{
					return testCell.getBooleanCellValue();
				}				
				return false;
			} else {
				return false;
			}
			
		}
		
	// checkIntType
	public static int checkIntType(XSSFCell testCell)
	{
		
		if (testCell != null) {
			if(testCell.getCellType() == CellType.STRING)
			{
				return Integer.parseInt(testCell.getStringCellValue());
			}
			return (int)testCell.getNumericCellValue();
		} else {
			return -1;
		}
	}
	public static long checkLongType(XSSFCell testCell)
	{
		
		if (testCell != null) {
			if(testCell.getCellType() == CellType.STRING)
			{
				return Long.parseLong(testCell.getStringCellValue());
			}
			return (long)testCell.getNumericCellValue();
		} else {
			return (long) -1;
		}
	}
}