package gewebe.examples.importexport;

import gewebe.BVHBone;
import gewebe.BVHParser;
import processing.core.PApplet;

public class SketchBVH extends PApplet {

    /*
     * this example demonstrates how to play animation from BVH files.
     * for more information on the BVH file format see https://en.wikipedia.org/wiki/Biovision_Hierarchy
     */

    private BVHParser mBVHParser;

    public void settings() {
        size(1280, 720, P3D);
    }

    public void setup() {
        final String mTempDirectory = System.getProperty("user.dir");
        String[]     mBVHData       = loadStrings(mTempDirectory + "/out/production/C_test.bvh");
        mBVHParser = new BVHParser();
        mBVHParser.parse(mBVHData);
    }

    public void draw() {
        background(50);

        /* camera */
        camera(300, 0, 200, 0, 0, 0, 0, 1, 0);

        /* model */
        mBVHParser.moveMsTo(millis());
        mBVHParser.update();

        pushMatrix();
        scale(-1, -1, -1);
        drawBVH(mBVHParser);
        popMatrix();
    }

    public void drawBVH(BVHParser pParser) {
        fill(255);
        stroke(255);
        for (BVHBone b : pParser.getBones()) {
            pushMatrix();
            translate(b.absPos.x, b.absPos.y, b.absPos.z);
            circle(0, 0, 2);
            popMatrix();
            if (!b.hasChildren()) {
                pushMatrix();
                line(b.absPos.x, b.absPos.y, b.absPos.z, b.absEndPos.x, b.absEndPos.y, b.absEndPos.z);
                translate(b.absEndPos.x, b.absEndPos.y, b.absEndPos.z);
                circle(0, 0, 5);
                popMatrix();
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchBVH.class.getName());
    }
}