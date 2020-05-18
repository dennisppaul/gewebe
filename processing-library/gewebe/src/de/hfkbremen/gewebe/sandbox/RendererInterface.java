package de.hfkbremen.gewebe.sandbox;

import processing.opengl.PGraphics3D;

public class RendererInterface extends PGraphics3D {

    @Override
    public void beginShape(int kind) {
        super.beginShape(kind);
        System.out.println("beginShape");
    }

    @Override
    public void endShape(int mode) {
        System.out.println("endShape1");
        super.endShape(mode);
    }

    @Override
    protected void endShape(int[] indices) {
        System.out.println("endShape2");
        super.endShape(indices);
    }

    @Override
    public void vertex(float x, float y) {
        vertex(x, y, 0);
    }

    @Override
    public void vertex(float x, float y, float z) {
        super.vertex(x, y, z);
    }

    @Override
    public void endShape() {
        endShape(OPEN);
    }
}
