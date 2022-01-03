import gewebe.*; 
import org.sunflow.*; 
/*
 * this example demonstrates how to use [Boolean Operations](https://en.wikipedia
 * .org/wiki/Boolean_operations_on_polygons) or [Constructive Solid Geometry (CSG)](https://en.wikipedia
 * .org/wiki/Constructive_solid_geometry) using a Java-C/C++-Binding to [CGAL](https://www.cgal.org/).
 * <p>
 * note that this sketch currently only runs on macOS.
 */

CGALBooleanOperations3 mBooleanOperation;

Mesh mIntersection;

float mRotation;

Mesh mSolidA;

Mesh mSolidB;

void settings() {
    size(1024, 768, P3D);
}

void setup() {
    mSolidA = ModelLoaderOBJ.parseModelData(OBJWeirdObject.DATA).mesh();
    mSolidA.translate(100, 0, 0);
    mSolidB = ModelLoaderOBJ.parseModelData(OBJWeirdObject.DATA).mesh();
    mBooleanOperation = new CGALBooleanOperations3();
    float SCALER = 1.0f;
    for (int i = 0; i < mSolidB.vertices().length; i++) {
        mSolidB.vertices()[i] *= SCALER;
    }
    for (int i = 0; i < mSolidA.vertices().length; i++) {
        mSolidA.vertices()[i] *= SCALER;
    }
}

void draw() {
    background(50);
    directionalLight(126, 126, 126, 0, 0, -1);
    ambientLight(102, 102, 102);
    translate(width / 2.0f, height / 2.0f);
    rotateY(mRotation += 1 / 30f * 0.5f);
    if (mIntersection != null) {
        noStroke();
        fill(255);
        mIntersection.draw(g);
    }
    noFill();
    if (mIntersection == null) {
        stroke(255, 127, 0, 64);
    } else {
        stroke(255, 127, 0, 32);
    }
    mSolidA.draw(g);
    if (mIntersection == null) {
        stroke(0, 127, 255, 64);
    } else {
        stroke(0, 127, 255, 32);
    }
    mSolidB.draw(g);
}

void keyPressed() {
    if (key == '1') {
        mIntersection = mBooleanOperation.boolean_operation_mesh(CGALBooleanOperations3.INTERSECTION,
                                                                 mSolidB.vertices(), mSolidA.vertices());
    } else if (key == '2') {
        mIntersection = mBooleanOperation.boolean_operation_mesh(CGALBooleanOperations3.JOIN, //
                                                                 mSolidB.vertices(), //
                                                                 mSolidA.vertices());
    } else if (key == '3') {
        mIntersection = mBooleanOperation.boolean_operation_mesh(CGALBooleanOperations3.DIFFERENCE,
                                                                 mSolidB.vertices(), mSolidA.vertices());
    } else if (key == '4') {
        mIntersection = mBooleanOperation.boolean_operation_mesh(CGALBooleanOperations3.DIFFERENCE,
                                                                 mSolidA.vertices(), mSolidB.vertices());
    } else if (key == ' ') {
        mIntersection = null;
    }
}
