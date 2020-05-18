import de.hfkbremen.gewebe.*; 
import org.sunflow.*; 


/**
 * this example demonstrates how to create an *ico sphere* as an *indexed triangle list*. an *indexed
 * triangle list* allows to use the same vertex for multiple triangles.
 */
IndexedTriangleList mIndexedTriangleList;
void settings() {
    size(1024, 768, P3D);
}
void setup() {
    hint(ENABLE_DEPTH_SORT);
    mIndexedTriangleList = IcoSphere.indexed_triangle_list(2);
}
void draw() {
    background(50);
    strokeWeight(1.0f / 100.0f);
    translate(width / 2.0f, height / 2.0f);
    rotateX(frameCount / 180.0f);
    rotateY(0.33f * frameCount / 180.0f);
    scale(100);
    beginShape(TRIANGLES);
    for (int i = 0; i < mIndexedTriangleList.indices.size(); i++) {
        int mIndex = mIndexedTriangleList.indices.get(i);
        int mHighlight = ((frameCount / 3) % mIndexedTriangleList.indices.size()) / 3;
        stroke(255, 127);
        if (i / 3 == mHighlight) {
            fill(0, 127, 255, 127);
        } else {
            fill(255, 32);
        }
        PVector p = mIndexedTriangleList.vertices.get(mIndex);
        vertex(p.x, p.y, p.z);
    }
    endShape();
    // or for short `mIndexedTriangleList.draw(g);`
}
