package org.balazsbela.symbion.console.api;

public interface MessageHandler {
	void handleError(Exception e);
	void displayMessage(String message);
	void openLoadingDialog();
	void closeLoadingDialog();
	void toggleDisconnectLabel();
}
