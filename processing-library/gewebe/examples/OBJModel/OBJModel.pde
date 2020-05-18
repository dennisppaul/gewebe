import de.hfkbremen.gewebe.*; 
import org.sunflow.*; 


/**
 * this example demonstrates how to load and draw a mesh from model data. note, that model data can also be loaded
 * from external sources ( e.g hard drive or web ) with `loadStrings()`.
 */
Mesh mModelMesh;
void settings() {
    size(1024, 768, P3D);
}
void setup() {
    ModelData mModelData = ModelLoaderOBJ.parseModelData(OBJMan.DATA);
    mModelMesh = mModelData.mesh();
    println(mModelData);
}
void draw() {
    background(50);
    lights();
    lightFalloff(1.0f, 0.001f, 0.0f);
    pointLight(255, 127, 0, width / -2.0f, height / 2.0f, 0);
    if (mousePressed) {
        noFill();
        stroke(255);
        mModelMesh.primitive(POINTS);
    } else {
        fill(255, 127, 0);
        stroke(255);
        mModelMesh.primitive(TRIANGLES);
    }
    translate(width / 2.0f, height - 125, -200);
    rotateX(PI);
    rotateY(cos(frameCount * 0.0037f) * TWO_PI);
    mModelMesh.draw(g);
}
