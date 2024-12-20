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

public class TestStaticPropertiesInherited {

//    Class<Device>[] devices = new Class<Device>[]{DeviceA.class, DeviceB.class};

    class Device {
        boolean has_value;
    }

    class DeviceA extends Device {
        boolean has_value() {
            return true;
        }
    }

    class DeviceB extends Device {
        boolean has_value() {
            return true;
        }
    }
}
