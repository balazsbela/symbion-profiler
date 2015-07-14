package org.balazsbela.symbion.overlays.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.balazsbela.symbion.visualizer.presentation.Visualizer;

import com.jme3.math.ColorRGBA;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.AbstractController;
import de.lessvoid.nifty.controls.ScrollPanel;
import de.lessvoid.nifty.controls.ScrollPanel.AutoScroll;
import de.lessvoid.nifty.controls.dynamic.PanelCreator;
import de.lessvoid.nifty.controls.dynamic.TextCreator;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;

public class LegendControl extends AbstractController {

	/** The name of the text element rendering the text in the textarea. */
	private static final String TEXT_NAME = "legendPanel";
	private static final String SCROLLPANEL_NAME = "legendScrollpanel";

	private ScrollPanel m_scrollPanel;
	private Element m_legendPanel;
	private Nifty nifty;
	private Screen screen;
	private List<String> classes = new ArrayList<String>();

	@Override
	public void bind(Nifty nifty, Screen screen, Element element, Properties parameter,
			Attributes controlDefinitionAttributes) {
		super.bind(element);
		m_scrollPanel = screen.findNiftyControl(SCROLLPANEL_NAME, ScrollPanel.class);
		m_legendPanel = screen.findElementByName(TEXT_NAME);
		this.nifty = nifty;
		this.screen = screen;
		m_scrollPanel.setStepSizeY(12);
		m_scrollPanel.setPageSizeY(50);
	}

	public void init() {		
		
		System.out.println("Number of classes:"+Visualizer.getResourceManager().getClassColors().keySet().size());
		System.out.println("Of which loaded:"+classes.size());
		
		List<String> list = new ArrayList<String>(Visualizer.getResourceManager().getClassColors().keySet());
		Collections.sort(list);
		for(String str:list) {		
			System.out.println("Adding class "+ str);
			if(str.toLowerCase().equals("start node")) {
				continue;
			}
			if(classes.contains(str)) {
				continue;
			}
			
			classes.add(str);
			System.out.println("Increasing:"+classes.size());
			ColorRGBA color = Visualizer.getResourceManager().getClassColors().get(str);
						
			String colorCode = getColorCode(color);
			String textColorCode = getColorCode(color.add(new ColorRGBA(0.2f,0.2f,0.2f,1.0f)));					
			
			PanelCreator panelCreator = new PanelCreator();
			panelCreator.setHeight("30px");
			panelCreator.setWidth("95%");
			panelCreator.setChildLayout("vertical");
			panelCreator.setVAlign("top");
			panelCreator.setBackgroundColor(colorCode);
			panelCreator.setAlign("center");
			panelCreator.setInteractOnClick("loadClass("+str+")");			
			
			TextCreator textCreator = new TextCreator(str);
			textCreator.setWidth("100px");
			textCreator.setHeight("30px");
			textCreator.setColor(textColorCode);
			textCreator.setFont("Interface/Fonts/Default.fnt");
			textCreator.setAlign("center");

						
			Element panel = panelCreator.create(nifty, screen, m_legendPanel);				
			Element text = textCreator.create(nifty, screen, panel);			
			panel.layoutElements();		
			
			m_legendPanel.add(panel);
			m_legendPanel.layoutElements(); 
			m_scrollPanel.getElement().layoutElements();
		}
		
		System.out.println("Loaded now:"+classes.size());
		m_legendPanel.setConstraintHeight(new SizeValue((classes.size()*70)+"px"));
		m_scrollPanel.getElement().layoutElements();
	}

	@Override
	public boolean inputEvent(NiftyInputEvent arg0) {
		return false;
	}

	@Override
	public void onStartScreen() {
	}
	
	private String getColorCode(ColorRGBA color) {
		int red = (int) (color.getRed() * 255);
		int green = (int) (color.getGreen() * 255);
		int blue = (int) (color.getBlue() * 255);
		int alpha = (int) (color.getAlpha() * 255);
		
		
		String redString = Integer.toHexString(red);
		if(redString.length()<2) {
			redString = 0 + redString;
		}
		
		String greenString = Integer.toHexString(green);
		if(greenString.length()<2) {
			greenString = 0 + greenString;
		}
		
		String blueString = Integer.toHexString(blue);
		if(blueString.length()<2) {
			blueString = 0 + blueString;
		}
		
		String alphaString = Integer.toHexString(alpha);
		if(alphaString.length()<2) {
			alphaString = 0 + alphaString;
		}				
		
		String colorString = "#"+redString+greenString+blueString+alphaString;
		return colorString;
	}

}
