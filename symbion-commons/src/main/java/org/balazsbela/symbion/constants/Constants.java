package org.balazsbela.symbion.constants;

public class Constants {
	public static final String VERSION_STRING = "symbion-profiler 1.0";

	public static final int CMD_STARTPROFILING = 1;
	public static final int CMD_RCV_CFG = 2;
	public static final int CMD_STOPPROFILING = 3;
	public static final int CMD_POLLDATA = 4;
	public static final int CMD_DISCONNECT = 123;
	public static final int CMD_ACK = 0x00;
	public static final int STATUS_UNKNOWN_CMD = 0x02;
	public static final int STATUS_ERR = 0x01;
	
	public static String SETTINGS_XML_PATH="settings.xml";
}
