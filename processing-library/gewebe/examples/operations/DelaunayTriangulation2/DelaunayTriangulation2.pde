import gewebe.*; 
import org.sunflow.*; 

ArrayList<PVector> mVertices;

void settings() {
    size(1024, 768, P3D);
}

void setup() {
    stroke(50, 91);
    fill(255);
    mVertices = new ArrayList();
}

void draw() {
    background(50);
    if (mousePressed) {
        addVertices(mouseX, mouseY, random(20, 100), random(2, 5));
    }
    float[] mTriangles = DelaunayTriangulation.triangulate(mVertices);
    beginShape(TRIANGLES);
    for (int i = 0; i < mTriangles.length; i += 3) {
        vertex(mTriangles[i + 0], mTriangles[i + 1], mTriangles[i + 2]);
    }
    endShape();
}

void keyPressed() {
    if (key == ' ') {
        mVertices.clear();
    }
}

void addVertices(float pXOffset, float pYOffset, float pRadius, float pSteps) {
    final float mSteps = TWO_PI / pSteps;
    final float mOffset = 5;
    final float mROffset = random(TWO_PI);
    for (float r = 0; r < TWO_PI; r += mSteps) {
        final float x = sin(r + mROffset) * pRadius + pXOffset + random(-mOffset, mOffset);
        final float y = cos(r + mROffset) * pRadius + pYOffset + random(-mOffset, mOffset);
        mVertices.add(new PVector(x, y));
    }
}
