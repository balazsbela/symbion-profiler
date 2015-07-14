package org.balazsbela.symbion.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.balazsbela.symbion.config.Rule;
import org.balazsbela.symbion.config.Rule.Action;
import org.balazsbela.symbion.errors.ProfilerError;

/*
 * Utility methods
 */
public class Utils {
	private static Map<String, Pattern> patternCache = new HashMap<String, Pattern>();
    private static final Pattern ruleRegex = Pattern
            .compile("^\\s*([a-zA-Z0-9_\\(\\)\\*\\.\\$]+)\\s*"
                    + ":\\s*(accept|reject)\\s*(.*)$");
    
	public static Pattern getRegex(String s) {
		synchronized (patternCache) {
			Pattern p = patternCache.get(s);
			if (p != null) {
				return p;
			}
			StringBuilder sb = new StringBuilder("^");
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c == '*') {
					sb.append("[a-zA-Z0-9_\\$\\.\\[\\]\\,]*");
				} else if (c == '.') {
					sb.append("\\.");
				} else if (c == '$') {
					sb.append("\\$");
				} else if (c == '(') {
					sb.append("\\(");
				} else if (c == ')') {
					sb.append("\\)");
				} else if (c == ']') {
					sb.append("\\]");
				} else if (c == '[') {
					sb.append("\\[");
				} else {
					sb.append(c);
				}
			}
			sb.append("$");
			p = Pattern.compile(sb.toString());
			patternCache.put(s, p);
			return p;
		}
	}
	
	
	public static List<Rule> parseRules(String rules) {
        List<Rule> list = new ArrayList<Rule>();
        if (rules != null && rules.trim().length()>0) {
            for (String s : rules.split("\\s*;\\s*")) {
                list.add(parseRule(s));
            }
        }
        return list;
    }

    public static Rule parseRule(String s) throws ProfilerError {
        Matcher m = ruleRegex.matcher(s);
        if (m.matches()) {
            String methodPattern = m.group(1);
            Rule.Action action = ("accept".equals(m.group(2))) ? Action.ACCEPT
                    : Action.REJECT;
            return new Rule(methodPattern, action);
        }
        throw new ProfilerError("Invalid rule!");
    }
}
