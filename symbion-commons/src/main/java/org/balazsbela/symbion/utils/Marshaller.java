package org.balazsbela.symbion.utils;

import org.balazsbela.symbion.models.FunctionCallListWrapper;

public interface Marshaller {
	FunctionCallListWrapper decode(String xml);
	String encode(FunctionCallListWrapper o);
}
