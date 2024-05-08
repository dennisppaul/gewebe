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

import processing.core.PVector;

public class MetaCircle {

    private final PVector mPosition = new PVector();

    private float mStrength = 30000;

    public PVector position() {
        return mPosition;
    }

    public void strength(float pStrength) {
        mStrength = pStrength;
    }

    public float strength() {
        return mStrength;
    }

    public float getStrengthAt(float x, float y, float z) {
        float dx;
        float dy;
        float dz;
        dx = mPosition.x - x;
        dy = mPosition.y - y;
        dz = mPosition.z - z;
        return mStrength / (dx * dx + dy * dy + dz * dz);
    }

    public float getStrengthAt(float x, float y) {
        float dx;
        float dy;
        dx = mPosition.x - x;
        dy = mPosition.y - y;
        return mStrength / (dx * dx + dy * dy);
    }
}
