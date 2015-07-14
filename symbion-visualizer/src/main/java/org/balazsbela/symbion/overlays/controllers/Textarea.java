package org.balazsbela.symbion.overlays.controllers;

import de.lessvoid.nifty.controls.NiftyControl;

/**
* Defines methods to manipulate a textarea.
*/
public interface Textarea extends NiftyControl {

  /**
   * Appends the given text to the textarea. It writes it in a new
   * line. Should the text bee too wide, it automatically wraps it.
   *
   * @param text The text to append to the textarea.
   */
  public void appendLine(String text);
  
  /**
   * Append line without wrapping
   * @param text
   */
  public void appendLineNoWrap(String text,String methodName);
  

  /**
   * Sets if the textarea should autoscroll, i.e. if the vertical scrollbar
   * should automatically move to the bottom when a new line is added to the
   * textarea and the scrollbar has previously been on the bottom. This is the default behavior.
   *
   * @param autoScroll True if the vertical scrollbar should automatically move
   * to the bottom when a new line is added to the textarea and the scrollbar has previously been on the bottom
   */
  public void setAutoscroll(boolean autoScroll);

  /**
   * Clears the text area. Deletes all text in it.
   */
  public void clearTextarea();
  
  /*
   * Scroll to line
   */
  public void scrollToLine(int line);

}