package org.balazsbela.symbion.overlays.controllers;

import org.balazsbela.symbion.visualizer.Visualizer;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
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

	public void searchButtonClicked() {
		String searchterm = nifty.getCurrentScreen().findNiftyControl("searchText", TextField.class).getText();
		Visualizer vz = (Visualizer) app;
		vz.search(searchterm);
	}


	public void onFocus(final boolean getFocus) {		
	}

}
