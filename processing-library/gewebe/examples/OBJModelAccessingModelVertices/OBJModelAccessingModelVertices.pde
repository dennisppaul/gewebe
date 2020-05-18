import de.hfkbremen.gewebe.*; 
import org.sunflow.*; 


/**
 * this example demonstrates how to manually access and draw the vertices of a 3D model.
 */
ModelData mModelData;
void settings() {
    size(1024, 768, P3D);
}
void setup() {
    mModelData = ModelLoaderOBJ.parseModelData(OBJWeirdObject.DATA);
    println(mModelData);
}
void draw() {
    background(50);
    translate(width / 2.0f, height / 2.0f, -200);
    rotateX(sin(frameCount * 0.001f) * TWO_PI);
    rotateY(cos(frameCount * 0.0037f) * TWO_PI);
    stroke(255, 16);
    strokeWeight(3.0f);
    noFill();
    for (int i = 0; i < mModelData.vertices().length; i += 3) {
        float x = mModelData.vertices()[i + 0];
        float y = mModelData.vertices()[i + 1];
        float z = mModelData.vertices()[i + 2];
        line(x, y, z, mouseX - width / 2.0f, mouseY - height / 2.0f, 0);
    }
}
