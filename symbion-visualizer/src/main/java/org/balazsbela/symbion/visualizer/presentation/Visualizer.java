package org.balazsbela.symbion.visualizer.presentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.balazsbela.symbion.controllers.MainController;
import org.balazsbela.symbion.models.Function;
import org.balazsbela.symbion.overlays.controllers.HudController;
import org.balazsbela.symbion.overlays.controllers.Textarea;
import org.balazsbela.symbion.visualizer.models.FunctionModel;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.render.TextRenderer;

public class Visualizer extends SimpleApplication {

	private static final boolean DEBUG = true;
	protected static final int MAX_FUNCTIONS = 100;
	public static final float EXPAND_COEFICIENT = 11f;
	protected static final float DEST_ARROW_Z_SCALE = 2f;
	protected float scaleSum = 1.0f;
	private static final Logger logger = Logger.getLogger(Visualizer.class.getName());
	private Nifty nifty;
	private FunctionNode selectedNode;
	private FunctionNode lastExpandedNode;
	private Node clickables;
	private static ResourceManager rm;
	private Map<String, FunctionNode> fnodes = new HashMap<String, FunctionNode>();
	private int nodeCount = 0;
	public HudController hudController = new HudController();

	private String lastSearchTerm = "";
	private int lastMatchIndex = 0;

	/**
	 * This contains all the expand directions of the currently expanding node
	 * this is so we do not expand two nodes in the same direction in parallel
	 * 
	 */
	Set<Vector3f> directionSet = new HashSet<Vector3f>();

	// Animation state
	List<AnimationState> nodeAnimationBuffer = new ArrayList<AnimationState>();

	public static void main(String[] args) {
		Visualizer app = new Visualizer();
		app.start();
	}

	public void init() {
		rm = new ResourceManager(assetManager);
		flyCam.setMoveSpeed(40f);

		// setDisplayFps(false); // to hide the FPS
		setDisplayStatView(false); // to hide the statistics
		inputManager.addMapping("toggleConsole", new KeyTrigger(KeyInput.KEY_LSHIFT));
		inputManager.addListener(new ActionListener() {
			public void onAction(String name, boolean isPressed, float tpf) {
				if (isPressed) {
					flyCam.setMoveSpeed(80f);
				} else {
					flyCam.setMoveSpeed(30f);
				}
			}
		}, "toggleConsole");
	}

	private void initGUI() {
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		nifty = niftyDisplay.getNifty();
		nifty.registerScreenController(hudController);
		nifty.fromXml("assets/Interface/ui.xml", "hud");
		hudController.initialize(stateManager, this);
		nifty.getCurrentScreen().findElementByName("hideButton").setFocus();
		guiViewPort.addProcessor(niftyDisplay);
		nifty.getCurrentScreen().findElementByName("panel_center").hide();

	}

