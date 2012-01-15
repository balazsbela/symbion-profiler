package org.balazsbela.symbion.profiler;

public class RegexTest {
	public static void main(String[] args) {
		Rule r = new Rule("org.balazsbela.FirmManagement.*",Rule.Action.ACCEPT);
		String methodFqn = "org.balazsbela.FirmManagement.Firm.toStringAction()";
		System.out.println(Utils.getRegex(r.getPattern()).matcher(methodFqn).matches());
	}
}
