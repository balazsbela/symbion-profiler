package org.balazsbela.symbion.profiler;

public class ProfilerError extends Error {
	public ProfilerError(String message) {
		super(message);
	}

	public ProfilerError(String message, Throwable cause) {
		super(message, cause);
	}

	public ProfilerError(Throwable cause) {
		super(cause);
	}

}
