import gewebe.*; 
import org.sunflow.*; 
/*
 * this example demonstrate how to find the *convex hull* around a set of 3D points.
 */

final ArrayList<PVector> mPoints = new ArrayList();

void settings() {
    size(1024, 768, P3D);
}

void setup() {
    noStroke();
    fill(255);
    sphereDetail(8);
    final float mScale = 0.2f;
    for (int i = 0; i < 500; i++) {
        final PVector p = new PVector(random(-width * mScale, width * mScale),
                                      random(-height * mScale, height * mScale),
                                      random(-width * mScale, width * mScale));
        mPoints.add(p);
    }
}

void draw() {
    background(50);
    lights();
    lightFalloff(1.0f, 0.001f, 0.0f);
    pointLight(255, 127, 0, width / -2.0f, height / 2.0f, 0);
    translate(width / 2.0f, height / 2.0f);
    rotateX(frameCount * 0.01f);
    rotateY(frameCount * 0.003f);
    if (mousePressed) {
        drawPoints();
    } else {
        drawHull();
    }
}

void drawPoints() {
    for (PVector p : mPoints) {
        pushMatrix();
        translate(p.x, p.y, p.z);
        sphere(2);
        popMatrix();
    }
}

void drawHull() {
    final float[] mVertices = ConvexHull3.hull_vertices(mPoints);
    beginShape(TRIANGLES);
    for (int i = 0; i < mVertices.length; i += 3) {
        vertex(mVertices[i + 0], mVertices[i + 1], mVertices[i + 2]);
    }
    endShape();
}
