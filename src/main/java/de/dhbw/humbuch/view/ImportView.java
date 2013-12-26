package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Runo;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.viewmodel.ImportViewModel;


public class ImportView extends Panel implements View {

	private static final long serialVersionUID = -739081142499192817L;

	private static final String TITLE = "Schüler Import";
	private static final String DESCRIPTION = "Betätigen Sie den Button um Schülerdaten zu importieren.";
	private static final String IMPORT = "Importieren";

	private VerticalLayout verticalLayoutContent;
	private Label labelDescription;
	private Button buttonImport;
	private Label labelResult;

	@Inject
	public ImportView(ViewModelComposer viewModelComposer, ImportViewModel importViewModel) {
		init();
		buildLayout();
		bindViewModel(viewModelComposer, importViewModel);
	}

	private void init() {
		verticalLayoutContent = new VerticalLayout();
		verticalLayoutContent.setMargin(true);
		verticalLayoutContent.setSpacing(true);

		labelResult = new Label("put result of import here. e.g. 4/5 successfully imported");

		labelDescription = new Label(DESCRIPTION);
		labelDescription.setStyleName(Runo.LABEL_H2);

		buttonImport = new Button(IMPORT);
		buttonImport.setIcon(new ThemeResource("images/icons/32/icon_upload_red.png"));
		buttonImport.setStyleName(BaseTheme.BUTTON_LINK);

		setSizeFull();
		setCaption(TITLE);
	}

	private void buildLayout() {
		verticalLayoutContent.addComponent(labelDescription);
		verticalLayoutContent.addComponent(buttonImport);
		verticalLayoutContent.addComponent(labelResult);

		setContent(verticalLayoutContent);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	private void bindViewModel(ViewModelComposer viewModelComposer,
			Object... viewModels) {
		try {
			viewModelComposer.bind(this, viewModels);
		}
		catch (IllegalAccessException | NoSuchElementException
				| UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}
}
