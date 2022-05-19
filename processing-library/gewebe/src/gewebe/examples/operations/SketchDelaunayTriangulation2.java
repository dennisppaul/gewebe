package gewebe.examples.operations;

import gewebe.DelaunayTriangulation;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class SketchDelaunayTriangulation2 extends PApplet {

    public ArrayList<PVector> mVertices;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        stroke(50, 91);
        fill(255);
        mVertices = new ArrayList<>();
    }

    public void draw() {
        background(50);

        if (mousePressed) {
            addVertices(mouseX, mouseY, random(20, 100), random(2, 5));
        }

        float[] mTriangles = DelaunayTriangulation.triangulate(mVertices);
        beginShape(TRIANGLES);
        for (int i = 0; i < mTriangles.length; i += 3) {
            vertex(mTriangles[i + 0], mTriangles[i + 1], mTriangles[i + 2]);
        }
        endShape();
    }

    public void keyPressed() {
        if (key == ' ') {
            mVertices.clear();
        }
    }

    private void addVertices(float pXOffset, float pYOffset, float pRadius, float pSteps) {
        final float mSteps = TWO_PI / pSteps;
        final float mOffset = 5;
        final float mROffset = random(TWO_PI);
        for (float r = 0; r < TWO_PI; r += mSteps) {
            final float x = sin(r + mROffset) * pRadius + pXOffset + random(-mOffset, mOffset);
            final float y = cos(r + mROffset) * pRadius + pYOffset + random(-mOffset, mOffset);
            mVertices.add(new PVector(x, y));
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchDelaunayTriangulation2.class.getName()});
    }
}
