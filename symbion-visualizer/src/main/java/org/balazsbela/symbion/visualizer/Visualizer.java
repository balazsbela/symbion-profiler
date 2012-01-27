package org.balazsbela.symbion.visualizer;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.jme3.util.SkyFactory;

public class Visualizer extends SimpleApplication {

	public static void main(String[] args) {
		Visualizer app = new Visualizer();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		this.flyCam.setMoveSpeed(20);
		this.cam.lookAt(new Vector3f(10.0f, 0.0f, 0.0f),new Vector3f(0.0f,1.0f,0.0f));
		
		loadNodes();
		setupBackground();
		initLights();

	}

	float angle = FastMath.PI / 8;

	int dirRight = -1;
	int dirLeft = 1;

	Geometry torosGeom;

	@Override
	public void simpleUpdate(float tpf) {

		float animSpeed = 0.8f * tpf;

	}

	@Override
	public void simpleRender(RenderManager rm) {
		// TODO: add render code
	}

	private void setupBackground() {
		 Texture t = assetManager.loadTexture("assets/Textures/bgtexture.png");
		 Picture p = new Picture("sky");
		 
		 p.setTexture(assetManager,(Texture2D)t, false);
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

		Material yellow = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		yellow.setFloat("Shininess", 32f);
		yellow.setBoolean("UseMaterialColors", true);
		yellow.setColor("Ambient", ColorRGBA.Black);
		yellow.setColor("Diffuse",new ColorRGBA(224f/256.0f, 27f/256f, 27f/256f, 0.7f));
		yellow.setColor("Specular", ColorRGBA.White);

		
		Spatial nodeModel = assetManager.loadModel("assets/Models/node.j3o");
		nodeModel.setMaterial(yellow);
		nodeModel.move(new Vector3f(10.0f, 0.0f, 0.0f));		
		rootNode.attachChild(nodeModel);
		
		
		
		BillboardControl bc = new BillboardControl();
		//bc.setSpatial(nodeModel);
		bc.setAlignment(BillboardControl.Alignment.Camera);
		bc.setEnabled(true);

		nodeModel.addControl(bc);
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
