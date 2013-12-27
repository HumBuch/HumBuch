package de.dhbw.humbuch.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;

import com.vaadin.ui.Upload;

import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.viewmodel.ImportViewModel;


public class CSVUploader implements Upload.Receiver, Upload.SucceededListener, Upload.FailedListener {
    
		private static final long serialVersionUID = 1L;
		
		private ImportViewModel importViewModel;
		
		private File file;
		private CSVReader csvReader;
		
		public CSVUploader(ImportViewModel importViewModel){
			this.importViewModel = importViewModel;
		}

		// Callback method to begin receiving the upload.
      public OutputStream receiveUpload(String filename,
                                        String MIMEType) {
          FileOutputStream fos = null; // Output stream to write to
          file = new File("./src/test/java/de/dhbw/humbuch/util/" + filename);
          try {
			csvReader = new CSVReader(new FileReader("./src/test/java/de/dhbw/humbuch/util/" + filename), ';', '\'', 0);
		}
		catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
          try {
              // Open the file for writing.
              fos = new FileOutputStream(file);
          } catch (final java.io.FileNotFoundException e) {
              // Error while opening the file. Not reported here.
              e.printStackTrace();
              return null;
          }

          return fos; // Return the output stream to write to
      }
      
      // This is called if the upload is finished.
      public void uploadSucceeded(Upload.SucceededEvent event) {
          // Log the upload on screen.
      	ArrayList<Student> studentList = CSVHandler.createStudentObjectsFromCSV(csvReader);
 //     	DAO<Student> daoStudent = this.importViewModel.getDAOStudent();
      	DAO<Grade> daoGrade = this.importViewModel.getDAOGrade();

      	int i = 3;
      	for(Student student : studentList){
      		//TODO: implement correct student persistance
//      		student.setGrade(grade);
//      		student.setBorrowedList(null);
//      		student.setParent(null);
//      		student.setProfile(null);
//      		student.setId(i);
//      		daoStudent.insert(student);
      		student.getGrade().setId(i);
      		daoGrade.insert(student.getGrade());
      		i++;
      	}
      	this.importViewModel.setImportResult("Success");
      }

      // This is called if the upload fails.
      public void uploadFailed(Upload.FailedEvent event) {
          // Log the failure on screen.
        	this.importViewModel.setImportResult("failed");
      }
  }