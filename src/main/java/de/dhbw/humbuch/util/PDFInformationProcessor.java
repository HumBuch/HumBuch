package de.dhbw.humbuch.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;

/**
 * Utility class which process information used for latter Pdf generation.
 * 
 * @author Henning Muszynski
 * */
public class PDFInformationProcessor {

	/**
	 * It sorts the final data structure for grades and students and links
	 * together all students and their materials.
	 * 
	 * @param allSelectedMaterials
	 *            all selected materials which should be linked to their
	 *            students
	 * @param allSelectedStudents
	 *            all selected students which should be linked to their
	 *            materials
	 * @return the students and their materials linked together in one data
	 *         structure
	 * */
	public static LinkedHashMap<Student, List<BorrowedMaterial>> linkStudentsAndMaterials(
			HashSet<BorrowedMaterial> allSelectedMaterials,
			HashSet<Student> allSelectedStudents) {
		LinkedHashMap<Student, List<BorrowedMaterial>> studentsWithMaterials = new LinkedHashMap<Student, List<BorrowedMaterial>>();

		// Sort for grades and students
		TreeMap<Grade, List<Student>> treeToSortForGrades = new TreeMap<Grade, List<Student>>();
		for (Student student : allSelectedStudents) {
			if (treeToSortForGrades.containsKey(student.getGrade())) {
				List<Student> studentsInGrade = treeToSortForGrades.get(student
						.getGrade());
				if (studentsInGrade.contains(student)) {
					continue;
				}
				studentsInGrade.add(student);
				Collections.sort(studentsInGrade);
				treeToSortForGrades.put(student.getGrade(), studentsInGrade);
			} else {
				List<Student> studentList = new ArrayList<Student>();
				studentList.add(student);
				treeToSortForGrades.put(student.getGrade(), studentList);
			}
		}

		// Extract all the informationen needed to create the pdf
		for (Grade grade : treeToSortForGrades.keySet()) {
			List<Student> studentsInGrade = treeToSortForGrades.get(grade);
			for (Student student : studentsInGrade) {
				for (BorrowedMaterial material : allSelectedMaterials) {
					if (student.equals(material.getStudent())) {
						if (studentsWithMaterials.containsKey(student)) {
							List<BorrowedMaterial> currentlyAddedMaterials = studentsWithMaterials
									.get(student);
							currentlyAddedMaterials.add(material);
							Collections.sort(currentlyAddedMaterials);
							studentsWithMaterials.put(student,
									currentlyAddedMaterials);
						} else {
							List<BorrowedMaterial> materialList = new ArrayList<BorrowedMaterial>();
							materialList.add(material);
							studentsWithMaterials.put(student, materialList);
						}
					}
				}
			}
		}

		return studentsWithMaterials;
	}
}
