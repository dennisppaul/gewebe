package gewebe.sandbox;

import gewebe.PLYWriter;
import processing.core.PApplet;
import processing.core.PVector;

public class TestPLYWriter extends PApplet {

    private boolean mRecord = false;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void draw() {
        background(50);
        directionalLight(126, 126, 126, 0, 0, -1);
        ambientLight(102, 102, 102);

        if (mRecord) {
            beginRaw(PLYWriter.class.getName(), "/Users/dennisppaul/Desktop/foobar" + frameCount + ".ply");
        }

        translate(width / 2.0f, height / 2.0f);
        rotateX(frameCount * 0.01f);
        rotateY(frameCount * 0.003f);

        /* lines */
        stroke(255, 127, 0);
        noFill();

        strokeWeight(190);
        line(-width * 0.25f, 0, 0, width * 0.25f, 0, 0);

        if (mRecord) {
            endRaw();
            mRecord = false;
            println("+++ exported model to " + sketchPath());
        }
    }

    public void keyPressed() {
        if (key == ' ') {
            mRecord = true;
        }
    }

    private void vertex(PVector p) {
        vertex(p.x, p.y, p.z);
    }

    private PVector sphere_coord(float pAzimut, float pInclination, float pRadius) {
        return new PVector(cos(pAzimut) * sin(pInclination) * pRadius,
                           sin(pAzimut) * sin(pInclination) * pRadius,
                           cos(pInclination) * pRadius);
    }

    public static void main(String[] args) {
        PApplet.main(TestPLYWriter.class.getName());
    }
}
