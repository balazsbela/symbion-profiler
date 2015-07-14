package org.balazsbela.symbion.visualizer.dataprocessing;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.balazsbela.symbion.models.SettingsModel;
import org.balazsbela.symbion.constants.Constants;
import org.balazsbela.symbion.controllers.MainController;
import org.balazsbela.symbion.models.ExecutionDataModel;
import org.balazsbela.symbion.models.Function;
import org.balazsbela.symbion.models.FunctionCall;
import org.balazsbela.symbion.models.FunctionCallListWrapper;
import org.balazsbela.symbion.utils.TimelineMarshaller;
import org.balazsbela.symbion.visualizer.models.FunctionModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainRepository {
	private static final String DATA_FILE = "execution-timeline.xml";

	private ExecutionDataModel dataModel;
	private HashMap<String, FunctionModel> functionRegistry = new HashMap<String, FunctionModel>();
	private Set<FunctionModel> roots;
	private TimelineMarshaller marshaller;
	private SettingsModel settings;

	public MainRepository() {
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml" });
		marshaller = (TimelineMarshaller) context.getBean("timeLineMarshaller");
		init();
	}

	public void init() {

		loadSettings();
		readDataModel();
		roots = extractRoots();
	}

	public synchronized void loadSettings() {
		XMLEncoder encoder;
		try {
			File settingsFile = new File(Constants.SETTINGS_XML_PATH);
			System.out.println("Settings path:" + settingsFile.getCanonicalPath());
			System.out.println(Constants.SETTINGS_XML_PATH);
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(
					settingsFile.getCanonicalPath())));
			settings = (SettingsModel) decoder.readObject();
			decoder.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void readDataModel() {
		XMLDecoder decoder;
		try {
			String path = "";
			if (settings == null) {
				System.out.println("Settings object NULL!!!");
				path = "../" + DATA_FILE;
				return;
			} else {
				path = settings.getOutputFolder() + "/" + DATA_FILE;
			}
			System.out.println("Reading data model from:" + path);
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line;
			String xml = "";
			while ((line = reader.readLine()) != null) {
				xml += line;
			}
			System.out.println("Data file has:" + xml.length() + " bytes.");
			FunctionCallListWrapper wrapper = marshaller.decode(xml);

			String sourcePath = "";
			if (settings != null) {
				System.out.println("Setting sourcepath to: " + sourcePath);
				sourcePath = settings.getSourcePath();
			}

			dataModel = new ExecutionDataModel();
			dataModel.setFunctionCalls(wrapper.getCalledMethods());
			System.out.println("Data file contains " + wrapper.getCalledMethods().size() + " function calls!");
			dataModel.setSourcePath(sourcePath);

		} catch (FileNotFoundException e) {
			// Could not read data, we will need to signal the main window.
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ExecutionDataModel getDataModel() {
		return dataModel;
	}

	private String extractFullMethodName(Function fctn) {
		String methodName = fctn.getContainingClassName() + "." + fctn.getMethodName() + "()";

		// if (fctn.getMethodSignature().length() > 1) {
		// methodName += fctn.getMethodSignature();
		// }
		return methodName;
	}

	/**
	 * Now for the hard part, we need to adapt all we have in the xml into real
	 * function nodes the visualizer deals with.
	 * 
	 * @return
	 */
	public synchronized Set<FunctionModel> extractRoots() {
		Set<FunctionModel> roots = new HashSet<FunctionModel>();
		functionRegistry = new HashMap<String, FunctionModel>();
		System.out.println("Extracting roots out of"+dataModel.getFunctionCalls().size() + " calls");
		for (FunctionCall fc : dataModel.getFunctionCalls()) {
			Function caller = fc.getParent();

			// Find or create caller
			String callerMethodKey = extractFullMethodName(caller);
			FunctionModel callerModel = null;
			if (functionRegistry.containsKey(callerMethodKey)) {
				callerModel = functionRegistry.get(callerMethodKey);
			} else {
				callerModel = new FunctionModel(callerMethodKey);
				callerModel.setFunction(caller);
				callerModel.setFullMethodName(callerMethodKey);
			}

			// Find or create target
			Function target = fc.getTarget();
			String targetMethodKey = extractFullMethodName(target);

			FunctionModel targetModel = null;
			if (functionRegistry.containsKey(targetMethodKey)) {
				targetModel = functionRegistry.get(targetMethodKey);
			} else {
				targetModel = new FunctionModel(targetMethodKey);
				targetModel.setFunction(target);
				targetModel.setFullMethodName(targetMethodKey);
			}

			// Add caller as a parent
			if (!targetModel.getParents().contains(callerModel)) {
				targetModel.getParents().add(callerModel);
			}

			// Add as a target to caller
			if (!callerModel.getTargets().contains(targetModel)) {
				callerModel.getTargets().add(targetModel);
			}

			/*
			 * If already contained in the registry Update targets/parents.
			 */
			if (!functionRegistry.containsKey(targetMethodKey)) {
				functionRegistry.put(targetMethodKey, targetModel);
			}
			// else {
			// FunctionModel model = functionRegistry.get(targetMethodKey);
			// model.getParents().add(callerModel);
			// }

			// If already contained in the registry
			if (!functionRegistry.containsKey(callerModel.getFullMethodName())) {
				functionRegistry.put(callerMethodKey, callerModel);
			}
			// else {
			// FunctionModel model = functionRegistry.get(callerMethodKey);
			// model.getTargets().add(targetModel);
			// }

		}

		// Extract those nodes with 0 parents.
		for (String key : functionRegistry.keySet()) {
			FunctionModel func = functionRegistry.get(key);
			if (func.getParents().isEmpty()) {
				roots.add(func);
			}
		}
		
		System.out.println(roots.size()+ " roots!");
		return roots;
	}

	private FunctionModel findOrReturnNew(String methodKey) {
		if (functionRegistry.containsKey(methodKey)) {
			return functionRegistry.get(methodKey);
		} else
			return new FunctionModel(methodKey);
	}

	public HashMap<String, FunctionModel> getFunctionRegistry() {
		return functionRegistry;
	}

	public Set<FunctionModel> getRoots() {
		return roots;
	}
}
