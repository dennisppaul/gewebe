package de.hfkbremen.gewebe.examples;

import de.hfkbremen.gewebe.Mesh;
import de.hfkbremen.gewebe.MeshUtil;
import de.hfkbremen.gewebe.VectorFont;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class SketchVectorFontAsMesh extends PApplet {

    /**
     * this example demonstrates how to convert characters a collection of triangles.
     */

    private VectorFont mPathCreator;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        mPathCreator = new VectorFont("Helvetica", 200);
    }

    public void draw() {
        mPathCreator.outline_flatness((float) mouseX / (float) width * 10);
        ArrayList<PVector> mVertices = mPathCreator.vertices("01.01.1970");
        Mesh mMesh = MeshUtil.mesh(mVertices);

        background(50);
        if (mousePressed) {
            fill(255);
            noStroke();
        } else {
            noFill();
            stroke(255);
        }
        translate(15, mouseY);
        mMesh.draw(g);
    }

    public static void main(String[] args) {
        PApplet.main(SketchVectorFontAsMesh.class.getName());
    }
}
