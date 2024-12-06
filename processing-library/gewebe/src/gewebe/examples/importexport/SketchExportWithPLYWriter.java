package gewebe.examples.importexport;

import gewebe.PLYWriter;
import processing.core.PApplet;

public class SketchExportWithPLYWriter extends PApplet {

    private boolean mRecord = false;

    public void settings() {
        size(1024, 768, P3D);
    }


    public void setup() {
        sphereDetail(8);
    }

    public void draw() {
        background(50);
        directionalLight(126, 126, 126, 0, 0, -1);
        ambientLight(102, 102, 102);

        if (mRecord) {
            beginRaw(PLYWriter.name(), "output" + frameCount + PLYWriter.file_extension());
        }

        translate(width / 2.0f, height / 2.0f, height / -2.0f);
        rotateX(frameCount * 0.01f);
        rotateY(frameCount * 0.003f);

        float mPadding = 50;
        float mSize    = width * 0.25f;

        /* lines */
        noFill();

        stroke(255, 127, 0);
        strokeWeight(10);
        line(-mSize, mSize, 0, mSize, -mSize, 0);
        stroke(255);
        strokeWeight(1);
        line(mSize, mSize, 0, -mSize, -mSize, 0);

        /* triangles */
        noStroke();
        beginShape(TRIANGLES);
        fill(255);
        vertex(-mSize + mPadding, mSize, 0);
        vertex(mSize - mPadding, mSize, 0);
        fill(255, 127, 0);
        vertex(0, mPadding, 0);
        vertex(0, -mPadding, 0);
        fill(255);
        vertex(mSize - mPadding, -mSize, 0);
        vertex(-mSize + mPadding, -mSize, 0);
        endShape();

        /* complex shapes ( based on triangles ) */
        noStroke();
        fill(255, 127, 0);
        sphere(mPadding);

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

    public static void main(String[] args) {
        PApplet.main(SketchExportWithPLYWriter.class.getName());
    }
}
