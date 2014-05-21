package de.dhbw.humbuch.view.components;

/**
 * This interface has to be implemented from every class which wants to receive
 * updates from the StudentMaterial Selector.
 * 
 * @author Henning Muszynski
 * */
public interface StudentMaterialSelectorObserver {
	/**
	 * This method is called whenever the StudentMaterialSelector gets updated.
	 * */
	public void update();
}
