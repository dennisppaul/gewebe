package gewebe;

import processing.core.PApplet;
import processing.core.PGraphics;

public abstract class TV extends OffscreenContext {

    public TV(PApplet pParent) {
        super(pParent);
    }

    public abstract void settings();

    public abstract void setup(PGraphics graphics);

    public abstract void draw(PGraphics graphics);

    public void drawTV(PGraphics g, int mStrokeColor, int mFillColor) {
        g.pushMatrix();

        g.rotateX(PApplet.PI / 2);
        g.translate(0, texture().height / 2.0f, 0);
        g.image(texture(), 0, 0);

        g.fill(mFillColor);
        g.stroke(mStrokeColor);
        final int mDepth = 40;
        g.translate(0, 0, mDepth / 2.0f);
        final int mPadding = 2;
        g.box(texture().width + mPadding, texture().height + mPadding, mDepth - 1.0f);

        g.popMatrix();
    }
}
