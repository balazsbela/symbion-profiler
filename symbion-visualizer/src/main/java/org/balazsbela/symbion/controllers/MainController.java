package org.balazsbela.symbion.controllers;

import org.balazsbela.symbion.visualizer.dataprocessing.MainRepository;
import org.balazsbela.symbion.visualizer.dataprocessing.SourceProvider;

public class MainController {
	private static MainController instance;
	private MainRepository repository;
	private SourceProvider sourceProvider;
	
	private MainController() {
		repository = new MainRepository();	
		sourceProvider = new SourceProvider(getRepository().getDataModel().getSourcePath());
	}
	
	public static MainController getInstance() {
		if(instance == null) {
			instance = new MainController();
		}
		return instance;
	}

	public MainRepository getRepository() {
		return repository;
	}

	public SourceProvider getSourceProvider() {
		return sourceProvider;
	}

	public void reloadData() {
		repository.init();
		
	}
}
