package org.balazsbela.symbion.overlays.controllers;

import java.util.List;
import java.util.concurrent.Future;

import org.balazsbela.symbion.controllers.MainController;
import org.balazsbela.symbion.visualizer.presentation.Visualizer;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class HudController extends AbstractAppState implements ScreenController {

	private Nifty nifty;
	private Screen screen;
	private SimpleApplication app;

	/** custom methods */

	public HudController() {
	}
	
	public HudController(String data) {
		/** You custom constructor, can accept arguments */
	}

	/** Nifty GUI ScreenControl methods */

	public void bind(Nifty nifty, Screen screen) {
		this.nifty = nifty;
		this.screen = screen;
	}

	public void onStartScreen() {	
	}

	public void onEndScreen() {
	}

	/** jME3 AppState methods */

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.app = (SimpleApplication) app;
	}

	@Override
	public void update(float tpf) {
		/** jME update loop! */
	}
	
	public void hideSource() {
		Element element = nifty.getCurrentScreen().findElementByName("panel_center");
		if(element.isVisible()) {
			element.hide();
		}
		else {
			element.show();
			Element legend = nifty.getCurrentScreen().findElementByName("legendControl");
			LegendControl controller = legend.getControl(LegendControl.class);
			controller.init();
		}
		
	}
	
	public void searchButtonClicked() {
		String searchterm = nifty.getCurrentScreen().findNiftyControl("searchText", TextField.class).getText();
		Visualizer vz = (Visualizer) app;
		vz.search(searchterm);
	}
	
	public void applyButtonClicked() {
		
	}


	public void onFocus(final boolean getFocus) {		
	}
	
	public void loadClass(final String className) {
			
		Runnable loader = new Runnable() {
			
			@Override
			public void run() {
				synchronized (this) {								
					Textarea sourceArea = nifty.getCurrentScreen().findNiftyControl("sourceText", Textarea.class);
					sourceArea.clearTextarea();
					
					TextRenderer renderer = nifty.getCurrentScreen().findElementByName("fullFunctionName").getRenderer(TextRenderer.class);
					renderer.setText(className);	
					try {
						Future<List<String>> lines = MainController.getInstance().getSourceProvider().getClassText(className);
						for (String line : lines.get()) {							
							sourceArea.appendLineNoWrap("   " + line, "");
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					sourceArea.scrollToLine(10);	
				}
			}
			
		};		
		
		Thread loadingThread = new Thread(loader);
		loadingThread.start();
	}

	public void highlightPhrase() {
		String phrase = nifty.getCurrentScreen().findNiftyControl("findText", TextField.class).getText();
		Element sourceText = nifty.getCurrentScreen().findElementByName("sourceText");
		TextareaControl controller = sourceText.getControl(TextareaControl.class);
		controller.highlightPhrase(phrase);
	}
	
	public void loadClassFromText() {
		String className = nifty.getCurrentScreen().findNiftyControl("classLoad", TextField.class).getText(); 
		loadClass(className);
	}
}
