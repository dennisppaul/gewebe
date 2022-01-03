package gewebe;

import processing.core.PGraphics;
import processing.core.PImage;

public class LEDisplay {

    private boolean mDisplayLEDAsSphere;
    private final PImage mLEDImage;
    private float mLEDScale;

    public LEDisplay(PImage pImage) {
        mLEDImage = pImage;
        mLEDScale = 1.0f;
        mDisplayLEDAsSphere = false;
    }

    public void setLEDScale(float pLEDScale) {
        mLEDScale = pLEDScale;
    }

    public void displayLEDAsSphere(boolean pDisplayLEDAsSphere) {
        mDisplayLEDAsSphere = pDisplayLEDAsSphere;
    }

    public void draw(PGraphics pCanvas) {
        draw(pCanvas, mLEDImage, mLEDScale, mDisplayLEDAsSphere);
    }

    public static void draw(PGraphics pCanvas, PImage pLEDImage, float pLEDScale, boolean pDisplayLEDAsSphere) {
        pCanvas.sphereDetail(8);
        pCanvas.pushMatrix();
        pCanvas.translate(pLEDImage.width / -2.0f, pLEDImage.height / -2.0f);
        pCanvas.noStroke();
        for (int x = 0; x < pLEDImage.width; x++) {
            for (int y = 0; y < pLEDImage.height; y++) {
                int c = pLEDImage.get(x, y);
                pCanvas.fill(c);
                pCanvas.pushMatrix();
                pCanvas.translate(x, y);
                if (pDisplayLEDAsSphere) {
                    pCanvas.sphere(pLEDScale / 2);
                } else {
                    pCanvas.ellipse(0, 0, pLEDScale, pLEDScale);
                }
                pCanvas.popMatrix();
            }
        }
        pCanvas.popMatrix();
    }

    public static void draw(PGraphics pCanvas, PImage pLEDImage, float[] mModelData, float pLEDScale,
                            boolean pDisplayLEDAsSphere) {
        pCanvas.sphereDetail(8);
        pCanvas.pushMatrix();
        pCanvas.translate(pLEDImage.width / -2.0f, pLEDImage.height / -2.0f);
        pCanvas.noStroke();
        if (pLEDImage.pixels.length * 3 > mModelData.length) {
            System.out.println("### image size does not match number of vertices in model data");
        }
        for (int i = 0; i < pLEDImage.pixels.length; i++) {
            int c = pLEDImage.pixels[i];
            final int j = i * 3;
            float x = mModelData[j + 0];
            float y = mModelData[j + 1];
            float z = mModelData[j + 2];
            pCanvas.fill(c);
            pCanvas.pushMatrix();
            pCanvas.translate(x, y, z);
            if (pDisplayLEDAsSphere) {
                pCanvas.sphere(pLEDScale / 2);
            } else {
                pCanvas.circle(0, 0, pLEDScale);
            }
            pCanvas.popMatrix();
        }
        pCanvas.popMatrix();
    }
}
