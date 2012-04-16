package org.balazsbela.symbion.profiler;

import java.util.List;
import org.balazsbela.symbion.profiler.Rule;
import org.balazsbela.symbion.profiler.Utils;


public class Config {
	private int port = 31337;
	private boolean exitVmOnFailure = true;
	private boolean waitConnection = true;

	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	private List<Rule> rules;

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

}
