import de.hfkbremen.gewebe.*; 
import org.sunflow.*; 


/**
 * this example demonstrates how to manually access the triangles of a mesh.
 */
ArrayList<Triangle> mTriangles;
void settings() {
    size(1024, 768, P3D);
}
void setup() {
    ModelData mModelData = ModelLoaderOBJ.parseModelData(OBJWeirdObject.DATA);
    Mesh mModelMesh = mModelData.mesh();
    mTriangles = mModelMesh.triangles();
}
void draw() {
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
