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

import processing.core.PGraphics;

import java.util.List;

public class BVHViewer {
    public float endpoint_size = 2;
    public float midpoint_size = 1;

    private final BVHParser parser;

    public BVHViewer(String[] data) {
        parser = new BVHParser();
        parser.parse(data);
    }

    public void update(int ms) {
        parser.moveMsTo(ms);
        parser.update();
    }

    public void draw(PGraphics g) {
        g.push();
        g.sphereDetail(8);
        g.fill(255);
        g.stroke(255);
        for (BVHBone b : parser.getBones()) {
            drawBone(g, b);
        }
        g.pop();
    }

    private void drawBone(PGraphics g, BVHBone pBone) {
        g.pushMatrix();
        g.translate(pBone.absPos.x, pBone.absPos.y, pBone.absPos.z);
        g.sphere(midpoint_size);
        g.popMatrix();
        if (!pBone.hasChildren()) {
            g.pushMatrix();
            g.line(pBone.absPos.x, pBone.absPos.y, pBone.absPos.z,
                   pBone.absEndPos.x, pBone.absEndPos.y, pBone.absEndPos.z);
            g.translate(pBone.absEndPos.x, pBone.absEndPos.y, pBone.absEndPos.z);
            g.sphere(endpoint_size);
            g.popMatrix();
        } else {
            final List<BVHBone> mChildren = pBone.getChildren();
            for (BVHBone mBone : mChildren) {
                g.line(mBone.absPos.x, mBone.absPos.y, mBone.absPos.z,
                       pBone.absPos.x, pBone.absPos.y, pBone.absPos.z);
                drawBone(g, mBone);
            }
        }
    }
}
