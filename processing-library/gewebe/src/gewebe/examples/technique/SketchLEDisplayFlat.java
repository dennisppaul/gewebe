package gewebe.examples.technique;

import gewebe.ArcBall;
import gewebe.LEDisplay;
import processing.core.PApplet;
import processing.core.PGraphics;

public class SketchLEDisplayFlat extends PApplet {

    private ArcBall   mArcBall;
    private PGraphics mDisplayContent;
    private LEDisplay mLEDisplay;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        mArcBall = new ArcBall(this, true);

        mDisplayContent = createGraphics(100, 20);
        mDisplayContent.beginDraw();
        mDisplayContent.background(255);
        mDisplayContent.endDraw();

        mLEDisplay = new LEDisplay(mDisplayContent);
        mLEDisplay.setLEDScale(0.75f);
        mLEDisplay.displayLEDAsSphere(true);
    }

    public void draw() {
        background(50);

        /* draw into source image */
        drawIntoDisplay(mDisplayContent);

        /* draw source image */
        image(mDisplayContent, 10, 10);

        /* draw LEDisplay */
        mArcBall.update();
        pushMatrix();
        translate(width / 2.0f, height / 2.0f);
        scale(8);
        mLEDisplay.draw(g);
        popMatrix();

    }

    private void drawIntoDisplay(PGraphics pG) {
        pG.beginDraw();
        pG.noStroke();
        pG.fill(random(255), 127, 0);
        pG.ellipse(random(pG.width), random(pG.height), 10, 10);
        pG.endDraw();
    }

    public static void main(String[] args) {
        PApplet.main(SketchLEDisplayFlat.class.getName());
    }
}
