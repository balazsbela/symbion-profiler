package org.balazsbela.symbion.profiler;


public class Rule {
	public String pattern;

	public enum Action {
		ACCEPT, REJECT
	}
	
    public enum AccessOption {
        PRIVATE, PACKAGE, PROTECTED, PUBLIC
    }

	
    public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	private Action action;


	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Rule() {

	}
	
	public Rule(String pattern,Action action) {
		this.pattern = pattern;
		this.action=action;
	}
	
    public boolean matches(String methodFqn) {
        return Utils.getRegex(pattern).matcher(methodFqn).matches();
    }
}
