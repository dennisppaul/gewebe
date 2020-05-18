import de.hfkbremen.gewebe.*; 
import org.sunflow.*; 


/**
 * this example demonstrates how to convert characters a collection of triangles.
 */
VectorFont mPathCreator;
void settings() {
    size(1024, 768, P3D);
}
void setup() {
    mPathCreator = new VectorFont("Helvetica", 200);
}
void draw() {
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
