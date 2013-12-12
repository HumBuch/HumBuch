package de.dhbw.humbuch.ui.components;

import java.util.ArrayList;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;


public class Task extends CustomComponent implements IComponent {

	private VerticalLayout verticalLayoutContent;
	private ArrayList<SubTask> subTasks;
	private Label labelTaskName;
	
	public Task() {
		init();
	}
	
	private void init() {
		verticalLayoutContent = new VerticalLayout();
		labelTaskName = new Label("Dummy Description");
		SubTask st1 = new SubTask("Dummy task 1");
		SubTask st2 = new SubTask("Dummy task 2");
		SubTask st3 = new SubTask("Dummy task 3");
		subTasks = new ArrayList<SubTask>();
		subTasks.add(st1);
		subTasks.add(st2);
		subTasks.add(st3);
		
		verticalLayoutContent.addComponent(labelTaskName);
		for(SubTask st : subTasks) {
			Component stComp = st.getComponent();
			verticalLayoutContent.addComponent(stComp);
			verticalLayoutContent.setComponentAlignment(stComp, Alignment.MIDDLE_CENTER);
		}
	}
	
	@Override
	public Component getComponent() {
		return verticalLayoutContent;
	}
}
