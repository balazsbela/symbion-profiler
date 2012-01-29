package org.balazsbela.symbion.visualizer;

import java.util.HashMap;
import java.util.Map;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.jme3.util.SkyFactory;

public class Visualizer extends SimpleApplication {

	private Node clickables;
	private ResourceManager rm;
	private Map<String,FunctionNode> fnodes = new HashMap<String, FunctionNode>();
	
	public static void main(String[] args) {
		Visualizer app = new Visualizer();
		app.start();
	}

	public void init() {
		rm = new ResourceManager(assetManager);
	}

	@Override
	public void simpleInitApp() {
		this.flyCam.setMoveSpeed(20);
		this.cam.lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
		this.cam.setLocation(new Vector3f(11.0f, 0, 10.0f));
		init();
		loadNodes();
		setupBackground();
		initLights();

		inputManager.addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addListener(actionListener, "Click");

		mouseInput.setCursorVisible(true);
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

	private Node createArrowNode(String arrowNodeName) {
		Node arrow = new Node(arrowNodeName);

		Spatial arrowBody = assetManager.loadModel("assets/Models/arrowBody.j3o");
		arrowBody.setMaterial(ResourceManager.nodeMat);
		arrowBody.move(new Vector3f(13.0f, -0.2f, 0f));

		Spatial arrowHead = assetManager.loadModel("assets/Models/arrowHead.j3o");
		arrowHead.setMaterial(ResourceManager.nodeMat);
		arrowHead.move(new Vector3f(15.0f, 0.05f, 0.335f));

		arrow.attachChild(arrowBody);
		arrow.attachChild(arrowHead);

		return arrow;
	}

	private void loadNodes() {
		clickables = new Node("clickables");
		Node parent = new Node("parentNode");
		FunctionNode globeNode = new FunctionNode("exampleFunction()");
		fnodes.put("exampleFunction()", globeNode);
		
		Node arrow = createArrowNode("arrow1");

		FunctionNode globeNode2 = new FunctionNode("exampleCalledFunction()");
		fnodes.put("exampleCalledFunction()", globeNode2);
		
		globeNode2.getSceneNode().move(new Vector3f(6.6f, 0f, 0f));

		clickables.attachChild(globeNode.getSceneNode());
		clickables.attachChild(globeNode2.getSceneNode());

		parent.attachChild(arrow);
		rootNode.attachChild(clickables);
		rootNode.attachChild(parent);

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
//				for (int i = 0; i < results.size(); i++) {
//					
//					float dist = results.getCollision(i).getDistance();
//					Vector3f pt = results.getCollision(i).getContactPoint();
//					String hit = results.getCollision(i).getGeometry().getName();
//					System.out.println("* Collision #" + i);
//					System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
//				}
				if (results.size() > 0) {
					CollisionResult closest = results.getClosestCollision();
					FunctionNode fn = fnodes.get(closest.getGeometry().getParent().getName());
					//System.out.println(closest.getGeometry().getParent().getName());
					System.out.println(fn.getLabelText());
										
					if(closest.getGeometry().getMaterial() == ResourceManager.nodeMat) {
						closest.getGeometry().setMaterial(ResourceManager.selectedMat);
						
					}
					else {
						closest.getGeometry().setMaterial(ResourceManager.nodeMat);
					}
				} 
			}
		}
	};

}
