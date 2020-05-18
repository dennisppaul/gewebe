import de.hfkbremen.gewebe.*; 
import org.sunflow.*; 


boolean mRecord = false;
void settings() {
    size(1024, 768, P3D);
}
void setup() {
}
void draw() {
    background(255);
    if (mRecord) {
        beginRaw(OBJWriter.name(), "output" + frameCount + ".obj");
    }
    /* lines */
    stroke(0);
    for (int i = 0; i < 50; i++) {
        strokeWeight(random(1, 20));
        line(random(width),
             random(height),
             random(-height, height),
             random(height),
             random(width),
             random(-height, height));
    }
    /* triangles */
    fill(0, 127, 255);
    noStroke();
    translate(0, height / 2);
    for (int i = 0; i < 10; i++) {
        translate(64, 0);
        sphere(random(32, 64));
    }
    if (mRecord) {
        endRaw();
        mRecord = false;
    }
}
void keyPressed() {
    if (key == ' ') {
        mRecord = true;
    }
}
