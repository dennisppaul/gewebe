import gewebe.*; 
import org.sunflow.*; 
/*
 * this example demonstrates how to render high-resolution images with [Cycles](https://www.cycles-renderer.org/) a
 * physically based renderer included in the [Blender](https://www.blender.org/) project.
 * <p>
 * # NOTES ON USING CYCLES RENDERER
 * <p>
 * - `background()` is ignored. background color must be set manually via `RendererCycles.BACKGROUND_COLOR` - the
 * path to the cycles executable can be changed via `RendererCycles.CYCLES_BINARY_PATH` - sketch can be forced to
 * wait until renderer is finished via `RendererCycles.RENDERING_PROCESS_BLOCKING` - image output file type ( jpg,
 * png, tga ) can be selected via `RendererCycles.OUTPUT_IMAGE_FILE_TYPE` - must only be used with
 * `beginRaw/endRaw`
 * <p>
 * ## KNOWN LIMITATIONS
 * <p>
 * - if image size is not equal to sketch size the viewport is not scaled - `lights()` does not work yet - Cycles
 * materials can not be used - binary is currently only compiled for `macOS 10.15`
 */
boolean mRecord = false;
void settings() {
    size(1024, 768, P3D);
}
void setup() {
    noStroke();
    sphereDetail(12);
}
void draw() {
    String mOutputFile = "";
    if (mRecord) {
        RendererCycles.NUMBER_OF_SAMPLES = 25;
        RendererCycles.OUTPUT_IMAGE_FILE_TYPE = RendererCycles.IMAGE_FILE_TYPE_PNG;
        RendererCycles.RENDERING_PROCESS_BLOCKING = true;
        RendererCycles.DEBUG_PRINT_RENDER_PROGRESS = false;
        RendererCycles.BACKGROUND_COLOR.set(0.5f);
        RendererCycles.RENDER_VIEWPORT_SCALE = 2.0f;
        RendererCycles.KEEP_XML_SCENE_FILE = true;
        mOutputFile = "cycles-" + nf(frameCount, 4);
        beginRaw(createGraphics(width, height, RendererCycles.name(), mOutputFile));
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
        translate(random(-mRange, mRange),
                  random(-mRange, mRange),
                  random(-mRange, mRange)
        );
        sphere(random(10, 30));
        popMatrix();
    }
    noFill();
    stroke(255);
    sphere(100);
    translate(-50, 0);
    sphere(80);
    translate(100, 0);
    sphere(90);
}