	@Override
	public void simpleInitApp() {
		this.flyCam.setMoveSpeed(30);
		this.cam.lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
		this.cam.setLocation(new Vector3f(15.0f, 0, 40.0f));
		init();
		loadNodes();
		setupBackground();
		initLights();

		inputManager.addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addMapping("Select", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addListener(actionListener, "Click");
		inputManager.addListener(actionListener, "Select");

		mouseInput.setCursorVisible(true);
		flyCam.setDragToRotate(true);

		initGUI();

		System.out.println(MainController.getInstance().getRepository().getDataModel().getSourcePath());

	}

	@Override
	public void simpleUpdate(float tpf) {

		float animSpeed = 2.0f * tpf;

		for (AnimationState as : nodeAnimationBuffer) {
			// Destinations

			float arrowScale = getDistance(as.nrNodes) / 2.6f;
			if (as.nrNodes >= 70 && as.nrNodes <= 85) {
				arrowScale = getDistance(as.nrNodes) / 2.3f;
			}
			if (as.nrNodes > 85) {
				arrowScale = getDistance(as.nrNodes) / 2.0f;
			}
			if (as.nrNodes > 130) {
				arrowScale = getDistance(as.nrNodes) / 1.3f;
			}

			Vector3f destPos = getDestpos(as.direction, as.nrNodes);
			Vector3f currentPos = as.fn.getSceneNode().getLocalTranslation();

			Vector3f destArrowScale = new Vector3f(1.0f, 1.0f, arrowScale);

			if (currentPos.length() < destPos.length()) {
				// Update by moving the node in the given direction
				as.fn.getSceneNode().move(destPos.mult(animSpeed));
			}

			if (DEBUG) {
				System.out.println("Current scale:" + as.arrow.getArrowModel().getLocalScale().length());
				System.out.println("Destination scale:" + destArrowScale.length());
			}

			if (as.arrow.getArrowModel().getLocalScale().length() < destArrowScale.length()) {
				scaleSum += animSpeed;
				as.arrow.getArrowModel().scale(1.0f, 1.0f, animSpeed * 1.6f + 1);
			} else {
				nodeAnimationBuffer.remove(as);
				break;
			}

		}
	}

	@Override
	public void simpleRender(RenderManager rm) {
		// TODO: add render code
	}

	private void setupBackground() {

		Texture t = assetManager.loadTexture("assets/Textures/bgtexture2.png");
		Picture p = new Picture("sky");

		p.setTexture(assetManager, (Texture2D) t, false);
		p.setWidth(settings.getWidth());
		p.setHeight(settings.getHeight());
		p.setPosition(0, 0);
		p.updateGeometricState();

		ViewPort pv = renderManager.createPreView("background", cam);
		pv.setClearFlags(true, true, true);
		pv.attachScene(p);

		viewPort.setClearFlags(false, true, true);

	}

	private void loadNodes() {
		MainController.getInstance().getRepository().init();

		clickables = new Node("clickables");
		FunctionModel startNode = new FunctionModel();
		startNode.setFullMethodName("Start Node");

		FunctionNode globeNode = new FunctionNode("start", "Start Node", startNode);
		fnodes.put("start", globeNode);
		clickables.attachChild(globeNode.getSceneNode());
		rootNode.attachChild(clickables);
	}

	private void initLights() {

		AmbientLight light = new AmbientLight();
		light.setColor(ColorRGBA.White.mult(200.0f));
		rootNode.addLight(light);

		DirectionalLight sun4 = new DirectionalLight();
		sun4.setDirection(new Vector3f(10, -70, -70).normalizeLocal());
		sun4.setColor(ColorRGBA.White);
		rootNode.addLight(sun4);

	}

	public int getOffset() {
		try {
			String offsetString = nifty.getCurrentScreen().findNiftyControl("offsetField", TextField.class).getText();
			return Integer.parseInt(offsetString);
		} catch (Exception e) {
			return 0;
		}
	}

	public int getNrNodes() {
		try {
			TextField field = nifty.getCurrentScreen().findNiftyControl("nrNodesField", TextField.class);
			String offsetString = field.getText();
			int nr = Integer.parseInt(offsetString);
			if ((nr > MAX_FUNCTIONS) || (nr <= 0)) {
				nr = MAX_FUNCTIONS;
				field.setText(nr + "");
			}
			return nr;
		} catch (Exception e) {
			return 0;
		}
	}

	// Action Listener for click

	private ActionListener actionListener = new ActionListener() {

		public void onAction(String name, boolean keyPressed, float tpf) {
			if (name.equals("Click") && !keyPressed) {
				Vector2f mousePos = inputManager.getCursorPosition();
				Vector3f worldCoords = cam.getWorldCoordinates(mousePos, 0);
				Vector3f worldCoords2 = cam.getWorldCoordinates(mousePos, 1);
				Ray mouseRay = new Ray(worldCoords, worldCoords2.subtractLocal(worldCoords).normalizeLocal());

				CollisionResults results = new CollisionResults();

				clickables.collideWith(mouseRay, results);

				if (results.size() > 0) {
					CollisionResult closest = results.getClosestCollision();
					String methodName = closest.getGeometry().getName();

					FunctionNode fn = fnodes.get(closest.getGeometry().getName());
					// System.out.println(fn.getLabelText());
					if (fn != null && !fn.isExpanded()) {
						System.out.println("Expanding node with:" + fn.getIdString());
						expand(fn);
						fn.setExpanded(true);

					} else {
						if (fn != null) {
							System.out.println("Unexpanding node:" + fn.getIdString());
							// We unexpand it.
							fn.undoExpansion();
							fn.setExpanded(false);
						}
					}
				}
			} else {
				if (name.equals("Select") && !keyPressed) {
					Vector2f mousePos = inputManager.getCursorPosition();
					Vector3f worldCoords = cam.getWorldCoordinates(mousePos, 0);
					Vector3f worldCoords2 = cam.getWorldCoordinates(mousePos, 1);
					Ray mouseRay = new Ray(worldCoords, worldCoords2.subtractLocal(worldCoords).normalizeLocal());

					CollisionResults results = new CollisionResults();

					clickables.collideWith(mouseRay, results);

					if (results.size() > 0) {
						CollisionResult closest = results.getClosestCollision();
						String labelText = closest.getGeometry().getName();
						FunctionNode fn = fnodes.get(closest.getGeometry().getName());
						updateHudInfo(fn);
					}
				}
			}
		}

	};

	protected void expand(FunctionNode fn) {
		if (!fn.isExpanded()) {

			lastExpandedNode = fn;

			directionSet.clear();

			if (fn.getLabelText().equals("Start Node")) {
				FunctionModel funcModel = new FunctionModel();
				funcModel.setTargets(MainController.getInstance().getRepository().getRoots());
				funcModel.setFullMethodName("Start Node");
				funcModel.setFunction(new Function());
				fn.setFunctionModel(funcModel);
				System.out.println("Setting model for start node!");
			}

			Object[] targets = fn.getFunctionModel().getTargets().toArray();

			TextRenderer renderer = nifty.getCurrentScreen().findElementByName("offsetText")
					.getRenderer(TextRenderer.class);
			renderer.setText("/" + targets.length);

			// for (int i = 0; i < targets.length; i++) {
			int offset = getOffset();
			int nrExpandingNodes = getNrNodes();

			System.out.println("Offset is:" + offset);

			int nrNodes = 0;
			if (targets.length - offset >= 0) {
				nrNodes = targets.length - offset <= nrExpandingNodes ? targets.length - offset : nrExpandingNodes;
			} else {
				nrNodes = targets.length <= nrExpandingNodes ? targets.length : nrExpandingNodes;
				offset = 0;
			}

			for (int i = offset; i < offset + nrNodes; i++) {

				if (DEBUG) {
					System.out.println("Expanding node " + fn.getIdString() + " with " + targets.length + " children");
				}

				FunctionModel model = (FunctionModel) targets[i];

				String key = fn.getFunctionModel().getFullMethodName() + "->" + model.getFullMethodName();

				if (model.getFullMethodName().contains("Start Node")) {
					continue;
				}

				FunctionNode nd = new FunctionNode(key, model.getFunction().getMethodName()
						+ model.getFunction().getMethodSignature(), model);
				fn.getChildren().attachChild(nd.getSceneNode());
				nd.setParent(fn);

				fnodes.put(key, nd);

				Vector3f direction = generateNewDirection(nrNodes);
				direction = divertCourse(fn, direction, nrNodes);
				directionSet.add(direction);

				// Create and add arrow.
				Arrow arrow = new Arrow(fn, nd, direction);

				arrow.setColor(nd.getNormalColor());
				fn.getArrows().attachChild(arrow.getSceneNode());
				scheduleAnimation(nd, direction, arrow, nrNodes);

			}
		}
	}

	public float getDistance(int nrNodes) {
		if (nrNodes < 20) {
			return 20f;
		}

		if (nrNodes > 80) {
			return nrNodes * 0.8f;
		}
		if (nrNodes > 90) {
			return (nrNodes * nrNodes) / 11.0f;
		}

		if (nrNodes > 120) {
			return (nrNodes * nrNodes) / 14.0f;
		}
		return nrNodes * 1.0f;
	}

	public Vector3f getDestpos(Vector3f direction, int nrNodes) {
		float distanceCoef = getDistance(nrNodes);
		Vector3f destPos = direction.normalize().mult(distanceCoef);
		return destPos;
	}

	public Vector3f generateNewDirection(int nrNodes) {
		// http://www.cs.cmu.edu/~mws/rpos.html
		// Algorithm to generate random points on a sphere
		float distanceCoef = getDistance(nrNodes);

		float max = 2 * (int) distanceCoef;
		float min = -(int) distanceCoef;

		float z = FastMath.nextRandomFloat() * max + FastMath.nextRandomFloat() * min;
		float phi = FastMath.nextRandomFloat() * 2 * FastMath.PI;

		float theta = FastMath.asin(z / max);

		float x = max * FastMath.cos(theta) * FastMath.cos(phi);
		float y = max * FastMath.cos(theta) * FastMath.sin(phi);
		Vector3f direction = new Vector3f(x, y, z);
		direction.normalize();

		System.out.println("Trying direction:" + direction);
		return direction;
	}

	private Vector3f divertCourse(FunctionNode fn, Vector3f direction, int nrNodes) {

		CollisionResults results;
		Vector3f point = fn.getSceneNode().getWorldTranslation();
		Ray directionRay;
		boolean obstacle = false;
		int nrIterations = 10000;
		do {
			direction = generateNewDirection(nrNodes);
			obstacle = false;

			// See if that direction is reserved for another node.
			for (Vector3f vec : directionSet) {
				float distance = vec.distance(direction);
				if (distance < 1) {
					obstacle = true;

					if (DEBUG) {
						System.out.println(direction + " already reserved.");
						System.out.println("Generated direction:" + direction + " too close.");
					}
					direction = generateNewDirection(nrNodes);
				}
			}

			// Not a valid direction
			if (direction.equals(new Vector3f(0.0f, 0.0f, 0.0f))) {
				obstacle = true;
				direction = generateNewDirection(nrNodes);
			}

			/**
			 * We try to expand always in the highest angle possible in order to
			 * expand further away from the parent and not towards.
			 */
			if (fn.getParent() != null) {
				int maxNrIterations = 10;
				int nrTries = 0;
				float angle = 0;
				float maxAngle = 0;
				Vector3f maxDirection = new Vector3f();
				do {
					Vector3f parent = fn.getParent().getGeometry().getWorldTranslation().normalize();
					Vector3f node = fn.getGeometry().getWorldTranslation().normalize();

					Vector3f vec1 = node.subtract(parent);
					angle = vec1.negate().normalize().angleBetween(direction.normalize());
					if (angle > maxAngle) {
						maxDirection = direction;
						maxAngle = angle;
					}

					if (DEBUG) {
						System.out.println("Parent:" + parent);
						System.out.println("Node:" + node);
						System.out.println("Vec1" + vec1);
						System.out.println("Direction" + direction);
						System.out.println("Angle nr " + nrTries + " :" + angle);
					}

					direction = generateNewDirection(nrNodes);
					nrTries++;
				} while ((nrTries < maxNrIterations) && (angle < 3 * FastMath.PI / 4) && (angle != 0));

				if (DEBUG) {
					System.out.println("Chosen angle:" + maxAngle);
					System.out.println("Chosen direction:" + maxDirection);
				}

				direction = maxDirection;
			}

			directionRay = new Ray(point, direction);
			results = new CollisionResults();
			clickables.collideWith(directionRay, results);
			if (results.size() > 0) {
				for (int index = 0; index < results.size(); index++) {
					CollisionResult res = results.getCollision(index);

					if (DEBUG) {
						System.out.println("----------------");
						System.out.println("Expanded node:" + fn.getGeometry().getName());
						System.out.println("Checking nodes in direction :" + direction.toString() + " from "
								+ point.toString());
						System.out.println("Coollision result:" + res.getGeometry().getName());
						System.out.println(res.getGeometry().getParent().getName());
						System.out.println("Position:" + res.getGeometry().getWorldTranslation());
						System.out.println("Expanding node has position " + fn.getGeometry().getWorldTranslation());
					}
					float distance = res.getGeometry().getWorldTranslation()
							.distance(fn.getGeometry().getWorldTranslation());

					if (DEBUG) {
						System.out.println("Distance from node:" + distance);
					}

					// Not too far not too near

					if ((distance < getDistance(nrNodes) + 2.0f) && (distance > 1.0f)) {

						String name = res.getGeometry().getName();
						// Disregard camera and other stuff.
						if (name.equals("start") || name.contains("->") || name.contains("Cube")) {
							System.out.println("Found obstacle at:" + res.getGeometry().getWorldTranslation());
							obstacle = true;
							direction = generateNewDirection(nrNodes);
							System.out.println("Trying direction:" + direction);
							break;
						}
					}
				}
			}

			nrIterations++;
		} while (obstacle && nrIterations < 1000);

		if (nrIterations == 1000) {
			System.out.println("FAILED TO POSITION NODES!");
		}
		return direction.normalize();
	}

	private void scheduleAnimation(FunctionNode nd, Vector3f vec, Arrow arrow, int nrNodes) {
		AnimationState as = new AnimationState();
		as.fn = nd;
		as.direction = vec;
		as.arrow = arrow;
		as.nrNodes = nrNodes;

		if (!nodeAnimationBuffer.contains(as)) {
			nodeAnimationBuffer.add(as);
		}
	}

	public void search(String searchterm) {
		if (!searchterm.equals(lastSearchTerm)) {
			lastMatchIndex = -1;
		}

		if(DEBUG) {
			System.out.println("Searching for:" + searchterm);
			System.out.println("Last search term:"+lastSearchTerm);
			System.out.println("Last match index:"+lastMatchIndex);
		}
		
		int index = 0;
		boolean found = false;
		for (String key : fnodes.keySet()) {
			String[] list = key.split("->");
			String callee = key;
			if (list.length > 1) {
				callee = list[1];
			}

			if (callee.toLowerCase().contains(searchterm.toLowerCase())) {
				if ((searchterm.equals(lastSearchTerm)) && (index <= lastMatchIndex)) {
					index++;
					continue;
				}
				
				FunctionNode fn = fnodes.get(key);
				if(isCurrentlyAttached(fn)) {
					System.out.println(fn.getSceneNode().getParent());
					cam.setLocation(fn.getSceneNode().getWorldTranslation().add(new Vector3f(0f, 0f, 10f)));
					cam.lookAt(fn.getSceneNode().getWorldTranslation(), Vector3f.UNIT_Y);
	
					if(DEBUG) {
						System.out.println("Search term:" + searchterm + "in key " + key);
						System.out.println("Found:" + key);
						System.out.println("Focusing on:" + fn.getFunctionModel().getFullMethodName());
					}
					
					found = true;
					lastMatchIndex = index;
				}
				else {
					if(DEBUG) System.out.println("Matched but not visible on:" + fn.getFunctionModel().getFullMethodName());
					index++;
					continue;
				}

				lastSearchTerm = searchterm;
				return;
			}
			index++;
		}

		if ((!found) && (searchterm.equals(lastSearchTerm))) {
			lastMatchIndex = -1;
		}
			
		lastSearchTerm = searchterm;					
	}

	private boolean isCurrentlyAttached(FunctionNode fn) {
		final List<Spatial> visibleNodes = new ArrayList<Spatial>();
		rootNode.depthFirstTraversal(new SceneGraphVisitor() {
			@Override
			public void visit(Spatial node) {
				visibleNodes.add(node);
			}
		});
		if (visibleNodes.contains(fn.getSceneNode())) {
			return true;
		}

		return false;
	}

	protected void updateHudInfo(final FunctionNode fn) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (fn == null) {
					return;
				}
				if (!fn.getLabelText().equals("Start Node")) {
					String fullName = fn.getFunctionModel().getFunction().getContainingClassName() + "."
							+ fn.getFunctionModel().getFunction().getMethodName();

					TextRenderer renderer = nifty.getCurrentScreen().findElementByName("fullFunctionName")
							.getRenderer(TextRenderer.class);
					renderer.setText(fn.getParent().getIdString() + "->\n" + fullName);

					String className = fn.getFunctionModel().getFunction().getContainingClassName();

					int pos = className.indexOf("$");
					if (pos > 0) {
						System.out.println(className.substring(0, pos));
						className = className.substring(0, pos);
					}

					String methodName = fn.getFunctionModel().getFunction().getMethodName();

					Textarea sourceArea = nifty.getCurrentScreen().findNiftyControl("sourceText", Textarea.class);
					try {
						sourceArea.clearTextarea();
						Future<List<String>> lines = MainController.getInstance().getSourceProvider()
								.getClassText(className);
						// List<String> lines =
						// MainController.getInstance().getSourceProvider().getMethodText(className,methodName);
						for (String line : lines.get()) {
							sourceArea.appendLineNoWrap("   " + line, methodName);
						}
						sourceArea.scrollToLine(MainController.getInstance().getSourceProvider()
								.getMethodLine(className, methodName));
					} catch (IOException e) {
						sourceArea.appendLine("Source unavailable!");
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (selectedNode != null) {
						selectedNode.toggleColor();
					}
					selectedNode = fn;
					selectedNode.setSelectedColor();
				}
			}
		};

		Thread t = new Thread(runnable);
		t.start();
	}

	public static ResourceManager getResourceManager() {
		return rm;
	}

	@Override
	public void stop() {
		System.out.println("Stopping!");
		nifty.exit();
		super.stop();
	}

	public void reloadData() {
		MainController.getInstance().reloadData();
	}

}
