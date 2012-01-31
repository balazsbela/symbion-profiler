package org.balazsbela.symbion.visualizer;

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

		Spatial arrowModel = ResourceManager.arrow.clone();
		arrowModel.setMaterial(ResourceManager.nodeMat);
		
		arrow.attachChild(arrowModel);			
		sceneNode = arrow;

		arrowModel.lookAt(end.getSceneNode().getWorldTranslation(), end.getSceneNode().getWorldRotation().getRotationColumn(1));
		arrowModel.scale(1.0f,1.0f,4.0f);
		
//		Vector3f v1 = start.getSceneNode().getChild(0).getWorldTranslation();
//		Vector3f v2 = end.getSceneNode().getChild(0).getWorldTranslation();
//		arrowModel.setLocalRotation(Utils.getRotationTo(v1, v2, Vector3f.ZERO));
		
	}

	public Node getSceneNode() {
		return sceneNode;
	}
}
