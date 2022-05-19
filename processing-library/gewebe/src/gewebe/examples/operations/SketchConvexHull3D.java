package gewebe.examples.operations;

import gewebe.ConvexHull3;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class SketchConvexHull3D extends PApplet {

    /*
     * this example demonstrate how to find the *convex hull* around a set of 3D points.
     */

    private final ArrayList<PVector> mPoints = new ArrayList<>();

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        noStroke();
        fill(255);
        sphereDetail(8);

        final float mScale = 0.2f;
        for (int i = 0; i < 500; i++) {
            final PVector p = new PVector(random(-width * mScale, width * mScale),
                                          random(-height * mScale, height * mScale),
                                          random(-width * mScale, width * mScale));
            mPoints.add(p);
        }
    }

    public void draw() {
        background(50);
        lights();
        lightFalloff(1.0f, 0.001f, 0.0f);
        pointLight(255, 127, 0, width / -2.0f, height / 2.0f, 0);

        translate(width / 2.0f, height / 2.0f);
        rotateX(frameCount * 0.01f);
        rotateY(frameCount * 0.003f);

        if (mousePressed) {
            drawPoints();
        } else {
            drawHull();
        }
    }

    private void drawPoints() {
        for (PVector p : mPoints) {
            pushMatrix();
            translate(p.x, p.y, p.z);
            sphere(2);
            popMatrix();
        }
    }

    private void drawHull() {
        final float[] mVertices = ConvexHull3.hull_vertices(mPoints);
        beginShape(TRIANGLES);
        for (int i = 0; i < mVertices.length; i += 3) {
            vertex(mVertices[i + 0], mVertices[i + 1], mVertices[i + 2]);
        }
        endShape();
    }

    public static void main(String[] args) {
        PApplet.main(SketchConvexHull3D.class.getName());
    }
}