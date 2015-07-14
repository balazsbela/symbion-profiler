package org.balazsbela.symbion.overlays.controllers;

//Your packagename here

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.AbstractController;
import de.lessvoid.nifty.controls.ScrollPanel;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.elements.tools.TextBreak;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.loaderv2.types.TextType;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.spi.render.RenderFont;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;
import japa.parser.JavaParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.balazsbela.symbion.visualizer.syntaxhighlighting.JavaTokens;

/**
 * The standard controller implementation of the textarea. A textarea is a
 * scrollpanel which can hold multiple lines of text. It is not dynamically
 * editable like a textfield. However, lines can be dynamically added through
 * methods, in which case the textarea will expand its scrollarea. It also
 * supports automatic line wrapping.
 */
public class TextareaControl extends AbstractController implements Textarea {

	/** Syntax highlighter **/
	private JavaTokens highlighter = new JavaTokens();
	/** The name of the panel where text is being displayed. */
	private static final String TEXTPANEL_NAME = "textpanel";
	/** The name of the text element rendering the text in the textarea. */
	private static final String TEXT_NAME = "areatext";
	private String sourceText="";
	/**
	 * The ID of the panel which implements scrolling through the textarea, as
	 * defined in the control definition.
	 */
	private static final String SCROLLPANEL_NAME = "textScrollpanel";
	/** The reference to the panel holding text. */
	private Element m_textPanel;
	/**
	 * The reference of the text element holding the text renderer of the text
	 * area.
	 */
	private Element m_text;
	/**
	 * The reference to the panel which implements scrolling through the
	 * textarea.
	 */
	private ScrollPanel m_scrollPanel;
	/** The reference to the renderer which renders the text in the textarea. */
	private TextRenderer m_textRenderer;
	/** True, if the vertical scrollbar of the textarea should autoscroll. */
	private boolean m_autoScroll = true;
	/** The original height of the text area. */
	private int m_originalHeight;

	@Override
	public void bind(Nifty nifty, Screen screen, Element element, Properties parameter,
			Attributes controlDefinitionAttributes) {
		super.bind(element);
		m_textPanel = element.findElementByName(TEXTPANEL_NAME);
		m_text = element.findElementByName(TEXT_NAME);
		m_scrollPanel = screen.findNiftyControl(SCROLLPANEL_NAME, ScrollPanel.class);
		m_textRenderer = m_text.getRenderer(TextRenderer.class);

		m_textRenderer.setXoffsetHack(1);
		m_textRenderer.setLineWrapping(true);

		m_scrollPanel.setStepSizeY(12);
		m_scrollPanel.setPageSizeY(50);

		m_autoScroll = true;

		m_originalHeight = m_scrollPanel.getHeight();
	}

	@Override
	public void onStartScreen() {

	}

	@Override
	public boolean inputEvent(NiftyInputEvent inputEvent) {
		return false;
	}

	@Override
	public void appendLine(String text) {
		String[] wrappedText = this.wrapText(text.split("\\r?\\n", -1));
		int oldHeight = m_textRenderer.getTextHeight();

		for (String line : wrappedText) {
			String originalText = m_textRenderer.getOriginalText();

			if (!originalText.isEmpty()) {
				m_textRenderer.setText(originalText + "\n" + line);
			} else {
				m_textRenderer.setText(line);
			}
		}

		if (m_textPanel.getHeight() < m_textRenderer.getTextHeight()) {
			m_textPanel.setConstraintHeight(new SizeValue(m_textRenderer.getTextHeight() + "px"));
			m_scrollPanel.getElement().layoutElements();

			if (m_autoScroll
					&& (m_scrollPanel.getVerticalPos() == oldHeight - m_originalHeight || oldHeight - m_originalHeight < 0)) {
				m_scrollPanel.setVerticalPos(m_textRenderer.getTextHeight());
				m_scrollPanel.getElement().layoutElements();
			}
		}
	}

	public void scrollToLine(int line) {
		int unit = 16;		
//		System.out.println("Scrolling ot line "+line+" with unit "+unit);
//		System.out.println("Vertical pos"+line*unit);
		m_scrollPanel.setVerticalPos(line * unit);
		m_scrollPanel.getElement().layoutElements();
	}

	public void appendLineNoWrap(String line,String method) {
		int oldHeight = m_textRenderer.getTextHeight();
		String originalText = m_textRenderer.getOriginalText();
		sourceText+=line+"\n";
		line = syntaxHighlight(line,method,true);
		String fullText = "";
		if (!originalText.isEmpty()) {
			fullText = originalText + "\n" + line;
		} else {
			fullText = line;
		}

		m_textRenderer.setText(fullText);
		
		if (m_textPanel.getHeight() < m_textRenderer.getTextHeight()) {
			m_textPanel.setConstraintHeight(new SizeValue(m_textRenderer.getTextHeight() + "px"));
			m_scrollPanel.getElement().layoutElements();

			if (m_autoScroll
					&& (m_scrollPanel.getVerticalPos() == oldHeight - m_originalHeight || oldHeight - m_originalHeight < 0)) {
				m_scrollPanel.setVerticalPos(m_textRenderer.getTextHeight());
				m_scrollPanel.getElement().layoutElements();
			}
		}
	}

	private String syntaxHighlight(String line,String methodName,boolean isMethod) {
		String buff=""; 
		String[] tokens = line.split(" ");
		String addition ="(";
		if(!isMethod) {
			addition ="";
		}
		for(String token : tokens) {
			if(token.trim().contains(methodName+addition)) {
				int pos = token.trim().indexOf(methodName);
				buff+=token.trim().substring(0,pos);
				buff += "\\#ff0000#"+methodName+"\\#000#";
				buff += token.substring(pos+methodName.length());								
			}
			else {
				buff += "\\"+highlighter.getColor(token.trim()).getColorString()+"#"+token+" ";
			}
		}
		
		//System.out.println(buff);
		return buff;
	}

	public void highlightPhrase(String phrase) {
		String[] lines = sourceText.split("\n");
		String[] coloredLines = m_textRenderer.getOriginalText().split("\n");
		String srcText = "";
		for(int i=0;i<coloredLines.length;i++) {
			if(lines[i].contains(phrase)) {
				coloredLines[i] = syntaxHighlight(lines[i], phrase,false);
			}
			srcText += coloredLines[i]+"\n";
		}
		
		m_textRenderer.setText(srcText);		
		
	}
	
	@Override
	public void setAutoscroll(boolean autoScroll) {
		m_autoScroll = autoScroll;
	}

	@Override
	public void clearTextarea() {
		sourceText="";
		m_textRenderer.setText("");
		m_textPanel.setConstraintHeight(new SizeValue(m_originalHeight + "px"));
		m_scrollPanel.getElement().layoutElements();
	}

	/**
	 * Wraps the given lines of a text into more lines, if they exceed the
	 * maximum width of the textarea. Returns the wrapped lines in an array,
	 * each element holding one line.
	 * 
	 * @param textLines
	 *            An array containing lines of a text which should be wrapped.
	 * @return An array containing the newly wrapped lines. If wrapping was not
	 *         necessary on any line, this contains the original textLines.
	 */
	private String[] wrapText(final String[] textLines) {
		RenderFont font = m_textRenderer.getFont();
		List<String> lines = new ArrayList<String>();
		for (String line : textLines) {
			int lineLengthInPixel = font.getWidth(line);
			if (lineLengthInPixel > m_text.getWidth()) {
				lines.addAll(new TextBreak(line, m_text.getWidth(), font).split());
			} else {
				lines.add(line);
			}
		}
		return lines.toArray(new String[0]);
	}
}