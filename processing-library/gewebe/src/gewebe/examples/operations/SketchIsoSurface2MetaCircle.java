package gewebe.examples.operations;

import gewebe.IsoSurface2;
import gewebe.MetaCircle;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class SketchIsoSurface2MetaCircle extends PApplet {

    final int mSquareSizeX = 8;
    final int mSquareSizeY = 8;
    private float[][] mEnergyGrid;
    private float mIsoValue = 32.0f;
    private MetaCircle[] mMetaCircles;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        noFill();
        stroke(255);

        mMetaCircles = new MetaCircle[30];
        for (int i = 0; i < mMetaCircles.length; i++) {
            mMetaCircles[i] = new MetaCircle();
            mMetaCircles[i].position().set(random(0, width), random(0, height));
            mMetaCircles[i].strength(random(5000, 50000));
        }
    }

    public void draw() {
        background(50);

        mMetaCircles[0].position().set(mouseX, mouseY);

        update_energy_field();
        ArrayList<ArrayList<PVector>> mBlobShapes = IsoSurface2.getBlobs(mEnergyGrid, mIsoValue);
        draw_blobs(mBlobShapes);
    }

    public void keyPressed() {
        switch (key) {
            case '+':
                mIsoValue++;
                break;
            case '-':
                mIsoValue--;
                break;
        }
    }

    private void draw_blobs(ArrayList<ArrayList<PVector>> pBlobShapes) {
        for (ArrayList<PVector> mBlobShape : pBlobShapes) {
            beginShape();
            for (PVector p : mBlobShape) {
                vertex(p.x * mSquareSizeX, p.y * mSquareSizeY);
            }
            endShape(CLOSE);
        }
    }

    private void update_energy_field() {
        final int mNumberOfSquaresX = width / mSquareSizeX;
        final int mNumberOfSquaresY = height / mSquareSizeY;
        mEnergyGrid = new float[mNumberOfSquaresX][mNumberOfSquaresY];

        for (int x = 0; x < mEnergyGrid.length; x++) {
            for (int y = 0; y < mEnergyGrid[x].length; y++) {
                mEnergyGrid[x][y] = 0;
                for (MetaCircle mMetaCircle : mMetaCircles) {
                    mEnergyGrid[x][y] += mMetaCircle.getStrengthAt(mSquareSizeX * x, mSquareSizeY * y);
                }
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchIsoSurface2MetaCircle.class.getName()});
    }
}
