package de.dhbw.humbuch.view.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;


public class StudentMaterialSelector extends CustomComponent {

	private static final long serialVersionUID = -618911643102742679L;

	private final static Logger LOG = LoggerFactory.getLogger(StudentMaterialSelector.class);

	private static final int MAX_STUDENTS_BEFORE_COLLAPSE = 12;
	public static final String TREE_TABLE_HEADER = "Daten auswählen";
	private static final String GRADE = "Klasse ";

	private VerticalLayout verticalLayoutContent;
	private TreeTable treeTableContent;
	private Map<Grade, Map<Student, List<BorrowedMaterial>>> gradeAndStudentsWithMaterials;
	private ArrayList<StudentMaterialSelectorObserver> registeredObservers;
	private HashMap<CheckBox, Object> allCheckBoxesWithId;
	private LinkedHashMap<CheckBox, Object> allGradeCheckBoxes;
	private LinkedHashMap<CheckBox, Object> allStudentCheckBoxes;
	private LinkedHashMap<CheckBox, Object> allMaterialCheckBoxes;
	private String filterString;
	private ValueChangeListener gradeListener;
	private ValueChangeListener studentListener;
	private ValueChangeListener materialListener;

	public StudentMaterialSelector() {
		init();
		buildLayout();
	}

	public void setGradesAndStudentsWithMaterials(Map<Grade, Map<Student, List<BorrowedMaterial>>> newGradeAndStudentsWithMaterials) {
		if (allCheckBoxesWithId.keySet().size() != 0) {
			updateTable(newGradeAndStudentsWithMaterials);
			this.gradeAndStudentsWithMaterials = newGradeAndStudentsWithMaterials;
		}
		else {
			this.gradeAndStudentsWithMaterials = newGradeAndStudentsWithMaterials;
			buildTable(newGradeAndStudentsWithMaterials);
		}
	}

	public HashSet<Grade> getCurrentlySelectedGrades() {
		HashSet<Grade> currentlySelectedGrades = new HashSet<Grade>();
		for (CheckBox checkBox : allCheckBoxesWithId.keySet()) {
			if (checkBox.getData() instanceof Grade) {
				if (checkBox.getValue() == true) {
					currentlySelectedGrades.add((Grade) checkBox.getData());
				}
			}
		}

		return currentlySelectedGrades;
	}

	public HashSet<Student> getCurrentlySelectedStudents() {
		HashSet<Student> currentlySelectedStudents = new HashSet<Student>();
		for (CheckBox checkBox : allCheckBoxesWithId.keySet()) {
			if (checkBox.getData() instanceof Student) {
				if (checkBox.getValue() == true) {
					currentlySelectedStudents.add((Student) checkBox.getData());
				}
			}
		}

		return currentlySelectedStudents;
	}

	public HashSet<BorrowedMaterial> getCurrentlySelectedBorrowedMaterials() {
		HashSet<BorrowedMaterial> currentlySelectedBorrowedMaterials = new HashSet<BorrowedMaterial>();
		for (CheckBox checkBox : allCheckBoxesWithId.keySet()) {
			if (checkBox.getData() instanceof BorrowedMaterial) {
				if (checkBox.getValue() == true) {
					currentlySelectedBorrowedMaterials.add((BorrowedMaterial) checkBox.getData());
				}
			}
		}
		return currentlySelectedBorrowedMaterials;
	}

	private void init() {
		verticalLayoutContent = new VerticalLayout();
		treeTableContent = new TreeTable();
		allCheckBoxesWithId = new HashMap<CheckBox, Object>();
		allGradeCheckBoxes = new LinkedHashMap<CheckBox, Object>();
		allStudentCheckBoxes = new LinkedHashMap<CheckBox, Object>();
		allMaterialCheckBoxes = new LinkedHashMap<CheckBox, Object>();

		verticalLayoutContent.setSpacing(true);

		treeTableContent.setSizeFull();
		treeTableContent.setImmediate(true);
		treeTableContent.addContainerProperty(TREE_TABLE_HEADER, CheckBox.class, null);
		
		implementListeners();
	}

	private void buildLayout() {
		verticalLayoutContent.addComponent(treeTableContent);

		setCompositionRoot(verticalLayoutContent);
	}

	private void implementListeners() {
		gradeListener = new ValueChangeListener() {
			private static final long serialVersionUID = -7395293342050339912L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				CheckBox checkBoxGrade = (CheckBox) event.getProperty();
				Grade grade = (Grade) checkBoxGrade.getData();
				boolean gradeSelected = checkBoxGrade.getValue();
				
				for (CheckBox checkBoxStudent : allStudentCheckBoxes.keySet()) {
					Student student = (Student) checkBoxStudent.getData();
					if(student.getGrade().equals(grade)) {
						checkBoxStudent.setValue(gradeSelected);
					}
				}
				notifyObserver();
			}
		};
		
