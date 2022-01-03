import gewebe.*; 
import org.sunflow.*; 

BVHViewer[] mBVHViewer;

void settings() {
    size(1280, 720, P3D);
}

void setup() {
    final String mTempDirectory = System.getProperty("user.dir");
    mBVHViewer = new BVHViewer[3];
    mBVHViewer[0] = new BVHViewer(loadStrings(mTempDirectory + "/out/production/07_11.bvh"));
    mBVHViewer[1] = new BVHViewer(loadStrings(mTempDirectory + "/out/production/09_06.bvh"));
    mBVHViewer[2] = new BVHViewer(loadStrings(mTempDirectory + "/out/production/C_test.bvh"));
}

void draw() {
    background(50);
    /* camera */
    float mCos = cos(millis() / 5000.f);
    float mSin = sin(millis() / 5000.f);
    camera(width / 4.f + width / 4.f * mCos + 200, height / 2.0f - 100, 550 + 150 * mSin, width / 2.0f,
           height / 2.0f, -400, 0, 1, 0);
    /* ground */
    fill(255);
    stroke(127);
    line(width / 2.0f, height / 2.0f, -30, width / 2.0f, height / 2.0f, 30);
    stroke(127);
    line(width / 2.0f - 30, height / 2.0f, 0, width / 2.0f + 30, height / 2.0f, 0);
    stroke(255);
    pushMatrix();
    translate(width / 2.0f, height / 2.0f - 10, 0);
    scale(-1, -1, -1);
    /* model */
    for (BVHViewer b : mBVHViewer) {
        b.update(millis());
        b.draw(g);
    }
    popMatrix();
}
