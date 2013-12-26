package de.dhbw.humbuch.ui.components;

import java.util.ArrayList;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

/**
 * TODO:
 * Architecture Proposal:
 * Every possible Task is defined as enum. Every task has strictly defined subtasks.
 * When a task is created you pass a value to the constructor. With this value a 
 * database query is performed to determine the subtasks of the tasks and they
 * create to this task.
 * */
public class Task extends CustomComponent {
	
	// TODO: when proposal is accepted put all possible values in seperate enum class
	public static final String TASK_DUMMY = "dummy";
	
	private VerticalLayout verticalLayoutContent;
	private ArrayList<SubTask> subTasks;
	private Label labelTaskName;
	
	public Task(String task) {
		if(task.equals(TASK_DUMMY)) {
			init();
		}
	}
	
	private void init() {
		verticalLayoutContent = new VerticalLayout();
		
		labelTaskName = new Label("This is a dummy task");
		labelTaskName.setStyleName(Runo.LABEL_H2);
		
		
		// we would get an arraylist with strings from the backend and create everything in a single loop
		SubTask st1 = new SubTask("First you have to design a nice interface", true);
		SubTask st2 = new SubTask("After that you have to link the interface to the data", true);
		SubTask st3 = new SubTask("?????", false);
		SubTask st4 = new SubTask("Profit", false);
		subTasks = new ArrayList<SubTask>();
		subTasks.add(st1);
		subTasks.add(st2);
		subTasks.add(st3);
		subTasks.add(st4);
		
		//verticalLayoutContent.setSpacing(true);
		verticalLayoutContent.addComponent(labelTaskName);
		for(SubTask st : subTasks) {
			verticalLayoutContent.addComponent(st);
			//verticalLayoutContent.setComponentAlignment(st, Alignment.MIDDLE_CENTER);
		}
		
		setCompositionRoot(verticalLayoutContent);
	}
}
