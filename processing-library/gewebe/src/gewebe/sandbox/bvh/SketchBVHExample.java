package gewebe.sandbox.bvh;

import processing.core.PApplet;

public class SketchBVHExample extends PApplet {

    PBvh bvh1;
    PBvh bvh2;
    PBvh bvh3;

    public void settings() {
        size(1280, 720, P3D);
    }

    public void setup() {
        bvh1 = new PBvh(loadStrings("/Users/dennisppaul/dev/gewebe/intellij/out/production/07_11.bvh"));
        bvh2 = new PBvh(loadStrings("/Users/dennisppaul/dev/gewebe/intellij/out/production/09_06.bvh"));
        bvh3 = new PBvh(loadStrings("/Users/dennisppaul/dev/gewebe/intellij/out/production/C_test.bvh"));
    }

    public void draw() {
        background(0);

        // camera
        float mCos = cos(millis() / 5000.f);
        float mSin = sin(millis() / 5000.f);
        camera(width / 4.f + width / 4.f * mCos + 200,
               height / 2.0f - 100,
               550 + 150 * mSin,
               width / 2.0f,
               height / 2.0f,
               -400,
               0,
               1,
               0);

        // ground
        fill(255);
        stroke(127);
        line(width / 2.0f, height / 2.0f, -30, width / 2.0f, height / 2.0f, 30);
        stroke(127);
        line(width / 2.0f - 30, height / 2.0f, 0, width / 2.0f + 30, height / 2.0f, 0);
        stroke(255);
        pushMatrix();
        translate(width / 2.0f, height / 2.0f - 10, 0);
        scale(-1, -1, -1);

        // model
        bvh1.update(millis());
        bvh2.update(millis());
        bvh3.update(millis());
        bvh1.draw();
        bvh2.draw();
        bvh3.draw();
        popMatrix();
    }

    public class PBvh {

        public BVHParser parser;

        public PBvh(String[] data) {
            parser = new BVHParser();
            parser.parse(data);
        }

        public void update(int ms) {
            parser.moveMsTo(ms);//30-sec loop
            parser.update();
        }

        public void draw() {
            fill(255);
            stroke(255);
            for (BVHBone b : parser.getBones()) {
                pushMatrix();
                translate(b.absPos.x, b.absPos.y, b.absPos.z);
                ellipse(0, 0, 2, 2);
                popMatrix();
                if (!b.hasChildren()) {
                    pushMatrix();
                    line(b.absPos.x, b.absPos.y, b.absPos.z, b.absEndPos.x, b.absEndPos.y, b.absEndPos.z);
                    translate(b.absEndPos.x, b.absEndPos.y, b.absEndPos.z);
                    ellipse(0, 0, 5, 5);
                    popMatrix();
                }

            }
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchBVHExample.class.getName());
    }
}