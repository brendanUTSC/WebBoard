package utils;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

/**
 * Class that contains static methods for manipulating PDF files.
 * 
 * @author Brendan Zhang
 *
 */
public class PDFUtils {
	private static Logger logger = Logger.getLogger(PDFUtils.class.getName());

	/**
	 * Prints the PDF File to PNG file(s).
	 * 
	 * @param srcFile
	 *            The path to the PDF file
	 * @param destFolder
	 *            Path of PNG file. This must be a String that ends in '.png'.
	 * @param fileName
	 *            Name of the .png file to create. If the PDF file contains multiple
	 *            pages, the png files will all of the type fileName-X, where X is
	 *            the page number of the page being printed.
	 */
	public static void printPDFToPNGFile(String srcFile, String destFolder, String fileName) {
		logger.info("Printing " + srcFile + " to " + destFolder);
		File file = new File(srcFile);
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
			FileChannel fileChannel = randomAccessFile.getChannel();
			ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
			PDFFile pdfFile = new PDFFile(byteBuffer);
			int numberOfPages = pdfFile.getNumPages();
			logger.info("Printing " + numberOfPages + "pages to png files");
			for (int i = 0; i < numberOfPages; i++) {
				PDFPage page = pdfFile.getPage(i);

				// get the width and height for the doc at the default zoom
				int width = (int) page.getWidth();
				int height = (int) page.getHeight();

				Rectangle rect = new Rectangle(0, 0, width, height);
				int rotation = page.getRotation();
				Rectangle rect1 = rect;
				if (rotation == 90 || rotation == 270)
					rect1 = new Rectangle(0, 0, rect.height, rect.width);

				BufferedImage img = (BufferedImage) page.getImage(rect.width, rect.height, rect1, null, true, true);
				String destFile = destFolder + fileName + "-" + i + ".png";
				ImageIO.write(img, "png", new File(destFile));
				logger.info("Created image file at: " + destFile);
			}
			logger.info("PDF File was successfully printed to " + numberOfPages + " PNG files.");
		} catch (FileNotFoundException fnfe) {
			logger.severe("Unable to find the file: " + srcFile + "." + fnfe);
		} catch (IOException e) {
			System.err.println("Unable to open the file: " + srcFile + "." + e);
		}
	}
}
