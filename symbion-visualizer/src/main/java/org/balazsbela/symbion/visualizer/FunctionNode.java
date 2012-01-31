package org.balazsbela.symbion.visualizer;

import java.util.Map;

import com.jme3.collision.CollisionResult;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;

public class FunctionNode {
	
	private Node sceneNode;
	private String labelText;
	private boolean isExpanded = false;
	private Geometry geometry;
	
	private Node children;
	private Node arrows;
	
	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}
	
	public FunctionNode(String labelText) {
		this.labelText = labelText;
		children = new Node(labelText+" children");
		arrows = new Node(labelText+" arrows");
		
		sceneNode = new Node(labelText);
		Node nodeModel = (Node) ResourceManager.nodeModel.clone();
		nodeModel.setName(labelText);	
		
		Spatial spatial = nodeModel.getChild(0);
		geometry = (Geometry)(((Node)spatial).getChild(0));
		geometry.setName(labelText);
				
		
		BitmapText label = new BitmapText(ResourceManager.hyperion);
		label.setText(labelText);
		label.setColor(new ColorRGBA(232f / 256.0f, 227f / 256f, 130f / 256f, 0.7f));
		label.setBox(new Rectangle(-1.0f, 0, 10f, 2f));
		label.setLocalTranslation(-0.4f, -1.3f, 0.0f);
		label.setSize(0.2f);

		sceneNode.attachChild(nodeModel);
		sceneNode.attachChild(label);
		children.attachChild(arrows);
		sceneNode.attachChild(children);
		sceneNode.setName(labelText);

		BillboardControl bc = new BillboardControl();
		bc.setAlignment(BillboardControl.Alignment.Camera);
		bc.setEnabled(true);
		nodeModel.addControl(bc);

		BillboardControl bc1 = new BillboardControl();
		bc1.setAlignment(BillboardControl.Alignment.Camera);
		bc1.setEnabled(true);
		label.addControl(bc1);

	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;		
		toggleColor();
	}

	
	
	public Geometry getGeometry() {
		return geometry;
	}
	
	private void toggleColor() {
		if(isExpanded) {
			//getGeometry().setMaterial(ResourceManager.selectedMat);		
			getGeometry().getMaterial().setColor("Diffuse" , new ColorRGBA(1f, 0f,0f, 0.7f));
		}
		else {
			//getGeometry().setMaterial(ResourceManager.nodeMat);
			getGeometry().getMaterial().setColor("Diffuse" , new ColorRGBA(232f / 256.0f, 227f / 256f, 130f / 256f, 0.7f));
		}
	}

	public Node getSceneNode() {
		return sceneNode;
	}

	public Node getChildren() {
		return children;
	}

	public void setChildren(Node children) {
		this.children = children;
	}

	public Node getArrows() {
		return arrows;
	}

	public void undoExpansion() {
		arrows.detachAllChildren();
		children.detachAllChildren();
		children.attachChild(arrows);		
	}
	


	
	
}
