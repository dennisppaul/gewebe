package gewebe.examples.application;

import gewebe.ArcBall;
import gewebe.Line3;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class SketchAttractors extends PApplet {

    private static final int ATTRACTOR_LORENZ    = 0;
    private static final int ATTRACTOR_SPROTT    = 1;
    private static final int ATTRACTOR_FOUR_WING = 2;
    private static final int ATTRACTOR_THOMAS    = 3;
    int mCurrentAttractor = ATTRACTOR_LORENZ;

    float x;
    float y;
    float z;

    ArrayList<PVector> mVertices = new ArrayList<>();
    final float mLineWidth = 4;
    boolean mClearVertices = true;

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
        for (int i = 0; i < 5; i++) {
            switch (mCurrentAttractor) {
                case ATTRACTOR_LORENZ:
                    calcLorenzAttractor();
                    break;
                case ATTRACTOR_SPROTT:
                    calcSprottAttractor();
                    break;
                case ATTRACTOR_FOUR_WING:
                    calcFourWingAttractor();
                    break;
                case ATTRACTOR_THOMAS:
                    calcThomasAttractor();
                    break;
            }
        }
        drawAttractor();

        if (mClearVertices) {
            mVertices.clear();
            x              = 0.01f;
            y              = 0;
            z              = 0;
            mClearVertices = false;
        }
    }

    private void calcFourWingAttractor() {
        float dt = 0.1f;
        float a  = 0.2f;
        float b  = 0.01f;
        float c  = -0.4f;

        float dx = a * x + y * z;
        float dy = b * x + c * y - x * z;
        float dz = -z - x * y;

        x += dx * dt;
        y += dy * dt;
        z += dz * dt;

        PVector p = new PVector(x, y, z);
        p.mult(100f);
        mVertices.add(p);
    }

    private void calcSprottAttractor() {
        float dt = 0.01f;
        float a  = 2.07f; // Sprott attractor parameter

        float dx = y + a * x * y + x * z;
        float dy = 1 - a * (x * x) + y * z;
        float dz = x - x * x - y * y;

        x += dx * dt;
        y += dy * dt;
        z += dz * dt;

        PVector p = new PVector(x, y, z);
        p.mult(100);
        mVertices.add(p);
    }

    private void calcLorenzAttractor() {
        float dt = 0.01f;
        float a  = 10;
        float b  = 28;
        float c  = 8.0f / 3.0f;

        float dx = (a * (y - x)) * dt;
        float dy = (x * (b - z) - y) * dt;
        float dz = (x * y - c * z) * dt;

        x += dx;
        y += dy;
        z += dz;

        PVector p = new PVector(x, y, z);
        p.mult(10);
        mVertices.add(p);
    }

    private void calcThomasAttractor() {
        float dt = 0.2f;
        float b  = 0.208186f;

        float dx = sin(y) - b * x;
        float dy = sin(z) - b * y;
        float dz = sin(x) - b * z;

        x += dx * dt;
        y += dy * dt;
        z += dz * dt;

        PVector p = new PVector(x, y, z);
        p.mult(80);
        mVertices.add(p);
    }

    private void drawAttractor() {
        /* convert line to triangles */
        ArrayList<PVector> mTriangles;
        mTriangles = Line3.triangles_continuous(mVertices, mLineWidth, true, null);

        beginShape(TRIANGLES);
        for (PVector pp : mTriangles) {
            vertex(pp.x, pp.y, pp.z);
        }
        endShape();
    }

    public void keyPressed() {
        switch (key) {
            case '1':
                mCurrentAttractor = ATTRACTOR_LORENZ;
                break;
            case '2':
                mCurrentAttractor = ATTRACTOR_SPROTT;
                break;
            case '3':
                mCurrentAttractor = ATTRACTOR_FOUR_WING;
                break;
            case '4':
                mCurrentAttractor = ATTRACTOR_THOMAS;
                break;
        }
        mClearVertices = true;
    }

    public static void main(String[] args) {
        PApplet.main(SketchAttractors.class.getName());
    }
}
