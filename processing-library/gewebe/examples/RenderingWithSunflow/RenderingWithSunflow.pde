import de.hfkbremen.gewebe.*; 
import org.sunflow.*; 


/**
 * this example demonstrates how to render high-resolution images with the java-based renderer
 * [Sunflow](http://sunflow.sourceforge.net/).
 *
 * # NOTES ON USING SUNFLOW RENDERER
 *
 * - `background()` is ignored. background color must be set manually via `RendererSunflow.BACKGROUND_COLOR`
 * - image output file type ( png, tga ) can be selected via `RendererSunflow.OUTPUT_IMAGE_FILE_TYPE`
 * - must only be used with `beginRaw/endRaw`
 *
 * ## KNOWN LIMITATIONS
 *
 * - floor object is not working as expected. use `camera` + `rect` instead for now
 * - normal export not working
 * - lighting is not working yet ( neither sky nor light )
 * - material not working yet
 */
boolean mRecord = false;
void settings() {
    size(1024, 768, P3D);
}
void setup() {
}
void draw() {
    String mOutputFile = "";
    if (mRecord) {
        RendererSunflow.BACKGROUND_COLOR.set(0.2f);
        RendererSunflow.RENDER_VIEWPORT_SCALE = 2.0f;
        mOutputFile = "sunflow-" + nf(frameCount, 4);
        beginRaw(createGraphics(width, height, RendererSunflow.name(), mOutputFile));
    }
    drawScene();
    if (mRecord) {
        endRaw();
        saveFrame(mOutputFile + ".screen.png");
        mRecord = false;
    }
}
void keyPressed() {
    if (key == ' ') {
        mRecord = true;
    }
}
void drawScene() {
    background(50);
    camera(height / 2.0f, height, width, 0, 0, 0, 0, 1, 0);
    /* floor */
    noStroke();
    fill(200);
    rect(-500, -500, 1000, 1000);
    rotateX(frameCount * 0.007f);
    rotateY(frameCount * 0.013f);
    for (int i = 0; i < 100; i++) {
        pushMatrix();
        noStroke();
        fill(random(255), random(255), random(255));
        final float mRange = 100;
        translate(
                random(-mRange, mRange),
                random(-mRange, mRange),
                random(-mRange, mRange)
                 );
        sphere(random(10, 30));
        popMatrix();
    }
    noStroke();
    fill(255);
    sphere(100);
    translate(-50, 0);
    sphere(80);
    translate(100, 0);
    sphere(90);
}
