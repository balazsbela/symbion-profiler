package org.balazsbela.symbion.visualizer.presentation;

import javax.vecmath.Matrix3d;

import jme3test.app.state.RootNodeState;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;

public class Arrow {
	
	Node sceneNode;
	Node pivot;
	Spatial arrowModel;
		
	
	public Arrow(FunctionNode start,FunctionNode end,Vector3f d) {
		Node arrow = new Node(start.getLabelText() + "->" + end.getLabelText());

//		Spatial arrowBody = ResourceManager.arrowBody.clone();
//		arrowBody.setMaterial(ResourceManager.nodeMat);
//		arrowBody.move(new Vector3f(3.0f, -0.2f, 0f));
//
//		arrowBody.scale(1.0f, 2.0f, 1.0f);
//		
//		Spatial arrowHead = ResourceManager.arrowHead.clone();
//		arrowHead.setMaterial(ResourceManager.nodeMat);
//		arrowHead.move(new Vector3f(7.4f, 0.28f, 0.335f));

		arrowModel = ResourceManager.arrow.clone();
		arrowModel.setMaterial(ResourceManager.arrowMat.clone());
		
		arrow.attachChild(arrowModel);			
		sceneNode = arrow;
		Node startNode = (Node) end.getSceneNode().getChild(0);
		Node endNode = (Node) end.getSceneNode().getChild(0);
	
		//arrowModel.lookAt(end.getSceneNode().getLocalTranslation(), Vector3f.UNIT_Y);
		arrowModel.lookAt(d.mult(Visualizer.EXPAND_COEFICIENT), Vector3f.UNIT_Y);
//		arrowModel.scale(1.0f,1.0f,4.2f);
				
		
	}

	public Node getSceneNode() {
		return sceneNode;
	}

	public Spatial getArrowModel() {
		return arrowModel;
	}
	
	public void setColor(Material mat) {
		arrowModel.setMaterial(mat);
	}
}
