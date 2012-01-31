package org.balazsbela.symbion.visualizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

public class Visualizer extends SimpleApplication {

	private Node clickables;
	private ResourceManager rm;
	private Map<String, FunctionNode> fnodes = new HashMap<String, FunctionNode>();
	private List<Vector3f> expandDirections = new ArrayList<Vector3f>();
	private int nodeCount = 0;
	
	public static void main(String[] args) {
		Visualizer app = new Visualizer();
		app.start();
	}

	public void init() {
		rm = new ResourceManager(assetManager);
		initExpandDirections();
	}

	private void initExpandDirections() {
		// UP
		expandDirections.add(new Vector3f(1, 0, 0));
		// RIGHT
		expandDirections.add(new Vector3f(0, 1, 0));
		// DOWN
		expandDirections.add(new Vector3f(0, -1, 0));
		// UPRIGHT
		expandDirections.add(new Vector3f(1, 1, 0));
		// DOWNRIGHT
		expandDirections.add(new Vector3f(1, -1, 0));
		// UPLEFT
		expandDirections.add(new Vector3f(-1, 1, 0));
		// DOWNLEFT
		expandDirections.add(new Vector3f(-1, -1, 0));
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
		inputManager.addMapping("Grab", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
		inputManager.addListener(actionListener, "Click");
		inputManager.addListener(actionListener, "Grab");


		mouseInput.setCursorVisible(true);
		flyCam.setDragToRotate(true);
	}

	@Override
	public void simpleUpdate(float tpf) {

		float animSpeed = 0.8f * tpf;

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
		clickables = new Node("clickables");
		
		FunctionNode globeNode = new FunctionNode("exampleFunction()");
		fnodes.put("exampleFunction()", globeNode);		
		clickables.attachChild(globeNode.getSceneNode());
		rootNode.attachChild(clickables);
		
		nodeCount+=2;

	}

	private void initLights() {

		AmbientLight light = new AmbientLight();
		light.setColor(ColorRGBA.White.mult(5.0f));
		rootNode.addLight(light);

		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(new Vector3f(10, 0, 0).normalizeLocal());
		sun.setColor(ColorRGBA.White);
		rootNode.addLight(sun);

		DirectionalLight sun2 = new DirectionalLight();
		sun2.setDirection(new Vector3f(-10, 0, 0).normalizeLocal());
		sun2.setColor(ColorRGBA.White);
		rootNode.addLight(sun2);

		DirectionalLight sun3 = new DirectionalLight();
		sun2.setDirection(new Vector3f(10, 70, 70).normalizeLocal());
		sun2.setColor(ColorRGBA.White);
		rootNode.addLight(sun3);

		DirectionalLight sun4 = new DirectionalLight();
		sun2.setDirection(new Vector3f(10, -70, -70).normalizeLocal());
		sun2.setColor(ColorRGBA.White);
		rootNode.addLight(sun4);

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
					String labelText = closest.getGeometry().getName();
					FunctionNode fn = fnodes.get(closest.getGeometry().getName());
					// System.out.println(fn.getLabelText());
					if (fn!=null && !fn.isExpanded()) {
						List<String> expandNames = new ArrayList<String>();
						for(int i=0;i<20;i++) {
							expandNames.add("node"+nodeCount+"()");
							nodeCount++;
						}
						expand(fn, expandNames);
						fn.setExpanded(true);
					} else {
						if(fn!=null) {
							// We unexpand it.
							fn.undoExpansion();
							fn.setExpanded(false);
						}
					}
				}
			}
			else {
				if(name.equals("Grab")&& !keyPressed) {
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

						
					}
				}
			}
		}
		
	};

	protected void expand(FunctionNode fn, List<String> strings) {
		if (!fn.isExpanded()) {
			int localZ = 0;
			int localZSign = 1;
			int index = 0;
			float distanceCoef = 12.0f;
			for (int i = 0; i < strings.size(); i++) {
				FunctionNode nd = new FunctionNode(strings.get(i));
				fn.getChildren().attachChild(nd.getSceneNode());

				fnodes.put(strings.get(i), nd);

				if (i > expandDirections.size() * (localZ + 1)) {
					localZ++;
					localZSign *= -1;
				}

				index = i % expandDirections.size();
				Vector3f direction = expandDirections.get(index);
				direction.setZ(localZ * localZSign);
				// Put it in the position of the parent
				//System.out.println("Adding node in direction:" + direction);
				nd.getSceneNode().setLocalTranslation(0f, 0f, 0f);
				nd.getSceneNode().setLocalTranslation(direction.normalize().scaleAdd(distanceCoef, Vector3f.ZERO));
				Arrow arrow = new Arrow(fn, nd,direction);
				fn.getArrows().attachChild(arrow.getSceneNode());					
				
			}
		}
	}

}