		studentListener = new ValueChangeListener() {
			private static final long serialVersionUID = 5610970965368269295L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				CheckBox checkBoxStudent = (CheckBox) event.getProperty();
				Student student = (Student) checkBoxStudent.getData();
				boolean studentSelected = checkBoxStudent.getValue();
				
				for(CheckBox checkBoxMaterial : allMaterialCheckBoxes.keySet()) {
					BorrowedMaterial material = (BorrowedMaterial) checkBoxMaterial.getData();
					if(material.getStudent().equals(student)) {
						checkBoxMaterial.setValue(studentSelected);
					}
				}
				notifyObserver();
			}
		};
		
		materialListener = new ValueChangeListener() {
			private static final long serialVersionUID = 614006288313075687L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				notifyObserver();
			}
		};
	}
	
	private void buildTable(Map<Grade, Map<Student, List<BorrowedMaterial>>> currentGradeAndStudentsWithMaterials) {
		if (treeTableContent.removeAllItems()) {
			if (currentGradeAndStudentsWithMaterials == null ||
					currentGradeAndStudentsWithMaterials.isEmpty()) {
				return;
			}
			allCheckBoxesWithId.clear();
			Set<Grade> grades = currentGradeAndStudentsWithMaterials.keySet();
			
			boolean collapsed = true;
			ArrayList<Student> allStudents = getAllStudentsFromStructure(currentGradeAndStudentsWithMaterials);
			if(allStudents.size() <= MAX_STUDENTS_BEFORE_COLLAPSE) {
				collapsed = false;
			}
			
			// Add all grades as roots
			for (Grade grade : grades) {
				
				final CheckBox checkBoxGrade = new CheckBox(GRADE + grade.getGrade() + grade.getSuffix());
				checkBoxGrade.setData(grade);
				Object gradeItemId = treeTableContent.addItem(new Object[] { checkBoxGrade }, null);
				treeTableContent.setCollapsed(gradeItemId, collapsed);
				
				allCheckBoxesWithId.put(checkBoxGrade, gradeItemId);
				allGradeCheckBoxes.put(checkBoxGrade, gradeItemId);
				List<Student> students = getAllStudentsForGrade(grade, currentGradeAndStudentsWithMaterials);

				// Collect all student checkboxes for selecting purposes
				final ArrayList<CheckBox> studentCheckBoxes = new ArrayList<CheckBox>();
				// Add all students below the grade level 
				for (final Student student : students) {
					final CheckBox checkBoxStudent = new CheckBox(student.getFirstname() + " " + student.getLastname());
					checkBoxStudent.setData(student);

					studentCheckBoxes.add(checkBoxStudent);

					Object studentItemId = treeTableContent.addItem(new Object[] { checkBoxStudent }, null);
					treeTableContent.setParent(studentItemId, gradeItemId);

					allCheckBoxesWithId.put(checkBoxStudent, studentItemId);
					allStudentCheckBoxes.put(checkBoxStudent, studentItemId);
					List<BorrowedMaterial> materials = currentGradeAndStudentsWithMaterials.get(grade).get(student);
					if (materials == null) {
						LOG.warn("Borrowed Materials for Student " + student.getFirstname() + " " + student.getLastname() + " are null");
						continue;
					}

					// Collect all borrowed materials checkboxes for selecting purposes
					final ArrayList<CheckBox> borrowedMaterialCheckBoxes = new ArrayList<CheckBox>();
					for (final BorrowedMaterial material : materials) {
						final CheckBox checkBoxMaterial = new CheckBox(material.getTeachingMaterial().getName());
						checkBoxMaterial.setData(material);

						borrowedMaterialCheckBoxes.add(checkBoxMaterial);

						Object materialItemId = treeTableContent.addItem(new Object[] { checkBoxMaterial }, null);
						treeTableContent.setParent(materialItemId, studentItemId);
						treeTableContent.setChildrenAllowed(materialItemId, false);

						allCheckBoxesWithId.put(checkBoxMaterial, materialItemId);
						allMaterialCheckBoxes.put(checkBoxMaterial, materialItemId);
						
						// Whenever a borrowed material changes its value change the Set with all selected materials
						checkBoxMaterial.addValueChangeListener(materialListener);
					}

					// Whenever the student changes its value it changes all materials
					checkBoxStudent.addValueChangeListener(studentListener);
				}

				// Whenever the grade changes its value it changes all students
				checkBoxGrade.addValueChangeListener(gradeListener);
			}
		}
		else {
			LOG.warn("Could not remove all items from TreeTable in StudentMaterialSelector. "
					+ "Continuing without changing the displayed data.");
		}
	}

	private void updateTable(Map<Grade, Map<Student, List<BorrowedMaterial>>> newGradeAndStudentsWithMaterials) {
		ArrayList<Grade> newGrades = new ArrayList<Grade>(newGradeAndStudentsWithMaterials.keySet());
		ArrayList<Student> newStudents = getAllStudentsFromStructure(newGradeAndStudentsWithMaterials);
		ArrayList<BorrowedMaterial> newMaterials = getAllMaterialsFromStructure(newGradeAndStudentsWithMaterials);

		ArrayList<Grade> oldGrades = new ArrayList<Grade>(gradeAndStudentsWithMaterials.keySet());
		ArrayList<Student> oldStudents = getAllStudentsFromStructure(gradeAndStudentsWithMaterials);
		ArrayList<BorrowedMaterial> oldMaterials = getAllMaterialsFromStructure(gradeAndStudentsWithMaterials);


		if(newGrades.size() > oldGrades.size()) {
			newGrades.removeAll(oldGrades);
			for(Grade grade : newGrades) {
				CheckBox checkBoxGrade = new CheckBox(GRADE + grade.getGrade() + grade.getSuffix());
				checkBoxGrade.setData(grade);
				checkBoxGrade.addValueChangeListener(gradeListener);
				Object gradeItemId = treeTableContent.addItem(new Object[]{checkBoxGrade}, null);
				
				allCheckBoxesWithId.put(checkBoxGrade, gradeItemId);
				allGradeCheckBoxes.put(checkBoxGrade, gradeItemId);
			}
		}
		else if(newGrades.size() < oldGrades.size()) {
			oldGrades.removeAll(newGrades);
			for(Grade grade : oldGrades) {
				for(CheckBox checkBoxGrade : allGradeCheckBoxes.keySet()) {
					if(checkBoxGrade.getData().equals(grade)) {
						treeTableContent.removeItem(allGradeCheckBoxes.get(checkBoxGrade));
						allGradeCheckBoxes.remove(checkBoxGrade);
						allCheckBoxesWithId.remove(checkBoxGrade);						
						break;
					}
				}
			}
		}
		
		if(newStudents.size() > oldStudents.size()) {
			newStudents.removeAll(oldStudents);
			for(Student student : newStudents) {
				CheckBox checkBoxStudent = new CheckBox(student.getFirstname() + " " + student.getLastname());
				checkBoxStudent.setData(student);
				checkBoxStudent.addValueChangeListener(studentListener);
				Object studentItemId = treeTableContent.addItem(new Object[]{checkBoxStudent}, null);
				
				Object parentGradeItemId = null;
				for(CheckBox checkBoxGrade : allGradeCheckBoxes.keySet()) {
					Grade grade = (Grade) checkBoxGrade.getData();
					if(grade.equals(student.getGrade())) {
						parentGradeItemId = allGradeCheckBoxes.get(checkBoxGrade);
						break;
					}
				}
				treeTableContent.setParent(studentItemId, parentGradeItemId);
				
				allCheckBoxesWithId.put(checkBoxStudent, studentItemId);
				allStudentCheckBoxes.put(checkBoxStudent, studentItemId);
			}
		}
		else if(newStudents.size() < oldStudents.size()) {
			oldStudents.removeAll(newStudents);
			for(Student student : oldStudents) {
				for(CheckBox checkBoxStudent : allStudentCheckBoxes.keySet()) {
					if(checkBoxStudent.getData().equals(student)) {
						treeTableContent.removeItem(allStudentCheckBoxes.get(checkBoxStudent));
						allGradeCheckBoxes.remove(checkBoxStudent);
						allCheckBoxesWithId.remove(checkBoxStudent);						
						break;
					}
				}
			}
		}
		
		if(newMaterials.size() > oldMaterials.size()) {
			newMaterials.removeAll(oldMaterials);
			for(BorrowedMaterial material : newMaterials) {
				CheckBox checkBoxMaterial = new CheckBox(material.getTeachingMaterial().getName());
				checkBoxMaterial.setData(material);
				checkBoxMaterial.addValueChangeListener(materialListener);
				Object materialItemId = treeTableContent.addItem(new Object[]{checkBoxMaterial}, null);
				treeTableContent.setChildrenAllowed(materialItemId, false);
				
				Object parentStudentItemId = null;
				for(CheckBox checkBoxStudent : allStudentCheckBoxes.keySet()) {
					Student student = (Student) checkBoxStudent.getData();
					if(student.equals(material.getStudent())) {
						parentStudentItemId = allStudentCheckBoxes.get(checkBoxStudent);
						break;
					}
				}
				treeTableContent.setParent(materialItemId, parentStudentItemId);
				
				allCheckBoxesWithId.put(checkBoxMaterial, materialItemId);
				allMaterialCheckBoxes.put(checkBoxMaterial, materialItemId);
			}
		}
		else if(newMaterials.size() < oldMaterials.size()) {
			oldMaterials.removeAll(newMaterials);
			for(BorrowedMaterial material : oldMaterials) {
				for(CheckBox checkBoxMaterial : allMaterialCheckBoxes.keySet()) {
					if(checkBoxMaterial.getData().equals(material)) {
						treeTableContent.removeItem(allMaterialCheckBoxes.get(checkBoxMaterial));
						allGradeCheckBoxes.remove(checkBoxMaterial);
						allCheckBoxesWithId.remove(checkBoxMaterial);						
						break;
					}
				}
			}
		}

		notifyObserver();
	}

	public void setFilterString(String filterString) {
		this.filterString = filterString;
		filterTableContent();
	}

	private void filterTableContent() {
		LinkedHashMap<Grade, Map<Student, List<BorrowedMaterial>>> gradeAndStudentsWithMaterialsFiltered = new LinkedHashMap<Grade, Map<Student, List<BorrowedMaterial>>>();
		for (Grade grade : gradeAndStudentsWithMaterials.keySet()) {
			Map<Student, List<BorrowedMaterial>> entry = gradeAndStudentsWithMaterials.get(grade);
			Map<Student, List<BorrowedMaterial>> filteredEntries = new LinkedHashMap<Student, List<BorrowedMaterial>>();
			for (Student student : entry.keySet()) {
				boolean matchesFilter = false;

				// match firstname and lastname ignoring case
				String fullName = student.getFirstname() + " " + student.getLastname();
				fullName = fullName.toLowerCase();
				if (fullName.contains(filterString.toLowerCase())) {
					matchesFilter = true;
				}

				if (matchesFilter) {
					filteredEntries.put(student, entry.get(student));
				}
			}

			if (filteredEntries.size() != 0) {
				gradeAndStudentsWithMaterialsFiltered.put(grade, filteredEntries);
			}
		}
		buildTable(gradeAndStudentsWithMaterialsFiltered);
	}

	private ArrayList<Student> getAllStudentsFromStructure(Map<Grade, Map<Student, List<BorrowedMaterial>>> structure) {
		ArrayList<Student> students = new ArrayList<Student>();
		for (Grade grade : structure.keySet()) {
			Map<Student, List<BorrowedMaterial>> studentsWithMaterials = structure.get(grade);
			students.addAll(studentsWithMaterials.keySet());
		}

		return students;
	}

	private ArrayList<BorrowedMaterial> getAllMaterialsFromStructure(Map<Grade, Map<Student, List<BorrowedMaterial>>> structure) {
		ArrayList<BorrowedMaterial> materials = new ArrayList<BorrowedMaterial>();
		for (Grade grade : structure.keySet()) {
			Map<Student, List<BorrowedMaterial>> studentsWithMaterials = structure.get(grade);
			for (Student student : studentsWithMaterials.keySet()) {
				materials.addAll(studentsWithMaterials.get(student));
			}
		}

		return materials;
	}

	private List<Student> getAllStudentsForGrade(Grade grade, Map<Grade, Map<Student, List<BorrowedMaterial>>> currentGradeAndStudentsWithMaterials) {
		Map<Student, List<BorrowedMaterial>> studentsWithMaterials = currentGradeAndStudentsWithMaterials.get(grade);
		List<Student> studentList = new ArrayList<Student>(studentsWithMaterials.keySet());
		return studentList;
	}

	public void registerAsObserver(StudentMaterialSelectorObserver observer) {
		if (registeredObservers == null) {
			registeredObservers = new ArrayList<StudentMaterialSelectorObserver>();
		}
		else if (registeredObservers.contains(observer)) {
			return;
		}
		registeredObservers.add(observer);
	}

	public void notifyObserver() {
		if (registeredObservers == null) {
			return;
		}

		for (StudentMaterialSelectorObserver observer : registeredObservers) {
			observer.update();
		}
	}
}