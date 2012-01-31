package org.balazsbela.symbion.visualizer;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class ResourceManager {
	private AssetManager assetManager;
	
	static Material nodeMat;
	static Material selectedMat;
	static Spatial nodeModel; 
	static BitmapFont hyperion;
	static Spatial arrowBody;
	static Spatial arrowHead;
	static Spatial arrow;
	
	public ResourceManager(AssetManager assetManager) {
		nodeMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		nodeMat.setFloat("Shininess", 32f);
		nodeMat.setBoolean("UseMaterialColors", true);
		nodeMat.setColor("Ambient", ColorRGBA.Black);
		nodeMat.setColor("Diffuse", new ColorRGBA(232f / 256.0f, 227f / 256f, 130f / 256f, 0.7f));
		nodeMat.setColor("Specular", ColorRGBA.White);
		
		selectedMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		selectedMat.setFloat("Shininess", 32f);
		selectedMat.setBoolean("UseMaterialColors", true);
		selectedMat.setColor("Ambient", ColorRGBA.Black);
		selectedMat.setColor("Diffuse", new ColorRGBA(1f, 0f,0f, 0.7f));
		selectedMat.setColor("Specular", ColorRGBA.White);
		
		nodeModel = assetManager.loadModel("assets/Models/node.j3o");
		nodeModel.setMaterial(nodeMat);
		
		hyperion = assetManager.loadFont("assets/Fonts/Hyperion.fnt");
		
		arrowBody = assetManager.loadModel("assets/Models/arrowBody.j3o");
		arrowHead = assetManager.loadModel("assets/Models/arrowHead.j3o");
		
		arrow = assetManager.loadModel("assets/Models/arrow.j3o");

	}
}
