package org.balazsbela.symbion.visualizer.syntaxhighlighting;

import java.util.HashMap;
import java.util.Map;

import de.lessvoid.nifty.tools.Color;

public class JavaTokens {
	Map<String,TokenType> javaTokens;
	TokenColorMap colorMap;	
	public JavaTokens() {
		
		javaTokens = new HashMap<String, TokenType>();
		colorMap = new TokenColorMap();
		
		javaTokens.put("package",TokenType.KEYWORD);
		javaTokens.put("import",TokenType.KEYWORD);
		javaTokens.put("byte",TokenType.KEYWORD);
		javaTokens.put("char",TokenType.KEYWORD);
		javaTokens.put("short",TokenType.KEYWORD);
		javaTokens.put("int",TokenType.KEYWORD);
		javaTokens.put("long",TokenType.KEYWORD);
		javaTokens.put("float",TokenType.KEYWORD);
		javaTokens.put("double",TokenType.KEYWORD);
		javaTokens.put("boolean",TokenType.KEYWORD);
		javaTokens.put("void",TokenType.KEYWORD);
		javaTokens.put("class",TokenType.KEYWORD);
		javaTokens.put("interface",TokenType.KEYWORD);
		javaTokens.put("abstract",TokenType.KEYWORD);
		javaTokens.put("final",TokenType.KEYWORD);
		javaTokens.put("private",TokenType.KEYWORD);
		javaTokens.put("protected",TokenType.KEYWORD);
		javaTokens.put("public",TokenType.KEYWORD);
		javaTokens.put("static",TokenType.KEYWORD);
		javaTokens.put("synchronized",TokenType.KEYWORD);
		javaTokens.put("native",TokenType.KEYWORD);
		javaTokens.put("volatile",TokenType.KEYWORD);
		javaTokens.put("transient",TokenType.KEYWORD);
		javaTokens.put("break",TokenType.KEYWORD);
		javaTokens.put("case",TokenType.KEYWORD);
		javaTokens.put("continue",TokenType.KEYWORD);
		javaTokens.put("default",TokenType.KEYWORD);
		javaTokens.put("do",TokenType.KEYWORD);
		javaTokens.put("else",TokenType.KEYWORD);
		javaTokens.put("for",TokenType.KEYWORD);
		javaTokens.put("if",TokenType.KEYWORD);
		javaTokens.put("instanceof",TokenType.KEYWORD);
		javaTokens.put("new",TokenType.KEYWORD);
		javaTokens.put("return",TokenType.KEYWORD);
		javaTokens.put("switch",TokenType.KEYWORD);
		javaTokens.put("while",TokenType.KEYWORD);
		javaTokens.put("throw",TokenType.KEYWORD);
		javaTokens.put("try",TokenType.KEYWORD);
		javaTokens.put("catch",TokenType.KEYWORD);
		javaTokens.put("extends",TokenType.KEYWORD);
		javaTokens.put("finally",TokenType.KEYWORD);
		javaTokens.put("implements",TokenType.KEYWORD);
		javaTokens.put("throws",TokenType.KEYWORD);
		javaTokens.put("this",TokenType.LITERAL);
		javaTokens.put("null",TokenType.LITERAL);
		javaTokens.put("super",TokenType.LITERAL);
		javaTokens.put("true",TokenType.LITERAL);
		javaTokens.put("false",TokenType.LITERAL);
		
		javaTokens.put("{",TokenType.LITERAL);
		javaTokens.put("}",TokenType.LITERAL);
		javaTokens.put(";",TokenType.LITERAL);
		
		javaTokens.put("+",TokenType.OPERATOR);
		javaTokens.put("-",TokenType.OPERATOR);
		javaTokens.put("*",TokenType.OPERATOR);
		javaTokens.put("/",TokenType.OPERATOR);
		javaTokens.put("+",TokenType.OPERATOR);
		javaTokens.put("++",TokenType.OPERATOR);
		javaTokens.put("--",TokenType.OPERATOR);
		javaTokens.put("+=",TokenType.OPERATOR);
		javaTokens.put("-=",TokenType.OPERATOR);
		javaTokens.put("&&",TokenType.OPERATOR);
		javaTokens.put("||",TokenType.OPERATOR);

		javaTokens.put("//",TokenType.COMMENT);
		javaTokens.put("/*",TokenType.COMMENT);
		javaTokens.put("/**",TokenType.COMMENT);
		javaTokens.put("*/",TokenType.COMMENT);
		javaTokens.put("**/",TokenType.COMMENT);
		
		
	}
	
	
	public Color getColor(String token) {
		TokenType type = javaTokens.get(token);
		if(type!=null) {
			Color c = colorMap.getColorMap().get(type);
			if(c!=null) {
				return c;
			}
		}
		return new Color("#000");
	}
}
