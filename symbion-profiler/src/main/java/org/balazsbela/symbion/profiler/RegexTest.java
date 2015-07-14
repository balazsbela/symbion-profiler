package org.balazsbela.symbion.profiler;

import org.balazsbela.symbion.config.Rule;
import org.balazsbela.symbion.utils.Utils;

public class RegexTest {
	public static void main(String[] args) {
		Rule r = new Rule("org.balazsbela.FirmManagement.*",Rule.Action.ACCEPT);
		String methodFqn = "org.balazsbela.FirmManagement.Firm.toStringAction()";
		System.out.println(Utils.getRegex(r.getPattern()).matcher(methodFqn).matches());
	}
}
