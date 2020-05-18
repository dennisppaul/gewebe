package de.hfkbremen.gewebe.examples;

import de.hfkbremen.gewebe.Mesh;
import de.hfkbremen.gewebe.MeshUtil;
import de.hfkbremen.gewebe.ModelData;
import de.hfkbremen.gewebe.ModelLoaderOBJ;
import de.hfkbremen.gewebe.OBJWeirdObject;
import de.hfkbremen.gewebe.Triangle;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class SketchOBJModelFindPointsInside extends PApplet {

    /**
     * this example demonstrates how test if a point is inside of a mesh.
     */

    private static PVector mCenterOfMass;
    PVector mPoint = new PVector();
    private ArrayList<Triangle> mTriangles;
    private ArrayList<PVector> mPoints;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        ModelData mModelData = ModelLoaderOBJ.parseModelData(OBJWeirdObject.DATA);
        Mesh mModelMesh = mModelData.mesh();
        mTriangles = mModelMesh.triangles();
        mCenterOfMass = mModelMesh.calcCenterOfMass();
        mPoints = new ArrayList<>();
    }

    public void draw() {
        background(50);
        prepareView();
        if (!mousePressed) { drawMesh(); }
        queryPointPosition();
        drawPointsInside();
    }

    public void queryPointPosition() {
        /* is random point inside mesh? */

        final float r = 400;
        mPoint.x = random(-r, r);
        mPoint.y = random(-r, r);
        mPoint.z = random(-r, r);

        final PVector mDirection = PVector.sub(mCenterOfMass, mPoint);
        if (MeshUtil.isPointInsideMesh(mTriangles, mPoint, mDirection)) {
            mPoints.add(new PVector().set(mPoint));
        }
    }

    private void prepareView() {
        translate(width / 2.0f, height / 2.0f, -200);
        rotateX(sin(frameCount * 0.01f) * TWO_PI);
        rotateY(cos(frameCount * 0.0037f) * TWO_PI);
    }

    private void drawMesh() {
        stroke(255, 31);
        noFill();
        beginShape(TRIANGLES);
        for (Triangle t : mTriangles) {
            vertex(t.a.x, t.a.y, t.a.z);
            vertex(t.b.x, t.b.y, t.b.z);
            vertex(t.c.x, t.c.y, t.c.z);
        }
        endShape();
    }

    private void drawPointsInside() {
        for (PVector p : mPoints) {
            noStroke();
            fill(0, 127, 255);
            pushMatrix();
            translate(p.x, p.y, p.z);
            sphere(10);
            popMatrix();
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchOBJModelFindPointsInside.class.getName());
    }
}
