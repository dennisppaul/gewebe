import de.hfkbremen.gewebe.*; 
import org.sunflow.*; 


/**
 * this example demonstrates how to create an *ico sphere* as a mesh.
 */
Mesh mMesh;
void settings() {
    size(1024, 768, P3D);
}
void setup() {
    mMesh = IcoSphere.mesh(5);
}
void draw() {
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
