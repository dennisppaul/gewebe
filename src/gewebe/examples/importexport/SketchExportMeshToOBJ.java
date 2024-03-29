package gewebe.examples.importexport;

import gewebe.IcoSphere;
import gewebe.Mesh;
import gewebe.ModelLoaderOBJ;
import processing.core.PApplet;

public class SketchExportMeshToOBJ extends PApplet {

    /*
     * this example demonstrates how to export a mesh as an OBJ file.
     */

    private Mesh mMesh;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        mMesh = IcoSphere.mesh(5);
    }

    public void draw() {
        background(50);
        stroke(255, 63);
        noFill();

        translate(width / 2.0f, height / 2.0f);
        rotateX(frameCount / 180.0f);
        rotateY(0.33f * frameCount / 180.0f);
        scale(height / 3.0f);
        strokeWeight(3.0f / height); // @NOTE(this is necessary to *unscale* the stroke weight )

        mMesh.draw(g);
    }

    public void keyPressed() {
        String[] mOBJ = ModelLoaderOBJ.convertMeshToOBJ(mMesh);
        saveStrings("icosphere" + frameCount + ".obj", mOBJ);
        println("+++ exported model to " + sketchPath());
    }

    public static void main(String[] args) {
        PApplet.main(SketchExportMeshToOBJ.class.getName());
    }
}
