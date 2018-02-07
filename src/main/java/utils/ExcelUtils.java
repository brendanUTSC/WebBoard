package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import core.User;

/**
 * Methods for reading and writing to Excel files.
 * 
 * @author Brendan Zhang
 *
 */
public class ExcelUtils {
	private static final Logger logger = Logger.getLogger(ExcelUtils.class.getSimpleName());

	private static String[] getExpectedHeader() {
		String[] expectedHeader = { "UserId", "Username", "Password", "FirstName", "LastName" };
		return expectedHeader;
	}

	/**
	 * Reads an Excel file that contains User information as rows. This function
	 * checks the first row to validate the headers and if the file does not have
	 * the correct headers, it will return null. If there are no users (but the
	 * header is valid), it returns an empty list.
	 * 
	 * @param file
	 *            The Excel file
	 * @return List of Users
	 */
	public static List<User> readUsersFromExcelFile(File file) {
		List<User> users = new ArrayList<User>();
		Workbook workbook = null;
		Sheet sheet = null;

		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			if (file.getName().toLowerCase().endsWith(".xls")) {
				workbook = new HSSFWorkbook(fileInputStream);
				sheet = (HSSFSheet) workbook.getSheetAt(0);
			}
			if (file.getName().toLowerCase().endsWith(".xlsx")) {
				workbook = new XSSFWorkbook(fileInputStream);
				sheet = (XSSFSheet) workbook.getSheetAt(0);
			}
			Iterator<Row> rowIterator = sheet.iterator();
			List<String> headers = new ArrayList<String>();

			if (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String cellValue = cell.getStringCellValue();
					headers.add(cellValue);
				}

				String[] expectedHeaders = getExpectedHeader();
				for (int i = 0; i < expectedHeaders.length; i++) {
					if (!headers.get(i).equals(expectedHeaders[i])) {
						return null;
					}
				}
			}

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();

				int userId = (int) cellIterator.next().getNumericCellValue();
				String username = cellIterator.next().getStringCellValue();
				String password = cellIterator.next().getStringCellValue();
				String firstName = cellIterator.next().getStringCellValue();
				String lastName = cellIterator.next().getStringCellValue();

				User user = new User(username, password, 1, firstName, lastName);
				user.setId(userId);
				users.add(user);
			}
		} catch (FileNotFoundException fnfe) {
			logger.severe("The file could not be found: " + file + ". " + fnfe);
		} catch (IOException ioe) {
			logger.severe("An IO Exception occured while trying to open: " + file + ". " + ioe);
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (Exception e) {
			}
		}

		return users;
	}

	/**
	 * Writes the data to an Excel file. data.get(i).get(j) must give the i-th row
	 * and j-th column of data. The filePath must end with a ".xls" or ".xlsx" file
	 * extension.
	 * 
	 * @param data
	 *            The data to write
	 * @param filePath
	 *            The fle path of the new File
	 */
	public static void writeToExcelFile(List<List<String>> data, String filePath) {
		// Sheet name
		String sheetName = "Sheet1";
		Workbook workBook = null;
		Sheet sheet = null;

		try {
			if (filePath.toLowerCase().endsWith(".xls")) {
				workBook = new HSSFWorkbook();
				sheet = (HSSFSheet) workBook.createSheet(sheetName);
			}
			if (filePath.toLowerCase().endsWith(".xlsx")) {
				workBook = new XSSFWorkbook();
				sheet = (XSSFSheet) workBook.createSheet(sheetName);
			}

			for (int rowNumber = 0; rowNumber < data.size(); rowNumber++) {
				Row row = sheet.createRow(rowNumber);
				for (int columnNumber = 0; columnNumber < data.get(rowNumber).size(); columnNumber++) {
					Cell cell = row.createCell(columnNumber);
					cell.setCellValue(data.get(rowNumber).get(columnNumber));
				}
			}

			FileOutputStream fileOutputStream;
			fileOutputStream = new FileOutputStream(filePath);
			workBook.write(fileOutputStream);

			fileOutputStream.flush();
			fileOutputStream.close();
			workBook.close();
		} catch (FileNotFoundException fnfe) {
			logger.severe("The file could not be found: " + filePath + ". " + fnfe);
		} catch (IOException ioe) {
			logger.severe("An IO Exception occured while trying to open: " + filePath + ". " + ioe);
		} finally {
			try {
				if (workBook != null) {
					workBook.close();
				}
			} catch (Exception e) {
			}
		}
	}
}
