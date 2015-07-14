package org.balazsbela.symbion.console.ui;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class LoadingDialog extends Dialog {

	private Object result;
	private volatile Shell shell;
	private static volatile Display  display;
	private static GC shellGC;
	private static Color shellBackground;
	private static ImageLoader loader;
	private static ImageData[] imageDataArray;
	private static Thread animateThread;
	private static Image image;
	private static final boolean useGIFBackground = false;
	private static LoadingDialog instance;
	
	private boolean opened = false;
	
	public static LoadingDialog getInstance(Shell parent, int style) {
		if(instance ==null) {
			instance = new LoadingDialog(parent, style);
		}
		return instance;
	}
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	private LoadingDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");		
	}
	


	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		if(opened) {
			return result;
		}
		opened = true;
		createContents();
		display = getParent().getDisplay();
		Monitor primary = display.getPrimaryMonitor ();
		Rectangle bounds = primary.getBounds ();
		Rectangle rect = shell.getBounds ();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation (x, y);
		
		shell.open();
		shell.layout();
		
		shellGC = new GC(shell);
		
		Label lblDownloadingDatapleaseWait = new Label(shell, SWT.NONE);
		lblDownloadingDatapleaseWait.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDownloadingDatapleaseWait.setBounds(22, 10, 329, 15);
		lblDownloadingDatapleaseWait.setText("Downloading data,please wait ...");
		shellBackground = shell.getBackground();

		loader = new ImageLoader();
		try {
			InputStream is = LoadingDialog.class.getClassLoader().getResourceAsStream("loading.gif");
			imageDataArray = loader.load(is);			
			if (imageDataArray.length > 1) {
				animateThread = new Thread("Animation") {
					public void run() {
						/*
						 * Create an off-screen image to draw on, and fill it
						 * with the shell background.
						 */
						Image offScreenImage = new Image(display, loader.logicalScreenWidth, loader.logicalScreenHeight);
						GC offScreenImageGC = new GC(offScreenImage);
						offScreenImageGC.setBackground(shellBackground);
						offScreenImageGC.fillRectangle(0, 0, loader.logicalScreenWidth, loader.logicalScreenHeight);

						try {
							/*
							 * Create the first image and draw it on the
							 * off-screen image.
							 */
							int imageDataIndex = 0;
							ImageData imageData = imageDataArray[imageDataIndex];
							if (image != null && !image.isDisposed())
								image.dispose();
							image = new Image(display, imageData);
							offScreenImageGC.drawImage(image, 0, 0, imageData.width, imageData.height, imageData.x,
									imageData.y, imageData.width, imageData.height);

							/*
							 * Now loop through the images, creating and drawing
							 * each one on the off-screen image before drawing
							 * it on the shell.
							 */
							int repeatCount = loader.repeatCount;
							while (loader.repeatCount == 0 || repeatCount > 0) {
								switch (imageData.disposalMethod) {
								case SWT.DM_FILL_BACKGROUND:
									/*
									 * Fill with the background color before
									 * drawing.
									 */
									Color bgColor = null;
									if (useGIFBackground && loader.backgroundPixel != -1) {
										bgColor = new Color(display, imageData.palette.getRGB(loader.backgroundPixel));
									}
									offScreenImageGC.setBackground(bgColor != null ? bgColor : shellBackground);
									offScreenImageGC.fillRectangle(imageData.x, imageData.y, imageData.width,
											imageData.height);
									if (bgColor != null)
										bgColor.dispose();
									break;
								case SWT.DM_FILL_PREVIOUS:
									/*
									 * Restore the previous image before
									 * drawing.
									 */
									offScreenImageGC.drawImage(image, 0, 0, imageData.width, imageData.height,
											imageData.x, imageData.y, imageData.width, imageData.height);
									break;
								}

								imageDataIndex = (imageDataIndex + 1) % imageDataArray.length;
								imageData = imageDataArray[imageDataIndex];
								image.dispose();
								image = new Image(display, imageData);
								offScreenImageGC.drawImage(image, 0, 0, imageData.width, imageData.height, imageData.x,
										imageData.y, imageData.width, imageData.height);

								/* Draw the off-screen image to the shell. */
								shellGC.drawImage(offScreenImage, 0, 0);

								/*
								 * Sleep for the specified delay time (adding
								 * commonly-used slow-down fudge factors).
								 */
								try {
									int ms = imageData.delayTime * 10;
									if (ms < 20)
										ms += 30;
									if (ms < 30)
										ms += 10;
									Thread.sleep(ms);
								} catch (InterruptedException e) {
								}

								/*
								 * If we have just drawn the last image,
								 * decrement the repeat count and start again.
								 */
								if (imageDataIndex == imageDataArray.length - 1)
									repeatCount--;
							}
						} catch (SWTException ex) {
							System.out.println("There was an error animating the GIF");
						} finally {
							if (offScreenImage != null && !offScreenImage.isDisposed())
								offScreenImage.dispose();
							if (offScreenImageGC != null && !offScreenImageGC.isDisposed())
								offScreenImageGC.dispose();
							if (image != null && !image.isDisposed())
								image.dispose();
						}
					}
				};
				animateThread.setDaemon(true);
				animateThread.start();
			}
		} catch (SWTException ex) {
			System.out.println("There was an error loading the GIF");
		}
		
		return result;
	}
	
	public void closeLoader() {
		shell.setVisible(false);
	}
	
	public void showLoader() {
		shell.setVisible(true);
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(443, 291);
		shell.setText(getText());

	}
}
