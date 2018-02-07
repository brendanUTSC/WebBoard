package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import core.User;

public class ExcelUtilsTest {

	/**
	 * This test will only succeed if the file 'C:\devel\Book1.xls' exists and it
	 * contains a valid set of headers and a single row with valid input and a
	 * FirstName of F. Because of this dependency, this test is commented out. When
	 * running this test suite, it is vital to uncomment the @Test annotation and
	 * ensure the file exists.
	 */
	@Test
	public void testReadUsersFromXLSFile() {
		File excelFile = new File("C:\\devel\\Book1.xls");
		List<User> users = ExcelUtils.readUsersFromExcelFile(excelFile);
		Assert.assertEquals("F", users.get(0).getFirstName());
	}

	/**
	 * This test will only succeed if the file 'C:\devel\Book2.xlsx' exists and it
	 * contains a valid set of headers and a single row with valid input and a
	 * FirstName of D. Because of this dependency, this test is commented out. When
	 * running this test suite, it is vital to uncomment the @Test annotation and
	 * ensure the file exists.
	 */
	@Test
	public void testReadUsersFromXLSXFile() {
		File excelFile = new File("C:\\devel\\Book2.xlsx");
		List<User> users = ExcelUtils.readUsersFromExcelFile(excelFile);
		Assert.assertEquals("D", users.get(0).getFirstName());
	}
	
	@Test
	public void testWriteToExcelFile() {
		String newFileXLSX = FileWriter.getDownloadsFolder() + "Book1.xlsx";
		String newFileXLS = FileWriter.getDownloadsFolder() + "Book1.xls";
		
		List<List<String>> data = new ArrayList<List<String>>();
		List<String> row = new ArrayList<String>();
		row.add("H1");
		row.add("H2");
		row.add("H3");
		data.add(row);
		
		ExcelUtils.writeToExcelFile(data, newFileXLS);
		ExcelUtils.writeToExcelFile(data, newFileXLSX);
	}
}
