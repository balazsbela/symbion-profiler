package org.balazsbela.symbion.console.api;

import java.util.Set;

import org.balazsbela.symbion.models.ThreadModel;

public interface Console {
	public void updateListOfMatchedClasses(Set<String> matchedClasses);
	public void updateThreadList(Set<ThreadModel> threads);
}
