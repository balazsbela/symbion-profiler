package org.balazsbela.symbion.visualizer;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

/**
 * test
 * @author normenhansen
 */
public class Robot extends SimpleApplication {

    public static void main(String[] args) {
        Robot app = new Robot();
        app.start();
    }

    Node head;
    Node neck;
    Node torso;
    Node rightUpperArm;
    Node leftUpperArm;
    Node rightLowerArm;
    Node leftLowerArm;
    Node leftUpperLeg;
    Node rightUpperLeg;
    Node leftLowerLeg;
    Node rightLowerLeg;
    Node leftLeg;
    Node rightLeg;
    
    
    @Override
    public void simpleInitApp() {
        this.flyCam.setMoveSpeed(20);

        initRobot();  
        initFloor();
    }
    
    float angle = FastMath.PI/8;
    
    int dirRight = -1;
    int dirLeft = 1;
    
    Geometry torosGeom;
    
    @Override
    public void simpleUpdate(float tpf) {
        
        float animSpeed = 0.8f * tpf;
        
        //swing legs from 0 - Pi/4
        // change direction at every Pi/4
        if ( angle > FastMath.PI/4 ) {
            angle = 0;
            dirRight *= -1;
            dirLeft *= -1;
        } else {
           
            // simple leg swing
            leftUpperLeg.rotate(dirLeft * animSpeed, 0, 0);
            rightUpperLeg.rotate(dirRight * animSpeed, 0, 0);

            // adjust legs so they dont leave the body
            //leftUpperLeg.move(0,0,dirLeft * animSpeed * 2f);
            //rightUpperLeg.move(0,0,dirRight * animSpeed * 2f);

            
            // knee bending at the following quarters
            // 0 - Pi/16 HERE Pi*2/16 HERE Pi*3/16 - Pi/4
            if ( (angle < FastMath.PI*2/16 ) && (angle > FastMath.PI/16)) {
                
                
                // reverse direction of rotation
                leftLowerLeg.rotate(dirLeft* -1 * animSpeed , 0 ,0);
                rightLowerLeg.rotate(dirRight * animSpeed  , 0 ,0);
                
                //adjust knee position
                //leftLowerLeg.move(0, 0 ,dirLeft * animSpeed );
                //rightLowerLeg.move(0, 0 , dirRight * - 1 * animSpeed );
                
            } else if ( (angle < FastMath.PI*3/16 ) && (angle > FastMath.PI*2/16) ) {
                // same backwards
                leftLowerLeg.rotate(dirLeft * animSpeed  , 0 ,0);
                rightLowerLeg.rotate(dirRight*-1 * animSpeed  , 0 ,0);   
              
                //leftLowerLeg.move(0, 0 ,dirLeft * -1 * animSpeed);
                //rightLowerLeg.move(0, 0 , dirRight * animSpeed );
            }
            
            // torso swing
            torosGeom.rotate(0, dirLeft * animSpeed * 0.4f, 0);
            neck.rotate(0, dirLeft * animSpeed * 0.4f, 0);
            rightUpperArm.rotate(0, dirLeft * animSpeed * 0.4f, 0);
            leftUpperArm.rotate(0, dirLeft * animSpeed * 0.4f, 0);   
            
            angle += animSpeed;
        }
        torso.move(0, 0, animSpeed * 8);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void initRobot() {
        
        Material redUnshaded = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        redUnshaded.setFloat("Shininess", 32f);
        redUnshaded.setBoolean("UseMaterialColors", true);
        redUnshaded.setColor("Ambient", ColorRGBA.Black);
        redUnshaded.setColor("Diffuse", ColorRGBA.Red);
        redUnshaded.setColor("Specular", ColorRGBA.White);
        //redUnshaded.setColor("Color", ColorRGBA.Red);
   
        Material greenUnshaded = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        greenUnshaded.setFloat("Shininess", 32f);
        greenUnshaded.setBoolean("UseMaterialColors", true);
        greenUnshaded.setColor("Ambient", ColorRGBA.Black);
        greenUnshaded.setColor("Diffuse", ColorRGBA.Green);
        greenUnshaded.setColor("Specular", ColorRGBA.White);        
        //greenUnshaded.setColor("Color", ColorRGBA.Green);
        
        Material yellowUnshaded = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        yellowUnshaded.setFloat("Shininess", 32f);
        yellowUnshaded.setBoolean("UseMaterialColors", true);
        yellowUnshaded.setColor("Ambient", ColorRGBA.Black);
        yellowUnshaded.setColor("Diffuse", ColorRGBA.Yellow);
        yellowUnshaded.setColor("Specular", ColorRGBA.White); 
        //yellowUnshaded.setColor("Color", ColorRGBA.Yellow); 
        
        Box torsoBox = new Box(Vector3f.ZERO , 2.5f , 4 , 1);
        torosGeom = new Geometry("torso",torsoBox);
        torosGeom.setMaterial(greenUnshaded);
        torso = new Node("torsoNode");
        torso.attachChild(torosGeom);

        Box neckBox = new Box(new Vector3f(0,4.4f,0) , 0.4f , 0.7f , 0.4f);
        Geometry neckGeom = new Geometry("neck",neckBox);
        neckGeom.setMaterial(yellowUnshaded);
        neck = new Node("neckNode");
        neck.attachChild(neckGeom);
        torso.attachChild(neck);        
        
        Box headBox = new Box(new Vector3f(0,6.1f,0) , 1 , 1 , 1);
        Geometry headGeom = new Geometry("head",headBox);
        headGeom.setMaterial(redUnshaded);
        head = new Node("headNode");
        head.attachChild(headGeom);
        neck.attachChild(head);
        
        Box luArmBox = new Box(new Vector3f(3.3f,2.2f,0) , 0.7f , 1.7f , 1);
        Geometry luArmGeom = new Geometry("leftUpperArm",luArmBox);
        luArmGeom.setMaterial(yellowUnshaded);
        leftUpperArm = new Node("leftUpperArmNode");
        leftUpperArm.attachChild(luArmGeom);
        torso.attachChild(leftUpperArm);
        
        
        Box ruArmBox = new Box(new Vector3f(-3.3f,2.2f,0) , 0.7f , 1.7f , 1);
        Geometry ruArmGeom = new Geometry("rightUpperArm",ruArmBox);
        ruArmGeom.setMaterial(yellowUnshaded);
        rightUpperArm = new Node("rightUpperArmNode");
        rightUpperArm.attachChild(ruArmGeom);
        torso.attachChild(rightUpperArm);      
        
        Box llArmBox = new Box(new Vector3f(3.3f,-1.1f,0) , 0.5f , 1.5f , 0.5f);
        Geometry llArmGeom = new Geometry("leftLowerArm",llArmBox);
        llArmGeom.setMaterial(redUnshaded);
        leftLowerArm = new Node("leftLowerArmNode");
        leftLowerArm.attachChild(llArmGeom);
        leftUpperArm.attachChild(leftLowerArm);        
        
        Box rlArmBox = new Box(new Vector3f(-3.3f,-1.1f,0) , 0.5f , 1.5f , 0.5f);
        Geometry rlArmGeom = new Geometry("rightLowerArm",rlArmBox);
        rlArmGeom.setMaterial(redUnshaded);
        rightLowerArm = new Node("rightLowerArmNode");
        rightLowerArm.attachChild(rlArmGeom);
        rightUpperArm.attachChild(rightLowerArm);            
        
        //Box luLegBox = new Box(new Vector3f(1.5f,-6.1f,0) , 1f , 2f , 1);
        Box luLegBox = new Box(new Vector3f(0,-2f,0) , 1f , 2f , 1);
        Geometry luLegGeom = new Geometry("leftUpperLeg",luLegBox);
        luLegGeom.setMaterial(yellowUnshaded);
        leftUpperLeg = new Node("leftUpperLegNode");
        leftUpperLeg.attachChild(luLegGeom);
        torso.attachChild(leftUpperLeg);        
        leftUpperLeg.setLocalTranslation(1.5f,-4.1f,0);
 
        //Box ruLegBox = new Box(new Vector3f(-1.5f,-6.1f,0) , 1f , 2f , 1);
        Box ruLegBox = new Box(new Vector3f(0,-2f,0) , 1f , 2f , 1);
        Geometry ruLegGeom = new Geometry("rightUpperLeg",ruLegBox);
        ruLegGeom.setMaterial(yellowUnshaded);
        rightUpperLeg = new Node("rightUpperLegNode");
        rightUpperLeg.attachChild(ruLegGeom);
        torso.attachChild(rightUpperLeg);        
        rightUpperLeg.setLocalTranslation(-1.5f,-4.1f,0);
        
        //Box llLegBox = new Box(Vector3f.ZERO , 0.8f , 2f , 0.8f);
        Box llLegBox = new Box(new Vector3f(0,-2,0) , 0.8f , 2f , 0.8f);
        Geometry llLegGeom = new Geometry("leftLowerLeg",llLegBox);
        llLegGeom.setMaterial(redUnshaded);
        leftLowerLeg = new Node("leftLowerLegNode");
        leftLowerLeg.attachChild(llLegGeom);
        leftUpperLeg.attachChild(leftLowerLeg); 
        leftLowerLeg.setLocalTranslation(0f,-4f,0);
        
        //Box rlLegBox = new Box(Vector3f.ZERO , 0.8f , 2f , 0.8f);
        Box rlLegBox = new Box(new Vector3f(0,-2,0) , 0.8f , 2f , 0.8f);
        Geometry rlLegGeom = new Geometry("rightLowerLeg",rlLegBox);
        rlLegGeom.setMaterial(redUnshaded);
        rightLowerLeg = new Node("rightLowerLegNode");
        rightLowerLeg.attachChild(rlLegGeom);
        rightUpperLeg.attachChild(rightLowerLeg);  
        rightLowerLeg.setLocalTranslation(0f,-4f,0);
        
        Box lLegBox = new Box(new Vector3f(0,-4.3f,1f) , 0.8f , 0.2f , 2f);
        Geometry lLegGeom = new Geometry("leftLeg",lLegBox);
        lLegGeom.setMaterial(greenUnshaded);
        leftLeg = new Node("leftLegNode");
        leftLeg.attachChild(lLegGeom);
        leftLowerLeg.attachChild(leftLeg); 
        
        Box rLegBox = new Box(new Vector3f(0,-4.3f,1f) , 0.8f , 0.2f , 2f);
        Geometry rLegGeom = new Geometry("rightLeg",rLegBox);
        rLegGeom.setMaterial(greenUnshaded);
        rightLeg = new Node("rightLegNode");
        rightLeg.attachChild(rLegGeom);
        rightLowerLeg.attachChild(rightLeg);         
        
        
        rootNode.attachChild(torso);
    }

    private void initFloor() {
        
        Material floorMap = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        
//        Texture floorDiffuse = assetManager.loadTexture("Textures/Diffuse_example.jpg");
//        Texture floorNormal = assetManager.loadTexture("Textures/Normal_example.jpg");
//        
//        floorDiffuse.setWrap(Texture.WrapMode.Repeat);
//        floorNormal.setWrap(Texture.WrapMode.Repeat);
        
//        floorMap.setTexture("DiffuseMap", floorDiffuse);
//        floorMap.setTexture("NormalMap", floorNormal);
        floorMap.setFloat("Shininess", 4f);
        
        Box floorBox = new Box(new Vector3f(0,-13f,0) , 500f , 0.2f , 500f);
        floorBox.scaleTextureCoordinates(new Vector2f(50,50));
        Geometry floorGeom = new Geometry("floor",floorBox);
        floorGeom.setMaterial(floorMap);
        rootNode.attachChild(floorGeom);
        
        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.White);
        rootNode.addLight(light);
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-60,0,-100).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
    
        DirectionalLight sun2 = new DirectionalLight();
        sun2.setDirection(new Vector3f(60,0,-100).normalizeLocal());
        sun2.setColor(ColorRGBA.White);
        rootNode.addLight(sun2);        
        
    }
    
}
