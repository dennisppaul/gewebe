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

public class Vector3i {

    int x;
    int y;
    int z;

    public Vector3i(int pX, int pY, int pZ) {
        set(pX, pY, pZ);
    }

    public void set(int pX, int pY, int pZ) {
        x = pX;
        y = pY;
        z = pZ;
    }
}
