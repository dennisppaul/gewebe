import gewebe.*; 
import org.sunflow.*; 
/*
 * this example demonstrates how to arrange characters along a given path and return the result as a collection of
 * triangles.
 */

int mNumberOfTriangles = 0;

VectorFont mPathCreator;

void settings() {
    size(1024, 768, P3D);
}

void setup() {
    mPathCreator = new VectorFont("Helvetica", 32 * 2);
    mPathCreator.insideFlag(VectorFont.CLOCKWISE);
    mPathCreator.stretch_to_fit(true);
    mPathCreator.repeat(false);
}

void draw() {
    background(50);
    /* adjust flatness ( ie resolutions of curves ) */
    final float mOutlineFlatness = abs((float) mouseX / (float) width) * 10 + 0.1f;
    final float mPathFlatness = abs((float) mouseY / (float) height) * 5 + 0.1f;
    mPathCreator.outline_flatness(mOutlineFlatness);
    mPathCreator.path_flatness(mPathFlatness);
    /* create path */
    final ArrayList<PVector> mPath = new ArrayList();
    for (int x = 32; x < width - 32; x += 20) {
        float y = sin(radians(x * 0.5f + frameCount * 5)) * 50 + height / 2.0f;
        mPath.add(new PVector(x, y));
    }
    /* create outlines */
    String mString = "FLATNESS: " + round(mOutlineFlatness) + " = TRIANGLES: " + nf(mNumberOfTriangles, 4);
    final ArrayList<PVector> mTriangles = mPathCreator.vertices(mString, mPath);
    mNumberOfTriangles = mTriangles.size();
    /* toggle fill and wireframe */
    if (mousePressed) {
        stroke(255, 127);
        noFill();
    } else {
        noStroke();
        fill(255);
    }
    /* create and draw trinangles */
    beginShape(TRIANGLES);
    for (PVector myCharacters : mTriangles) {
        vertex(myCharacters.x, myCharacters.y, myCharacters.z);
    }
    endShape();
}
