package gewebe.simulators;

import processing.core.PApplet;
import processing.core.PGraphics;

public abstract class TV extends OffscreenContext {

    public TV(PApplet pParent) {
        super(pParent);
    }

    public abstract void settings();

    public abstract void setup(PGraphics graphics);

    public abstract void draw(PGraphics graphics);

    public void drawTV(PGraphics g) {
        g.pushMatrix();

        g.rotateX(PApplet.PI / 2);
        g.translate(0, texture().height / 2, 0);
        g.image(texture(), 0, 0);

        g.fill(0);
        g.stroke(255);
        final int mDepth = 40;
        g.translate(0, 0, mDepth / 2);
        final int mPadding = 2;
        g.box(texture().width + mPadding, texture().height + mPadding, mDepth - 1.0f);

        g.popMatrix();
    }
}
