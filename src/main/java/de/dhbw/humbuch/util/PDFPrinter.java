package de.dhbw.humbuch.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.JOptionPane;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;

/**
 * 
 * @author Benjamin Räthlein
 *
 */
public class PDFPrinter {

	/**
	 * Read a PDF-file, render it and send it to a printer.
	 * 
	 * @param file
	 *            that shall be printed
	 */
	public PDFPrinter(File file) {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
			FileChannel fileChannel = fileInputStream.getChannel();
			ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
			createPrintJob(byteBuffer, file.getName());
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Read a ByteArray that is a PDF-file loaded in memory. Render the
	 * information and send it to a printer.
	 * 
	 * @param byteArrayOutputStream
	 *            represents a PDF-File in memory
	 */
	public PDFPrinter(ByteArrayOutputStream byteArrayOutputStream) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
		createPrintJob(byteBuffer, "Print");
	}

	private void createPrintJob(ByteBuffer byteBuffer, String jobTitle) {
		PDFFile pdfFile;
		try {
			pdfFile = new PDFFile(byteBuffer);

			// Create PDF Print Page
			PDFPrintPage pages = new PDFPrintPage(pdfFile);

			// Create Print Job
			PrinterJob pjob = PrinterJob.getPrinterJob();
			PageFormat pageFormat = PrinterJob.getPrinterJob().defaultPage();
			Paper a4paper = new Paper();

			//size of the paper in inch. 8,26 inch = 209.84mm
			double paperWidth = 8.26;
			double paperHeight = 11.69;
			a4paper.setSize(paperWidth * 72.0, paperHeight * 72.0);

			/*
			 * set the margins respectively the imageable area
			 */
			double leftMargin = 0.3;
			double rightMargin = 0.3;
			double topMargin = 0.5;
			double bottomMargin = 0.5;

			a4paper.setImageableArea(leftMargin * 72.0, topMargin * 72.0,
					(paperWidth - leftMargin - rightMargin) * 72.0,
					(paperHeight - topMargin - bottomMargin) * 72.0);
			pageFormat.setPaper(a4paper);

			pjob.setJobName(jobTitle);
			Book book = new Book();
			book.append(pages, pageFormat, pdfFile.getNumPages());
			pjob.setPageable(book);

			// Send print job to default printer
			if (pjob.printDialog()) {
				pjob.print();

			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (PrinterException e) {
			JOptionPane.showMessageDialog(null, "Printing Error: "
					+ e.getMessage(), "Print Aborted",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}


	class PDFPrintPage implements Printable {

		private PDFFile file;

		PDFPrintPage(PDFFile file) {
			this.file = file;
		}

		public int print(Graphics g, PageFormat format, int index)
				throws PrinterException {
			int pagenum = index + 1;

			// don't bother if the page number is out of range.
			if ((pagenum >= 1) && (pagenum <= file.getNumPages())) {
				// fit the PDFPage into the printing area
				Graphics2D g2 = (Graphics2D) g;
				PDFPage page = file.getPage(pagenum);
				double imageAreaWidth = format.getImageableWidth();
				double imageAreaHeight = format.getImageableHeight();

				double aspect = page.getAspectRatio();
				double imageAreaAspect = imageAreaWidth / imageAreaHeight;

				Rectangle imgbounds;

				if (aspect > imageAreaAspect) {
					// paper is too tall / pdfpage is too wide
					// aspect is the aspect of the page size. The aspect between imageWidth and imageHeight has to be the same.
					int newImageAreaHeight = (int) (imageAreaWidth / aspect);

					//(pheight  - height) = the difference between previous height and new height of imageArea. 
					//(pheight  - height) / 2 = divide through 2 to keep the same margin on top and on bottom
					// height is the height of the imageArea that fits to the aspect ratio of the page
					imgbounds = new Rectangle(
							(int) format.getImageableX(),
							(int) (format.getImageableY() + ((imageAreaHeight - newImageAreaHeight) / 2)),
							(int) imageAreaWidth, newImageAreaHeight);
				}
				else {
					// paper is too wide / pdfpage is too tall
					int newImageAreaWidth = (int) (imageAreaHeight * aspect);
					imgbounds = new Rectangle(
							(int) (format.getImageableX() + ((imageAreaWidth - newImageAreaWidth) / 2)),
							(int) format.getImageableY(), newImageAreaWidth, (int) imageAreaHeight);
				}

				// render the page
				PDFRenderer pgs = new PDFRenderer(page, g2, imgbounds, null,
						null);
				try {
					page.waitForFinish();
					pgs.run();
				}
				catch (InterruptedException ie) {}

				return PAGE_EXISTS;
			}
			else {
				return NO_SUCH_PAGE;
			}
		}
	}
}