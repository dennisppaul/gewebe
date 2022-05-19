package gewebe.examples;

import gewebe.CGALAlphaShape3;
import processing.core.PApplet;
import processing.core.PVector;

public class SketchAlphaShape3Optimal extends PApplet {

    /*
     * this example demonstrates how to use [Alpha Shapes](https://en.wikipedia.org/wiki/Alpha_shape) using a
     * Java-C/C++-Binding to [CGAL](https://www.cgal.org/).
     * <p>
     * note that this sketch currently only runs on macOS.
     */

    private CGALAlphaShape3 mCGAL;
    private int mNumberOfSolidComponents = 1;
    private float[] mOptimalAlphaShape;
    private float[] mPoints3;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        mCGAL = new CGALAlphaShape3();

        final int NUMBER_OF_POINTS = 1000;
        mPoints3 = new float[NUMBER_OF_POINTS * 3];
        final float mRange = 2;
        for (int i = 0; i < NUMBER_OF_POINTS; i++) {
            PVector p = new PVector().set(random(-1, 1), random(-1, 1), random(-1, 1));
            p.normalize();
            p.mult(random(mRange * 0.75f, mRange));
            mPoints3[i * 3 + 0] = p.x;
            mPoints3[i * 3 + 1] = p.y;
            mPoints3[i * 3 + 2] = p.z;
        }
        mCGAL.compute_cgal_alpha_shape(mPoints3);
        computeAlphaShape(mNumberOfSolidComponents);
    }

    public void draw() {
        background(50);
        directionalLight(126, 126, 126, 0, 0, -1);
        ambientLight(102, 102, 102);

        translate(width / 2.0f, height / 2.0f);
        scale(100);
        rotateX(frameCount * 0.01f);
        rotateY(frameCount * 0.003f);

        fill(255);
        noStroke();

        if (mOptimalAlphaShape != null) {
            beginShape(TRIANGLES);
            for (int i = 0; i < mOptimalAlphaShape.length; i += 3) {
                vertex(mOptimalAlphaShape[i + 0], mOptimalAlphaShape[i + 1], mOptimalAlphaShape[i + 2]);
            }
            endShape();
        }

        strokeWeight(1f / 25f);
        stroke(255, 127, 0);
        noFill();
        beginShape(POINTS);
        for (int i = 0; i < mPoints3.length; i += 3) {
            vertex(mPoints3[i + 0], mPoints3[i + 1], mPoints3[i + 2]);
        }
        endShape();
    }

    public void keyPressed() {
        if (key == '+') {
            mNumberOfSolidComponents++;
        }
        if (key == '-') {
            mNumberOfSolidComponents--;
        }
        computeAlphaShape(mNumberOfSolidComponents);
    }

    private void computeAlphaShape(int mNumberOfSolidComponents) {
        System.out.println("+++ number of create components: " + mNumberOfSolidComponents);
        System.out.println("+++ optimal alpha             : " + mCGAL.get_optimal_alpha(mNumberOfSolidComponents));
        mOptimalAlphaShape = mCGAL.compute_regular_mesh_optimal(mNumberOfSolidComponents);
    }

    public static void main(String[] args) {
        PApplet.main(SketchAlphaShape3Optimal.class.getName());
    }
}
