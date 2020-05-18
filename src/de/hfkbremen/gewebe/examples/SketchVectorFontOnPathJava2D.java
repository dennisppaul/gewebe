package de.hfkbremen.gewebe.examples;

import de.hfkbremen.gewebe.VectorFont;
import processing.core.PApplet;

public class SketchVectorFontOnPathJava2D extends PApplet {

    /**
     * this example demonstrates how to arrange characters along a given path and return the result as a collection of
     * vertices using Java2D.
     */

    private java.awt.Shape mCharacters;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        VectorFont mPathCreator = new VectorFont("Helvetica", 32);
        mPathCreator.insideFlag(VectorFont.CLOCKWISE);
        mPathCreator.outline_flatness(0.25f);
        mPathCreator.stretch_to_fit(true);
        mPathCreator.repeat(false);

        final float mRadius = 550;
        final String mText = "Since I was very young I realized I never wanted to be human size. ";
        final java.awt.geom.Ellipse2D.Float mPath = new java.awt.geom.Ellipse2D.Float();
        mPath.x = width / 2.0f - mRadius / 2;
        mPath.y = height / 2.0f - mRadius / 2;
        mPath.width = mRadius;
        mPath.height = mRadius;
        mCharacters = mPathCreator.charactersJAVA2D(mText, mPath);
    }

    public void draw() {
        background(50);
        drawOutline(mCharacters);
    }

    private void drawOutline(java.awt.Shape pShape) {
        stroke(255, 127);
        noFill();

        if (pShape != null) {
            final java.awt.geom.PathIterator it = pShape.getPathIterator(null, 1.0f);
            int type;
            float[] points = new float[6];
            beginShape(POLYGON);
            while (!it.isDone()) {
                type = it.currentSegment(points);
                vertex(points[0], points[1]);
                if (type == java.awt.geom.PathIterator.SEG_CLOSE) {
                    endShape(CLOSE);
                    beginShape();
                }
                it.next();
            }
            endShape();
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchVectorFontOnPathJava2D.class.getName());
    }
}
