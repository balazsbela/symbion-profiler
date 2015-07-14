package org.balazsbela.symbion.visualizer.dataprocessing;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.balazsbela.symbion.controllers.MainController;
import org.balazsbela.symbion.visualizer.models.SourceFileMethod;
import org.balazsbela.symbion.visualizer.presentation.ResourceManager;
import org.balazsbela.symbion.visualizer.presentation.Visualizer;
import org.apache.commons.io.FileUtils;

public class SourceProvider {
	String sourcePath;
	Map<String,File> sourceFiles;

	public SourceProvider(String sourcePath) {
		this.sourcePath = sourcePath;
		if (!this.sourcePath.endsWith("\\")) {
			this.sourcePath += "\\";
		}
		sourceFiles = new HashMap<String,File>();
		//loadAllFiles();
	}

	public File findFile(String filename) {
		filename = filename.replace(".", "\\");
		String filePath = sourcePath + filename + ".java";
		File sourceFile = new File(filePath);
		return sourceFile;
	}
	
	public File retrieveFile(String className) {
		return sourceFiles.get(className);
	}
	
	public Future<List<String>> getClassText(final String className) throws Exception {
	
		Callable<List<String>> callable = new Callable<List<String>>() {
			
			@Override
			public List<String> call() throws Exception {
				File file = findFile(className);
				List<String> lines = new ArrayList<String>();
				int n = 0;
				for (Scanner sc = new Scanner(file); sc.hasNext();) {
					++n;
					lines.add(sc.nextLine());
					
				}	
				return lines;
			}
		};
		
		ExecutorService pool = Executors.newFixedThreadPool(3);
		Future<List<String>> result = pool.submit(callable);	
		return result;
	}
	
	public int getMethodLine(String classname, String methodName) throws IOException {
		File file = findFile(classname);
		FileInputStream fstream = new FileInputStream(file);
		CompilationUnit cu = null;
		try {
			// parse the file
			cu = JavaParser.parse(fstream);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			fstream.close();
		}

		MethodVisitor methodVisitor = new MethodVisitor();
		methodVisitor.visit(cu, null);

		for (SourceFileMethod sfm : methodVisitor.getMethods()) {
			if (sfm.getMethodName().equals(methodName)) {
				return (int) sfm.getStartLine();
			}
		}
		return 0;
	}
	
	public String getFullMethodName(String classname, String methodName) throws IOException {
		System.out.println("Parsing:"+classname);
		
		File file = findFile(classname);
		FileInputStream fstream = new FileInputStream(file);
		CompilationUnit cu = null;
		try {
			// parse the file
			cu = JavaParser.parse(fstream);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			fstream.close();
		}

		
		MethodVisitor methodVisitor = new MethodVisitor();
		methodVisitor.visit(cu, null);
		
		for (SourceFileMethod sfm : methodVisitor.getMethods()) {
			if (sfm.getMethodName().equals(methodName)) {				
				return sfm.getMethodName();
			}
		}
		return null;
	}

	public List<String> getMethodText(String classname, String methodName) throws IOException, FileNotFoundException {
		File file = findFile(classname);
		FileInputStream fstream = new FileInputStream(file);
		CompilationUnit cu = null;
		try {
			// parse the file
			cu = JavaParser.parse(fstream);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			fstream.close();
		}

		MethodVisitor methodVisitor = new MethodVisitor();
		methodVisitor.visit(cu, null);

		for (SourceFileMethod sfm : methodVisitor.getMethods()) {
			if (sfm.getMethodName().equals(methodName)) {
				List<String> lines = getTextPortion(file, sfm.getStartLine(), sfm.getEndLine());
				return lines;
			}
		}

		List<String> lines = new ArrayList<String>();
		lines.add("Source unavailable!");
		return lines;
	}

	private List<String> getTextPortion(File file, long startLine, long endLine) throws FileNotFoundException {
		List<String> lines = new ArrayList<String>();
		int n = 0;
		for (Scanner sc = new Scanner(file); sc.hasNext();) {
			++n;
			String line=sc.nextLine();
			if ((n >= startLine) && (n <= endLine)) {
				lines.add(line);
			}
			if(n > endLine) {
				return lines;
			}			
		}
		
		return lines;
	}

	public void loadAllFiles() {
		
		Runnable runable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					File root = new File(sourcePath);
					String[] extensions = { "java" };

					boolean recursive = true;

					// Finds files within a root directory and optionally its
					// subdirectories which match an array of extensions. When the
					// extensions is null all files will be returned.
					// This method will returns matched file as java.io.File
					Collection files = FileUtils.listFiles(root, extensions, recursive);

					for (Iterator iterator = files.iterator(); iterator.hasNext();) {
						File file = (File) iterator.next();
						String className=file.getAbsolutePath().replace("/",".");
						Visualizer.getResourceManager().getExtColor(className);
						System.out.println(className);
						sourceFiles.put(file.getAbsolutePath().replace("/","."),file);
						//System.out.println("File = " + file.getAbsolutePath());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		Thread t = new Thread(runable);
		t.start();
		
	}
}
