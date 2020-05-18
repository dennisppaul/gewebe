package de.hfkbremen.gewebe.examples;

import de.hfkbremen.gewebe.ArcBall;
import de.hfkbremen.gewebe.Mesh;
import de.hfkbremen.gewebe.MeshUtil;
import de.hfkbremen.gewebe.ModelData;
import de.hfkbremen.gewebe.ModelLoaderOBJ;
import de.hfkbremen.gewebe.OBJMan;
import de.hfkbremen.gewebe.Triangle;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class SketchOBJTomograph extends PApplet {

    /**
     * this example demonstrates how to cut a slice out of a mesh.
     */

    private ArrayList<Triangle> mTriangles;
    private ArcBall mArcBall;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        ModelData mModelData = ModelLoaderOBJ.parseModelData(OBJMan.DATA);
        Mesh mModelMesh = mModelData.mesh();
        mTriangles = mModelMesh.triangles();
        mArcBall = new ArcBall(this, true);
    }

    public void draw() {
        background(50);

        pushMatrix();
        translate(width * 0.33f, 0, 0);
        mArcBall.update();
        translate(width * 0.5f, height, -200);
        scale(1, -1, 1);

        /* draw mesh */

        stroke(255, 16);
        noFill();
        beginShape(TRIANGLES);
        for (Triangle t : mTriangles) {
            vertex(t.a.x, t.a.y, t.a.z);
            vertex(t.b.x, t.b.y, t.b.z);
            vertex(t.c.x, t.c.y, t.c.z);
        }
        endShape();

        /* get slice */

        final float mTomographRadius = 250;
        final float mHeight = 525.0f - 550.0f * mouseY / (float) height;
        final float mTomographScanPoints = 72;
        ArrayList<PVector> mOutline = scanSlice(mTriangles, mHeight, mTomographScanPoints, mTomographRadius);

        /* draw slice onto mesh */

        stroke(255, 127, 0);
        noFill();
        beginShape();
        for (PVector p : mOutline) {
            vertex(p.x, mHeight, p.y);
        }
        endShape(CLOSE);
        popMatrix();

        /* draw slice in 2D */

        stroke(255, 192, 0);
        fill(255, 127, 0);
        translate(width * 0.33f, height / 2.0f);
        beginShape();
        for (PVector p : mOutline) {
            vertex(p.x, p.y);
        }
        endShape(CLOSE);
    }

    private ArrayList<PVector> scanSlice(ArrayList<Triangle> pTriangles,
                                         float pHeight,
                                         float pScanPoints,
                                         float pRadius) {
        final ArrayList<PVector> mOutline = new ArrayList<>();
        for (float r = 0; r < TWO_PI; r += TWO_PI / pScanPoints) {
            PVector p0 = new PVector(sin(r) * pRadius, pHeight, cos(r) * pRadius);
            PVector p1 = new PVector(sin(r + PI) * pRadius, pHeight, cos(r + PI) * pRadius);
            PVector mResult = new PVector();
            boolean mSuccess = findIntersection(pTriangles, p0, p1, mResult);
            if (mSuccess) {
                mOutline.add(mResult);
            }
        }
        /* convert to 2D */
        final ArrayList<PVector> mOutline2D = new ArrayList<>();
        for (PVector p : mOutline) {
            mOutline2D.add(new PVector(p.x, p.z));
        }
        return MeshUtil.giftWrap(mOutline2D);
    }

    private boolean findIntersection(ArrayList<Triangle> pTriangles, PVector p0, PVector p1, PVector pResult) {
        final PVector pRayOrigin = p1;
        final PVector pRayDirection = PVector.sub(p1, p0);
        for (Triangle t : pTriangles) {
            final PVector mResult = new PVector();
            boolean mSuccess = MeshUtil.findRayTriangleIntersectionPoint(pRayOrigin,
                                                                         pRayDirection,
                                                                         t.a,
                                                                         t.b,
                                                                         t.c,
                                                                         mResult,
                                                                         true);
            if (mSuccess) {
                pResult.set(mResult);
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        PApplet.main(SketchOBJTomograph.class.getName());
    }
}
