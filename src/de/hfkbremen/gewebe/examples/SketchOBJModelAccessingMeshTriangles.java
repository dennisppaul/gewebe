package de.hfkbremen.gewebe.examples;

import de.hfkbremen.gewebe.Mesh;
import de.hfkbremen.gewebe.ModelData;
import de.hfkbremen.gewebe.ModelLoaderOBJ;
import de.hfkbremen.gewebe.OBJWeirdObject;
import de.hfkbremen.gewebe.Triangle;
import processing.core.PApplet;

import java.util.ArrayList;

public class SketchOBJModelAccessingMeshTriangles extends PApplet {

    /**
     * this example demonstrates how to manually access the triangles of a mesh.
     */

    private ArrayList<Triangle> mTriangles;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        ModelData mModelData = ModelLoaderOBJ.parseModelData(OBJWeirdObject.DATA);
        Mesh mModelMesh = mModelData.mesh();
        mTriangles = mModelMesh.triangles();
    }

    public void draw() {
        background(50);

        translate(width / 2.0f, height / 2.0f, -200);
        rotateX(sin(frameCount * 0.01f) * TWO_PI);
        rotateY(cos(frameCount * 0.0037f) * TWO_PI);

        stroke(255);
        beginShape(TRIANGLES);
        for (Triangle t : mTriangles) {
            final float mRandom = random(255);
            fill(0, mRandom / 2, mRandom);
            vertex(t.a.x, t.a.y, t.a.z);
            vertex(t.b.x, t.b.y, t.b.z);
            vertex(t.c.x, t.c.y, t.c.z);
        }
        endShape();
    }

    public static void main(String[] args) {
        PApplet.main(SketchOBJModelAccessingMeshTriangles.class.getName());
    }
}
