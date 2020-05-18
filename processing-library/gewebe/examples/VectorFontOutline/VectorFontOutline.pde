import de.hfkbremen.gewebe.*; 
import org.sunflow.*; 


/**
 * this example demonstrates how to create a path from the outlines of a font.
 */
VectorFont mPathCreator;
void settings() {
    size(1024, 768, P3D);
}
void setup() {
    mPathCreator = new VectorFont("Helvetica", 500);
}
void draw() {
    mPathCreator.outline_flatness((float) mouseX / (float) width * 5);
    ArrayList<PVector> mVertices = mPathCreator.outline("23");
    background(50);
    noStroke();
    fill(255);
    translate(250, height - 225);
    for (int i = 0; i < mVertices.size(); i++) {
        PVector p = mVertices.get(i);
        final float mRadius = sin(radians(i * 10 + frameCount * 3)) * 2 + 4;
        ellipse(p.x, p.y, mRadius * 2, mRadius * 2);
    }
}
