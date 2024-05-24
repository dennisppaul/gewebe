/*
 * Gewebe
 *
 * This file is part of the *wellen* library (https://github.com/dennisppaul/wellen).
 * Copyright (c) 2024 Dennis P Paul.
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package gewebe;

import processing.core.PApplet;
import processing.core.PGraphics;

public abstract class OffscreenContext {

    public int width;
    public int height;

    protected PGraphics graphics;

    protected final PApplet parent;

    private boolean mIsSetupCalled;

    public OffscreenContext(PApplet pParent) {
        parent         = pParent;
        mIsSetupCalled = false;
        settings();
    }

    public final void size(int pWidth, int pHeight) {
        graphics = parent.createGraphics(pWidth, pHeight); // defaults to JAVA2D
        width    = pWidth;
        height   = pHeight;
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

    public PGraphics texture() {
        return graphics;
    }

    private void post_draw() {
        graphics.endDraw();
    }

    private void pre_draw() {
        graphics.beginDraw();
    }
}
