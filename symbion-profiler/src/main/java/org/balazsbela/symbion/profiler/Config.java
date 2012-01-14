package org.balazsbela.symbion.profiler;

public class Config {
    private int port = 31337;
	private boolean exitVmOnFailure = true;
    private boolean waitConnection = false;
    
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


}
