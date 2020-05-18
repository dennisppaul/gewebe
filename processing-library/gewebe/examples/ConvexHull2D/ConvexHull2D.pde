import de.hfkbremen.gewebe.*; 
import org.sunflow.*; 


/**
 * this example demonstrate how to find the *convex hull* around a set of 2D points.
 */
final ArrayList<PVector> mPoints = new ArrayList();
void settings() {
    size(1024, 768, P3D);
}
void setup() {
    noFill();
}
void draw() {
    if (mousePressed) {
        mPoints.add(new PVector(mouseX, mouseY));
    }
    background(50);
    stroke(255, 127);
    for (PVector p : mPoints) {
        cross(p, 3.0f);
    }
    stroke(255, 127, 0);
    ArrayList<PVector> mHull = MeshUtil.giftWrap(mPoints);
    beginShape();
    for (PVector p : mHull) {
        vertex(p.x, p.y, p.z);
    }
    endShape(CLOSE);
}
void cross(PVector pPosition, float pDiameter) {
    line(pPosition.x + pDiameter, pPosition.y + pDiameter, pPosition.x - pDiameter, pPosition.y - pDiameter);
    line(pPosition.x - pDiameter, pPosition.y + pDiameter, pPosition.x + pDiameter, pPosition.y - pDiameter);
}
