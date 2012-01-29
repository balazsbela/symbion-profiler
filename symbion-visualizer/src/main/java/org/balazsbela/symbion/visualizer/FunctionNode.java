package org.balazsbela.symbion.visualizer;

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
	
	public Node getSceneNode() {
		return sceneNode;
	}

	public void setSceneNode(Node sceneNode) {
		this.sceneNode = sceneNode;
	}

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}
	
	public FunctionNode(String labelText) {
		this.labelText = labelText;
		sceneNode = new Node(labelText);
		Node nodeModel = (Node) ResourceManager.nodeModel.clone();
		nodeModel.setName(labelText);	
		for(Spatial sp : nodeModel.getChildren()) {
			sp.setName(labelText);
		}
		
		BitmapText label = new BitmapText(ResourceManager.hyperion);
		label.setText(labelText);
		label.setColor(new ColorRGBA(232f / 256.0f, 227f / 256f, 130f / 256f, 0.7f));
		label.setBox(new Rectangle(-1.0f, 0, 10f, 2f));
		label.setLocalTranslation(-0.4f, -1.3f, 0.0f);
		label.setSize(0.2f);

		sceneNode.attachChild(nodeModel);
		sceneNode.attachChild(label);
		sceneNode.setName(labelText);
		sceneNode.move(new Vector3f(10.0f, 0.0f, 0.0f));

		BillboardControl bc = new BillboardControl();
		bc.setAlignment(BillboardControl.Alignment.Camera);
		bc.setEnabled(true);
		nodeModel.addControl(bc);

		BillboardControl bc1 = new BillboardControl();
		bc1.setAlignment(BillboardControl.Alignment.Camera);
		bc1.setEnabled(true);
		label.addControl(bc1);

	}

	
	
}
