package org.balazsbela.symbion.models;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Model to hold the settings, to save them to disk.
 * @author balazsbela
 *
 */
public class SettingsModel implements Serializable {
	private String host;
	private int port;
	private String sourcePath;
	private Set<String> rules;
	private Set<String> rejectRules;
	private boolean filterParents;
	private String outputFolder;
	
	public SettingsModel() {
		
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public Set<String> getRules() {
		return rules;
	}

	public void setRules(Set<String> rules) {
		this.rules = rules;
	}

	public boolean isFilterParents() {
		return filterParents;
	}

	public void setFilterParents(boolean filterParents) {
		this.filterParents = filterParents;
	}

	public Set<String> getRejectRules() {
		return rejectRules;
	}

	public void setRejectRules(Set<String> rejectRules) {
		this.rejectRules = rejectRules;
	}

	public String getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}
	
}
