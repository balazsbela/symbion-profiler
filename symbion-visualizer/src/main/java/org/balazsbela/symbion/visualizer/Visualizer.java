package org.balazsbela.symbion.visualizer;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
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
	private Material nodeMat;

	public static void main(String[] args) {
		Visualizer app = new Visualizer();
		app.start();
	}

	public void init() {
		nodeMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		nodeMat.setFloat("Shininess", 32f);
		nodeMat.setBoolean("UseMaterialColors", true);
		nodeMat.setColor("Ambient", ColorRGBA.Black);
		nodeMat.setColor("Diffuse", new ColorRGBA(232f / 256.0f, 227f / 256f, 130f / 256f, 0.7f));
		nodeMat.setColor("Specular", ColorRGBA.White);
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

	private Node createLabeledNode(String labelText) {
		Node globeNode = new Node(labelText);
		Spatial nodeModel = assetManager.loadModel("assets/Models/node.j3o");
		nodeModel.setMaterial(nodeMat);

		BitmapFont hyperion = assetManager.loadFont("assets/Fonts/Hyperion.fnt");
		BitmapText label = new BitmapText(hyperion);
		label.setText(labelText);
		label.setColor(new ColorRGBA(232f / 256.0f, 227f / 256f, 130f / 256f, 0.7f));
		label.setBox(new Rectangle(-1.0f, 0, 10f, 2f));
		label.setLocalTranslation(-0.4f, -1.3f, 0.0f);
		label.setSize(0.2f);

		globeNode.attachChild(nodeModel);
		globeNode.attachChild(label);

		globeNode.move(new Vector3f(10.0f, 0.0f, 0.0f));

		BillboardControl bc = new BillboardControl();
		bc.setAlignment(BillboardControl.Alignment.Camera);
		bc.setEnabled(true);
		nodeModel.addControl(bc);

		BillboardControl bc1 = new BillboardControl();
		bc1.setAlignment(BillboardControl.Alignment.Camera);
		bc1.setEnabled(true);
		label.addControl(bc1);

		return globeNode;
	}

	private Node createArrowNode(String arrowNodeName) {
		Node arrow = new Node(arrowNodeName);

		Spatial arrowBody = assetManager.loadModel("assets/Models/arrowBody.j3o");
		arrowBody.setMaterial(nodeMat);
		arrowBody.move(new Vector3f(13.0f, -0.2f, 0f));

		Spatial arrowHead = assetManager.loadModel("assets/Models/arrowHead.j3o");
		arrowHead.setMaterial(nodeMat);
		arrowHead.move(new Vector3f(15.0f, 0.05f, 0.335f));

		arrow.attachChild(arrowBody);
		arrow.attachChild(arrowHead);
		
		return arrow;
	}

	private void loadNodes() {

		Node parent = new Node("parentNode");
		Node globeNode = createLabeledNode("exampleFunction()");
		Node arrow = createArrowNode("arrow1");
		
		Node globeNode2 = createLabeledNode("exampleCalledFunction()");
		globeNode2.move(new Vector3f(6.6f,0f,0f));
	
		parent.attachChild(globeNode);
		parent.attachChild(arrow);
		parent.attachChild(globeNode2);

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

}
