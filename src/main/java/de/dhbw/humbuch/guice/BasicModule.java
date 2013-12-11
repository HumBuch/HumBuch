package de.dhbw.humbuch.guice;

import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.servlet.ServletModule;
import com.vaadin.ui.UI;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.ui.NavigationUI;
import de.dhbw.humbuch.ui.screens.HomeScreen;
import de.dhbw.humbuch.view.BasicUI;
import de.dhbw.humbuch.view.MVVMConfig;

public class BasicModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/*").with(BasicServlet.class);
		
		bind(ViewModelComposer.class).asEagerSingleton();
		bind(MVVMConfig.class).asEagerSingleton();
		
		//bind(LoginViewModel.class).in(UIScoped.class);
		
		bind(HomeScreen.class);
//		bind(LoginView.class);
		
		MapBinder<String, UI> mapbinder = MapBinder.newMapBinder(binder(), String.class, UI.class);
		mapbinder.addBinding(NavigationUI.class.getName()).to(NavigationUI.class);
//		mapbinder.addBinding(BasicUI.class.getName()).to(BasicUI.class);
	}

	@Provides
	private Class<? extends UI> provideUIClass() {
//		return BasicUI.class;
		return NavigationUI.class;
	}
}
