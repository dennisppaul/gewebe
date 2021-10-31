import gewebe.*; 
import org.sunflow.*; 

BVHParser parser;

void settings() {
    size(1280, 720, P3D);
}

void setup() {
    final String mTempDirectory = System.getProperty("user.dir");
    String[] mBVHData = loadStrings(mTempDirectory + "/out/production/C_test.bvh");
    parser = new BVHParser();
    parser.parse(mBVHData);
}

void draw() {
    background(0);
    /* camera */
    camera(300, 0, 200,
           0, 0, 0,
           0, 1, 0);
    /* model */
    parser.moveMsTo(millis());
    parser.update();
    pushMatrix();
//    translate(width / 2.0f, height / 2.0f, 0);
    scale(-1, -1, -1);
    drawBVH(parser);
    popMatrix();
}

void drawBVH(BVHParser pParser) {
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
