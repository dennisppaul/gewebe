import gewebe.*; 
import org.sunflow.*; 
/*
 * this example demonstrates how to export a mesh as an OBJ file.
 */
boolean mRecord = false;
float mSegments = 180;
void settings() {
    size(1024, 768, P3D);
}
void draw() {
    background(50);
    directionalLight(126, 126, 126, 0, 0, -1);
    ambientLight(102, 102, 102);
    if (mRecord) {
        beginRaw(OBJWriter.name(), "output" + frameCount + OBJWriter.file_extension());
    }
    translate(width / 2.0f, height / 2.0f);
    rotateX(frameCount * 0.01f);
    rotateY(frameCount * 0.003f);
    /* lines */
    stroke(255);
    noFill();
    PVector p1 = new PVector();
    for (int i = 0; i < mSegments; i++) {
        final float mRatio = TWO_PI * i / mSegments;
        final float mWeight = 1 + 10 * abs(sin(mRatio / 2));
        final float mInclination = mRatio * 0.5f;
        final float mAzimut = mRatio * 10.0f;
        final float mRadius = width * 0.25f;
        strokeWeight(mWeight);
        PVector p0 = sphere_coord(mAzimut, mInclination, mRadius);
        if (i % 2 == 1) {
            line(p0.x, p0.y, p0.z, p1.x, p1.y, p1.z);
        }
        p1.set(p0);
    }
    /* triangles */
    noStroke();
    fill(255);
    beginShape(TRIANGLES);
    for (int i = 0; i < mSegments; i++) {
        final float mRatio = TWO_PI * i / mSegments;
        final float mInclination = mRatio * 0.5f;
        final float mAzimut = mRatio * 10.0f;
        final float mRadius = width * 0.2f;
        final float mOffset = map(mouseX, 0, width, 0.02f, 1.0f);
        vertex(sphere_coord(mAzimut, mInclination, mRadius));
        vertex(sphere_coord(mAzimut + mOffset, mInclination, mRadius));
        vertex(sphere_coord(mAzimut, mInclination + mOffset, mRadius));
    }
    endShape();
    /* complex shapes ( based on triangles ) */
    noStroke();
    fill(255);
    sphere(width * 0.15f);
    if (mRecord) {
        endRaw();
        mRecord = false;
    }
}
void keyPressed() {
    if (key == ' ') {
        mRecord = true;
        println("+++ exported model to " + sketchPath());
    }
}
void vertex(PVector p) {
    vertex(p.x, p.y, p.z);
}
PVector sphere_coord(float pAzimut, float pInclination, float pRadius) {
    return new PVector(cos(pAzimut) * sin(pInclination) * pRadius,
                       sin(pAzimut) * sin(pInclination) * pRadius,
                       cos(pInclination) * pRadius);
}
