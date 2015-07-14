package org.balazsbela.symbion.visualizer.presentation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.balazsbela.symbion.models.Function;
import org.balazsbela.symbion.visualizer.models.FunctionModel;

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
	private FunctionModel model;
	private FunctionNode parent;
	private Node children;
	private Node arrows;
	private String idString;
	private Material normalColor = ResourceManager.nodeMat.clone();

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	public FunctionNode(String methodName, String labelText,FunctionModel model) {
		this.idString = methodName;		
		this.labelText = labelText;
		this.model = model;
		children = new Node(methodName + " children");
		arrows = new Node(methodName + " arrows");

		sceneNode = new Node(methodName);
		Node nodeModel = (Node) ResourceManager.nodeModel.clone();
		nodeModel.setName(methodName);

		Spatial spatial = nodeModel.getChild(0);
		geometry = (Geometry) (((Node) spatial).getChild(0));
		geometry.setName(methodName);

		BitmapText label = new BitmapText(ResourceManager.hyperion);
		//label.setText(labelText);
		int pos =labelText.indexOf("(");
		if(pos>0) {
			labelText = labelText.substring(0,pos);
		}
		label.setText(labelText);
	//	label.setColor(new ColorRGBA(232f / 256.0f, 227f / 256f, 130f / 256f, 0.7f));
		label.setColor(Visualizer.getResourceManager().getColor(model.getClassName()));
		label.setBox(new Rectangle(-1.0f, 0, 20f, 2f));
		label.setLocalTranslation(-0.4f, -1.3f, 0.0f);
		label.setSize(1f);

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
		
		System.out.println("Color for:"+model.getClassName());
		normalColor = Visualizer.getResourceManager().getExtColor(model.getClassName());
		setNodeColor(normalColor);
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

	public void toggleColor() {
		if (isExpanded) {
		    getGeometry().setMaterial(ResourceManager.expandedMaterial.clone());
		} else {
			setNodeColor(normalColor);
		}
	}
	
	public void setSelectedColor() {
		geometry.setMaterial(ResourceManager.selectedMaterial);
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

	public FunctionModel getFunctionModel() {
		return model;
	}

	public void setFunctionModel(FunctionModel model) {
		this.model = model;		
	}

	public String getIdString() {
		return idString;
	}

	public void setIdString(String methodName) {
		this.idString = methodName;
	}

	public FunctionNode getParent() {
		return parent;
	}

	public void setParent(FunctionNode parent) {
		this.parent = parent;
	}

	public void setNodeColor(Material material) {		
		geometry.setMaterial(material);
	}
	
	public void setArrowColor(Material material) {
		arrows.setMaterial(material);
	}

	public Material getNormalColor() {
		return normalColor;
	}
}
