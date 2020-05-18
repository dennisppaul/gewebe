package de.hfkbremen.gewebe.examples;

import de.hfkbremen.gewebe.CGALBooleanOperations3;
import de.hfkbremen.gewebe.Mesh;
import de.hfkbremen.gewebe.ModelLoaderOBJ;
import de.hfkbremen.gewebe.OBJWeirdObject;
import processing.core.PApplet;

public class SketchBooleanOperations extends PApplet {

    /**
     * @TODO(20200518, "Java-C/C++-Binding is currently broken")
     * this example demonstrates how to use [Boolean Operations](https://en.wikipedia.org/wiki/Boolean_operations_on_polygons)
     * or [Constructive Solid Geometry (CSG)](https://en.wikipedia.org/wiki/Constructive_solid_geometry) using a
     * Java-C/C++-Binding to [CGAL](https://www.cgal.org/).
     */

    private Mesh mSolidA;

    private Mesh mSolidB;

    private Mesh mIntersection;

    private CGALBooleanOperations3 mBooleanOperation;

    private float mRotation;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
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

    public void draw() {
        background(255);
        translate(width / 2.0f, height / 2.0f);
        rotateY(mRotation += 1 / 30f * 0.5f);

        if (mIntersection != null) {
            stroke(255);
            fill(0, 127, 255);
            mIntersection.draw(g);
        }

        noFill();
        if (mIntersection == null) {
            stroke(255, 0, 0, 128);
        } else {
            stroke(255, 0, 0, 32);
        }
        mSolidA.draw(g);
        if (mIntersection == null) {
            stroke(0, 255, 0, 128);
        } else {
            stroke(0, 255, 0, 32);
        }
        mSolidB.draw(g);
    }

    public void keyPressed() {
        if (key == '1') {
            mIntersection = mBooleanOperation.boolean_operation_mesh(CGALBooleanOperations3.INTERSECTION,
                                                                     mSolidB.vertices(),
                                                                     mSolidA.vertices());
        } else if (key == '2') {
            mIntersection = mBooleanOperation.boolean_operation_mesh(CGALBooleanOperations3.JOIN, //
                                                                     mSolidB.vertices(), //
                                                                     mSolidA.vertices());
        } else if (key == '3') {
            mIntersection = mBooleanOperation.boolean_operation_mesh(CGALBooleanOperations3.DIFFERENCE,
                                                                     mSolidB.vertices(),
                                                                     mSolidA.vertices());
        } else if (key == '4') {
            mIntersection = mBooleanOperation.boolean_operation_mesh(CGALBooleanOperations3.DIFFERENCE,
                                                                     mSolidA.vertices(),
                                                                     mSolidB.vertices());
        } else if (key == ' ') {
            mIntersection = null;
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchBooleanOperations.class.getName()});
    }
}
