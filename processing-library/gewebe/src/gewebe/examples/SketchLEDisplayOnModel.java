package gewebe.examples;

import gewebe.ArcBall;
import gewebe.LEDisplay;
import processing.core.PApplet;
import processing.core.PGraphics;

public class SketchLEDisplayOnModel extends PApplet {

    private ArcBall mArcBall;
    private PGraphics mDisplayContent;
    private float[] mModelData;

    private float mLineMover;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        mArcBall = new ArcBall(this, true);

        mDisplayContent = createGraphics(100, 20);
        mModelData = createModelData();

        LEDisplay mLEDisplay = new LEDisplay(mDisplayContent);
        mLEDisplay.setLEDScale(0.75f);
        mLEDisplay.displayLEDAsSphere(true);
    }

    public void draw() {
        background(50);

        beginShape(POINTS);
        for (int i = 0; i < mModelData.length; i += 3) {
            vertex(mModelData[i + 0], mModelData[i + 1], mModelData[i + 2]);
        }
        endShape();

        /* draw into source image */
        drawIntoDisplay(mDisplayContent);

        /* draw source image */
        image(mDisplayContent, 10, 10);

        /* draw LEDisplay */
        mArcBall.update();
        pushMatrix();
        translate(width / 2.0f, height / 2.0f);
        LEDisplay.draw(g, mDisplayContent, mModelData, 5, true);
        popMatrix();

    }

    private void drawIntoDisplay(PGraphics pG) {
        pG.beginDraw();
        pG.background(0);
        pG.noFill();
        mLineMover++;
        mLineMover %= pG.width;
        pG.stroke(255);
        pG.line(mLineMover + 5, 0, mLineMover - 5, pG.height);
        pG.line(mLineMover - 5, 0, mLineMover + 5, pG.height);
        pG.ellipse(mLineMover, pG.height / 2.0f, 10, 10);
        pG.endDraw();
    }

    private float[] createModelData() {
        float[] mData = new float[mDisplayContent.width * mDisplayContent.height * 3];
        for (int i = 0; i < mData.length / 3; i++) {
            int x = (i % mDisplayContent.width) - mDisplayContent.width / 2;
            int y = (i / mDisplayContent.width) - mDisplayContent.height / 2;

            int j = i * 3;
            mData[j + 0] = x * 8;
            final float mNoiseScaleY = 0.029f;
            mData[j + 1] = y * noise(x * mNoiseScaleY, y * mNoiseScaleY) * 25;
            final float mNoiseScaleZ = 0.05f;
            mData[j + 2] = noise(x * mNoiseScaleZ, y * mNoiseScaleZ) * 100;
        }
        return mData;
    }

    public static void main(String[] args) {
        PApplet.main(SketchLEDisplayOnModel.class.getName());
    }
}