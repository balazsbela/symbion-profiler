package org.balazsbela.symbion.visualizer.presentation;
import java.util.HashMap;
import java.util.Map;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

public class ResourceManager {
	private AssetManager assetManager;

	static Material nodeMat;
	static Material arrowMat;
	static Material expandedMaterial;
	static Material selectedMaterial;
	static Spatial nodeModel;
	static BitmapFont hyperion;
	static Spatial arrowBody;
	static Spatial arrowHead;
	static Spatial arrow;

	private Map<String, Material> extensionMaterials = new HashMap<>();
	private Map<String, ColorRGBA> classColors = new HashMap<>();

	public ResourceManager(AssetManager assetManager) {
		nodeMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		nodeMat.setFloat("Shininess", 50f);
		nodeMat.setBoolean("UseMaterialColors", true);
		nodeMat.setColor("Ambient", ColorRGBA.Black);
		nodeMat.setColor("Diffuse", new ColorRGBA(232f / 256.0f, 227f / 256f, 130f / 256f, 0.7f));
		nodeMat.setColor("Specular", ColorRGBA.White);

		arrowMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		arrowMat.setFloat("Shininess", 50f);
		arrowMat.setBoolean("UseMaterialColors", true);
		arrowMat.setColor("Ambient", ColorRGBA.Black);
		arrowMat.setColor("Diffuse", new ColorRGBA(232f / 256.0f, 227f / 256f, 130f / 256f, 0.7f));
		arrowMat.setColor("Specular", ColorRGBA.White);

		expandedMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		expandedMaterial.setFloat("Shininess", 32f);
		expandedMaterial.setBoolean("UseMaterialColors", true);
		expandedMaterial.setColor("Ambient", ColorRGBA.Black);
		expandedMaterial.setColor("Diffuse", new ColorRGBA(1f, 0f, 0f, 0.7f));
		expandedMaterial.setColor("Specular", ColorRGBA.White);

		selectedMaterial = nodeMat.clone();
		selectedMaterial.setColor("Diffuse", new ColorRGBA(256f / 256.0f, 135f / 256f, 0f / 256f, 0.7f));

		nodeModel = assetManager.loadModel("assets/Models/node.j3o");
		nodeModel.setMaterial(nodeMat.clone());

		hyperion = assetManager.loadFont("assets/Fonts/Hyperion.fnt");

		arrowBody = assetManager.loadModel("assets/Models/arrowBody.j3o");
		arrowHead = assetManager.loadModel("assets/Models/arrowHead.j3o");

		arrow = assetManager.loadModel("assets/Models/arrow.j3o");

		this.assetManager = assetManager;
	}

	public Material getExtColor(String ext) {
		Material extMat = extensionMaterials.get(ext);
		ColorRGBA color = classColors.get(ext);

		if (ext == "Start Node") {
			return nodeMat.clone();
		}

		if (extMat == null) {
			String className = "";

			extMat = nodeMat.clone();
			ext = ext.trim();
			className = ext;
			if (className.indexOf("$") != -1) {
				className = className.substring(0, className.indexOf("$"));
			}

			color = getColor(className);
			extMat.setColor("Diffuse", color);

			classColors.put(className, color);
			extensionMaterials.put(className, extMat);
		}
		return extMat;
	}

	public ColorRGBA getColor(String ext) {

		ColorRGBA defaultColor = classColors.get(ext);

		if (ext.equals("Start node")) {
			return new ColorRGBA(new ColorRGBA(232f / 256.0f, 227f / 256f, 130f / 256f, 0.7f));
		}

		if (defaultColor == null) {
			ext = ext.trim();
			String className = ext;
			if (className.indexOf("$") != -1) {
				className = className.substring(0, className.indexOf("$"));
			}			
						
			String hexColor = String.format("%06X", className.hashCode());
			
			System.out.println("Generated hex "+ className+" "+hexColor);
			if(hexColor.length()>=6) {
				hexColor=hexColor.substring(0,6);
				ColorRGBA chosen = colorFromHex(hexColor);
				classColors.put(className, chosen);
				return chosen;
			}
			
			//Fallback algorithm
			
			System.out.println("Tokenizing "+className);
			String[] tokenization = className.split("\\.");
			
			System.out.println(tokenization.length);
			if(tokenization.length<3) {
				ext = className;
			}
			else {
				ext = tokenization[tokenization.length-3]+"."+tokenization[tokenization.length-2]+"."+tokenization[tokenization.length-1];
			}
			System.out.println("Color calc for "+ext);
			
			int hash = 1;
			for (char c : ext.toCharArray()) {
				if (c != 0 && hash * c != 0) {
					hash *= c;
				}
			}

			hash = Math.abs(hash);
			if (hash < 0) {
				hash = Math.abs(hash + hash / 2);
			}

			if (hash != 0) {

				int comp1 = hash % 100;
				hash = hash / 10;
				int comp2 = hash % 100;
				hash = hash / 10;
				int comp3 = hash % 100;

				int max = comp1 > comp2 ? comp1 : comp2;
				max = max > comp3 ? max : comp3;
				max += 20;

				float red = (float) comp1 / (float) max;
				float green = (float) comp2 / (float) max;
				float blue = (float) comp3 / (float) max;
				ColorRGBA color = new ColorRGBA(red, green, blue, 1.0f);				
				classColors.put(className, color);
				return color;
			}
			else {
				defaultColor = new ColorRGBA(232f / 256.0f, 227f / 256f, 130f / 256f, 0.7f);
			}
		}		
		return defaultColor;
	}
	

	private ColorRGBA colorFromHex(String hexColor) {
		int red = Integer.parseInt(hexColor.substring(0,2),16);
		int green = Integer.parseInt(hexColor.substring(2,4),16);
		int blue = Integer.parseInt(hexColor.substring(4,6),16);				
		
		System.out.println("Generated color from hashcode "+ red+" "+green+" "+blue);
		ColorRGBA color = new ColorRGBA(red/255.0f,green/255.0f,blue/255.0f,1.0f);
		return color;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public Map<String, Material> getExtensionMaterials() {
		return extensionMaterials;
	}

	public Map<String, ColorRGBA> getClassColors() {
		return classColors;
	}
}
