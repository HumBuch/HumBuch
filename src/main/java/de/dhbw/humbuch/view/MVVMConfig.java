package de.dhbw.humbuch.view;

import com.google.inject.Inject;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.StateChangeWrapper;
import de.davherrmann.mvvm.SourceWrapper;
import de.davherrmann.mvvm.ActionWrapper;
import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.State;

public class MVVMConfig {

	@Inject
	public MVVMConfig(ViewModelComposer viewModelComposer) {
		viewModelComposer.addStateChangeWrapper(AbstractField.class,
				new StateChangeWrapper() {
					@Override
					public StateChangeListener getStateChangeListener(
							final Object notified) {
						return new StateChangeListener() {
							@SuppressWarnings("unchecked")
							@Override
							public void stateChange(Object value) {
								((AbstractField<Object>) notified)
										.setValue(value);
							}
						};
					}
				});

		viewModelComposer.addSourceWrapper(AbstractField.class,
				new SourceWrapper<Object>() {
					@SuppressWarnings("unchecked")
					@Override
					public Object get(Object source) {
						return ((AbstractField<Object>) source).getValue();
					}
				});

		viewModelComposer.addActionWrapper(ValueChangeNotifier.class,
				new ActionWrapper() {
					@Override
					public void addActionHandler(Object notifier,
							final ActionHandler actionHandler) {
						((ValueChangeNotifier) notifier)
								.addValueChangeListener(new ValueChangeListener() {
									private static final long serialVersionUID = -854400079672018869L;

									@Override
									public void valueChange(
											ValueChangeEvent event) {
										try {
											actionHandler.handle(event
													.getProperty().getValue());
										} catch (UnsupportedOperationException e) {
											System.out.println(e.getMessage());
										}
									}
								});
					}
				});

		viewModelComposer.addActionWrapper(Button.class, new ActionWrapper() {
			@Override
			public void addActionHandler(Object notifier,
					final ActionHandler actionHandler) {
				((Button) notifier).addClickListener(new ClickListener() {
					private static final long serialVersionUID = 3154305342571215268L;

					@Override
					public void buttonClick(ClickEvent event) {
						actionHandler.handle(event.getButton().getData());
					}
				});
			}
		});

		viewModelComposer.addStateChangeWrapper(State.class,
				new StateChangeWrapper() {
					@Override
					public StateChangeListener getStateChangeListener(
							final Object notified) {
						return new StateChangeListener() {
							@SuppressWarnings("unchecked")
							@Override
							public void stateChange(Object value) {
								((State<Object>) notified).set(value);
							}
						};
					}
				});
	}
}
