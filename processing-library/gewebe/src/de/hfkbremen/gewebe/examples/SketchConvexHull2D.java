package de.hfkbremen.gewebe.examples;

import de.hfkbremen.gewebe.MeshUtil;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class SketchConvexHull2D extends PApplet {

    /**
     * this example demonstrate how to find the *convex hull* around a set of 2D points.
     */

    private final ArrayList<PVector> mPoints = new ArrayList<>();

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        noFill();
    }

    public void draw() {
        if (mousePressed) {
            mPoints.add(new PVector(mouseX, mouseY));
        }

        background(50);

        stroke(255, 127);
        for (PVector p : mPoints) {
            cross(p, 3.0f);
        }

        stroke(255, 127, 0);
        ArrayList<PVector> mHull = MeshUtil.giftWrap(mPoints);
        beginShape();
        for (PVector p : mHull) {
            vertex(p.x, p.y, p.z);
        }
        endShape(CLOSE);
    }

    private void cross(PVector pPosition, float pDiameter) {
        line(pPosition.x + pDiameter, pPosition.y + pDiameter, pPosition.x - pDiameter, pPosition.y - pDiameter);
        line(pPosition.x - pDiameter, pPosition.y + pDiameter, pPosition.x + pDiameter, pPosition.y - pDiameter);
    }

    public static void main(String[] args) {
        PApplet.main(SketchConvexHull2D.class.getName());
    }
}
