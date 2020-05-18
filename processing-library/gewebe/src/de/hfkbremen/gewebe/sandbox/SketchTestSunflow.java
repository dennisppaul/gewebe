package de.hfkbremen.gewebe.sandbox;

import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;
import processing.core.PApplet;
import de.hfkbremen.gewebe.sandbox.sunflowapiapi.P5SunflowAPIAPI;

public class SketchTestSunflow extends PApplet {

    int sceneWidth = 500;
    int sceneHeight = 500;

    public void settings() {
    }

    public void setup() {
        P5SunflowAPIAPI sunflow = new P5SunflowAPIAPI();
        sunflow.setWidth(sceneWidth);
        sunflow.setHeight(sceneHeight);
        sunflow.setCameraPosition(0, 2.5f, -5);
        sunflow.setCameraTarget(0, 0, 0);
        sunflow.setThinlensCamera("thinLensCamera", 50f, (float) sceneWidth / sceneHeight);
        sunflow.drawPlane("ground", new Point3(0, -3.5f, 0), new Vector3(0, 1, 0));
        sunflow.drawSphereFlake("mySphereFlake", 20, new Vector3(0, 1, 0), 1);
        sunflow.setPathTracingGIEngine(64);
        sunflow.render("/Users/dennisppaul/Desktop" + "/SunflowTestRender.png");
    }

    public void draw() {
    }

    public static void main(String[] args) {
        PApplet.main(SketchTestSunflow.class.getName());
    }
}
