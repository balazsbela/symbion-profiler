package org.balazsbela.symbion.visualizer.syntaxhighlighting;

import java.util.HashMap;
import java.util.Map;

import de.lessvoid.nifty.tools.Color;

public class TokenColorMap {
	Map<TokenType,Color> colorMap;
	public TokenColorMap() {
		colorMap = new HashMap<TokenType,Color>();
		
		colorMap.put(TokenType.KEYWORD,new Color("#950055"));
		colorMap.put(TokenType.LITERAL, new Color("#2A00FF"));
		colorMap.put(TokenType.LABEL,new Color("#cf2"));
		colorMap.put(TokenType.NULL,new Color("#000"));
		colorMap.put(TokenType.OPERATOR,new Color("#009900"));
		colorMap.put(TokenType.COMMENT,new Color("#CC0066"));
	}
	
	public Map<TokenType, Color> getColorMap() {
		return colorMap;
	}
	public void setColorMap(Map<TokenType, Color> colorMap) {
		this.colorMap = colorMap;
	}
	
}
