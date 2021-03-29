package gewebe.simulators;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public abstract class OffscreenContext {

    public int width;
    public int height;

    protected PGraphics graphics;

    protected final PApplet parent;

    private boolean mIsSetupCalled;

    public OffscreenContext(PApplet pParent) {
        parent = pParent;
        mIsSetupCalled = false;
        settings();
    }

    public final void size(int pWidth, int pHeight) {
        graphics = parent.createGraphics(pWidth, pHeight); // defaults to JAVA2D
        width = pWidth;
        height = pHeight;
    }

    public abstract void settings();

    public abstract void setup(PGraphics canvas);

    public abstract void draw(PGraphics canvas);

    public void update() {
        if (graphics != null) {
            pre_draw();
            if (!mIsSetupCalled) {
                setup(graphics);
                mIsSetupCalled = true;
            }
            draw(graphics);
            post_draw();
        } else {
            System.err.println("### warning @" + getClass().getName() + " / PGraphics has not been initialized. make sure to call `size(int, int`) in `settings()`");
        }
    }

    public PImage texture() {
        return graphics;
    }

    private void post_draw() {
        graphics.endDraw();
    }

    private void pre_draw() {
        graphics.beginDraw();
    }
}
