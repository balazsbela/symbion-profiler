package org.balazsbela.symbion.config;

import java.util.ArrayList;
import java.util.List;

import org.balazsbela.symbion.config.Rule;
import org.balazsbela.symbion.utils.Utils;


public class Config {
	private int port = 31337;
	private boolean exitVmOnFailure = true;
	private boolean waitConnection = true;
	private List<Rule> rules = new ArrayList<Rule>();
	private boolean filterParents = false;
	
	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	public Config() {
		// TODO Auto-generated constructor stub
	}

	public Config(String args) {
		// TODO Auto-generated constructor stub
	}

	public boolean isExitVmOnFailure() {
		return exitVmOnFailure;
	}

	public void setExitVmOnFailure(boolean exitVmOnFailure) {
		this.exitVmOnFailure = exitVmOnFailure;
	}

	public boolean isWaitConnection() {
		return waitConnection;
	}

	public void setWaitConnection(boolean waitConnection) {
		this.waitConnection = waitConnection;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	//Update Rules.
	public void parseRules(String rulesAsStr) {
		// update options
		rules = Utils.parseRules(rulesAsStr);
	}

	public boolean isFilterParents() {
		return filterParents;
	}

	public void setFilterParents(boolean filterParents) {
		this.filterParents = filterParents;
	}

	
}
