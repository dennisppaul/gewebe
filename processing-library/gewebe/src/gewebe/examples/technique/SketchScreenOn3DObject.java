package gewebe.examples.technique;

import gewebe.Mesh;
import gewebe.ModelData;
import gewebe.ModelLoaderOBJ;
import gewebe.OBJWeirdObject;
import gewebe.OffscreenContext;
import processing.core.PApplet;
import processing.core.PGraphics;

public class SketchScreenOn3DObject extends PApplet {

    /*
     * this example shows how to draw into an @{@link gewebe.OffscreenContext} and use it as a texture on a 3D model.
     * move mouse to offset texture.
     */

    private Mesh mModelMesh;
    private MyTexture mScreen;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        hint(ENABLE_DEPTH_SORT); /* enable depth sorting when using transparent textures */

        mScreen = new MyTexture(this);

        ModelData mModelData = ModelLoaderOBJ.parseModelData(OBJWeirdObject.DATA);
        mModelMesh = mModelData.mesh();
        mModelMesh.primitive(TRIANGLES);
        mModelMesh.texture_scale().set(mScreen.width * 0.5f,
                                       mScreen.height * 0.5f); /* scale texture coords to image size */
    }

    public void draw() {
        mScreen.update();

        background(50);

        // draw thumbnail
        image(mScreen.texture(), 10, 10, 64 * 2, 48 * 2);

        // draw screen on 3D model
        noStroke();
        translate(width / 2.0f, height / 2.0f, 0);
        rotateX(PI);
        rotateY(cos(frameCount * 0.0037f) * TWO_PI);
        mModelMesh.texture_offset().set(mouseX, mouseY - height / 2.0f); /* move texture on 3D model */
        mModelMesh.draw(g, mScreen.texture());
    }

    public void keyPressed() {
        mScreen.texture().background(255, 0);
    }

    static class MyTexture extends OffscreenContext {

        public MyTexture(PApplet pParent) {
            super(pParent);
        }

        public void settings() {
            size(640, 480);
        }

        public void setup(PGraphics graphics) {
            graphics.background(255, 0);
        }

        public void draw(PGraphics graphics) {
            graphics.noFill();
            graphics.stroke(255);
            final float mPadding = 20;
            float mY = parent.random(mPadding, graphics.height - mPadding);
            graphics.line(parent.random(mPadding, graphics.width - mPadding), mY,
                          parent.random(mPadding, graphics.width - mPadding), mY);
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchScreenOn3DObject.class.getName());
    }
}
