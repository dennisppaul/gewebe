package gewebe.examples.technique;

import gewebe.Gewebe;
import gewebe.PointSprites;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PGL;

public class SketchPointSprites extends PApplet {

    public void settings() {
        size(1024, 768, P3D);
    }

    PointSprites fPointSprites;
    PVector[]    fPoints;

    public void setup() {
        size(1024, 768, P3D);
        hint(DISABLE_DEPTH_TEST);
        blendMode(ADD);

        /* create array of points */
        fPoints = new PVector[100000];
        for (int i = 0; i < fPoints.length; i++) {
            fPoints[i] = new PVector(random(-200, 200), random(-200, 200), random(-200, 200));
        }

        /* create point sprites */
        PGL pgl = beginPGL();
        fPointSprites = new PointSprites(this, pgl, fPoints,
                                         sketchPath("../../../../resources/sprite.png"),
                                         sketchPath("../../../../resources/point_sprite_vert.glsl"),
                                         sketchPath("../../../../resources/point_sprite_frag.glsl"));
        endPGL();
    }

    public void draw() {
        background(0);

        /* rotate view */
        translate(width * 0.5f, height * 0.5f);
        rotateX(frameCount * 0.003f);
        rotateY(frameCount * 0.02f);

        /* draw a *normal* shape */
        fill(255);
        noStroke();
        sphere(100);

        /* move point sprites */
        if (mousePressed) {
            for (int i = 0; i < fPoints.length; i++) {
                final float mOffset = 10;
                fPoints[i].x += random(-mOffset, mOffset);
                fPoints[i].y += random(-mOffset, mOffset);
                fPoints[i].z += random(-mOffset, mOffset);
            }
            fPointSprites.update(); // call this when point locations have changed
        }

        /* set point sprite size */
        fPointSprites.set_point_size(map(mouseX, 0, width, 0.1f, 64));

        /* draw point sprite */
        PGL pgl = beginPGL();
        fPointSprites.draw(pgl);
        endPGL();
    }

    public static void main(String[] args) {
        Gewebe.run_sketch_with_resources(SketchPointSprites.class);
    }
}
