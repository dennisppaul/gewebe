package gewebe.sandbox;

import gewebe.Mesh;
import gewebe.ModelData;
import gewebe.ModelLoaderOBJ;
import gewebe.OBJTetrahedron;
import processing.core.PApplet;

public class SketchMeshAccessingVertices extends PApplet {

    private ModelData mModelData;
    private Mesh      mMesh;
    private int       i;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        mModelData = ModelLoaderOBJ.parseModelData(OBJTetrahedron.DATA);
        mMesh      = mModelData.mesh();
        println(mModelData);
    }

    public void draw() {
        background(50);

        translate(width / 2.0f, height / 2.0f, -200);
        rotateX(map(mouseX, 0, width, 0, TWO_PI));
        rotateY(map(mouseY, 0, height, 0, TWO_PI));

        noFill();
        stroke(255, 31);
        mMesh.draw(g);

        stroke(255);
        beginShape(TRIANGLES);
        vertex(
                mModelData.vertices()[i + 0],
                mModelData.vertices()[i + 1],
                mModelData.vertices()[i + 2]
              );
        vertex(
                mModelData.vertices()[i + 3],
                mModelData.vertices()[i + 4],
                mModelData.vertices()[i + 5]
              );
        vertex(
                mModelData.vertices()[i + 6],
                mModelData.vertices()[i + 7],
                mModelData.vertices()[i + 8]
              );
        endShape();
    }

    public void mousePressed() {
        i += 9;
        i %= mModelData.vertices().length;
        System.out.println("+++ triangle: " + i / 9);
    }

    public static void main(String[] args) {
        PApplet.main(SketchMeshAccessingVertices.class.getName());
    }
}
