package de.hfkbremen.gewebe.examples;

import de.hfkbremen.gewebe.ArcBall;
import de.hfkbremen.gewebe.Line3;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class SketchConvertLineToTriangles extends PApplet {

    /**
     * this examples demonstrates how to convert a line ( of 2 or more points ) into a single 3D object or a series of
     * unconnected 3D objects.
     */

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        ArcBall.setupRotateAroundCenter(this, false);
    }

    public void draw() {
        background(50);
        lights();
        lightFalloff(1.0f, 0.001f, 0.0f);
        pointLight(255, 127, 0, 50, 50, 50);
        translate(width / 2.0f, height / 2.0f);

        noStroke();
        fill(255, 127, 0);
        drawSpiral();
    }

    private void drawSpiral() {
        float mRadius = 200;
        final float mLineWidth = 8;
        final int mStep = 12;
        final int mHeightInc = 9;

        ArrayList<PVector> mVertices = new ArrayList<>();
        for (int i = -360 * 3; i < 360 * 3; i += mStep) {
            float r = radians(i);
            PVector p = new PVector(sin(r) * mRadius, cos(r) * mRadius, mHeightInc * i / (float) mStep);
            mVertices.add(p);
        }

        /* convert line to triangles */
        ArrayList<PVector> mTriangles;
        if (mousePressed) {
            mTriangles = Line3.triangles_continuous(mVertices, mLineWidth, true, null);
        } else {
            mTriangles = Line3.triangles(mVertices, mLineWidth, true, null);
        }
        drawTriangles(mTriangles);
    }

    private void drawTriangles(ArrayList<PVector> pTriangles) {
        beginShape(TRIANGLES);
        for (PVector p : pTriangles) {
            vertex(p.x, p.y, p.z);
        }
        endShape();
    }

    public static void main(String[] args) {
        PApplet.main(SketchConvertLineToTriangles.class.getName());
    }
}
