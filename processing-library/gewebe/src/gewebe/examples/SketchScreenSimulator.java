package gewebe.examples;

import gewebe.OffscreenContext;
import processing.core.PApplet;
import processing.core.PGraphics;

public class SketchScreenSimulator extends PApplet {

    /**
     * this example shows how to draw into an @{@link gewebe.OffscreenContext} and use it as an image.
     */

    private MyScreen mScreen;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        mScreen = new MyScreen(this);
    }

    public void draw() {
        mScreen.update();

        background(50);

        // draw thumbnail
        image(mScreen.texture(), 10, 10, 64 * 2, 48 * 2);

        // draw a plane in space
        translate(mScreen.width / -2.0f, mScreen.height / -2.0f, 0);
        rotateX(sin(frameCount * 0.033f) * 0.1f);
        rotateY(sin(frameCount * 0.05f) * 0.25f);
        translate(0, 0, 50);
        image(mScreen.texture(), mouseX, mouseY);
    }

    static class MyScreen extends OffscreenContext {

        public MyScreen(PApplet pParent) {
            super(pParent);
        }

        public void settings() {
            size(640, 480);
        }

        public void setup(PGraphics graphics) {
            /* call processing methods with a `parent.` e.g: */
            System.out.println("screen height is " + parent.height);

            /* call processing drawing methods with a `graphics.` e.g: */
            graphics.background(255);
        }

        public void draw(PGraphics graphics) {
            graphics.stroke(0);
            float mY = parent.random(0, graphics.height);
            graphics.line(parent.random(0, graphics.width), mY, parent.random(0, graphics.width), mY);
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchScreenSimulator.class.getName());
    }
}
