/*
 * Sunflow Plugin for Gestalt
 *
 * Copyright (C) 2011 The Product GbR Kochlik + Paul
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */


package de.hfkbremen.gewebe;

import org.sunflow.SunflowAPI;
import org.sunflow.core.ParameterList;
import org.sunflow.core.Ray;
import org.sunflow.core.ShadingState;
import org.sunflow.image.Color;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;

/**
 * taken from 'http://sunflow.sourceforge.net/phpbb2/viewtopic
 * .php?t=444&postdays=0&postorder=asc&highlight=translucent&start=15'
 */
public class ShaderTranslucentSR
        implements org.sunflow.core.Shader {

    /* TRANSLUCENT - WITH STORAGE AND REFLECTION OF PHOTONS */
    public static final String name = "translucent_sr";
    public Color absorptionColor = Color.BLUE;
    // object absorption color
    //public Color absorptionColor = Color.RED;
    public float absorptionDistance = 0.25f;
    // inverse of absorption color
    // object color
    public Color color = Color.WHITE;
    // global color-saving variable
    /* FIXME!?? - globals are not good */
    public Color glob = Color.black();
    // phong specular color

    public Color pcolor = Color.BLACK;
    // object absorption distance
    public boolean phong = false;
    // depth correction parameter
    public float ppower = 85f;
    // phong specular power
    public int psamples = 1;
    // phong specular samples
    public float thickness = 0.002f;
    // phong flag
    public Color transmittanceColor = absorptionColor.copy().opposite();

    public boolean update(ParameterList pl, SunflowAPI api) {
        color = pl.getColor("color", color);
        if (absorptionDistance == 0f) {
            absorptionDistance += 0.0000001f;
        }
        if (!pcolor.isBlack()) {
            phong = true;
        }
        return true;
    }

    public Color getRadiance(ShadingState state) {
        Color ret = Color.black();
        Color absorbtion = Color.white();
        glob.set(Color.black());
        state.faceforward();
        state.initLightSamples();
        state.initCausticSamples();
        if (state.getRefractionDepth() == 0) {
            ret.set(state.diffuse(color).mul(0.5f));
            bury(state, thickness);
        } else {
            absorbtion = Color.mul(-state.getRay().getMax() / absorptionDistance, transmittanceColor).exp();
        }
        state.traceRefraction(new Ray(state.getPoint(), randomVector()), 0);
        glob.add(state.diffuse(color));
        glob.mul(absorbtion);
        if (state.getRefractionDepth() == 0 && phong) {
            bury(state, -thickness);
            glob.add(state.specularPhong(pcolor, ppower, psamples));
        }
        return glob;
    }

    public void scatterPhoton(ShadingState state, Color power) {
        Color diffuse = getDiffuse(state);
        state.storePhoton(state.getRay().getDirection(), power, diffuse);
        state.traceReflectionPhoton(new Ray(state.getPoint(), randomVector()), power.mul(diffuse));
    }

    public void bury(ShadingState state, float th) {
        Point3 pt = state.getPoint();
        Vector3 norm = state.getNormal();
        pt.x = pt.x - norm.x * th;
        pt.y = pt.y - norm.y * th;
        pt.z = pt.z - norm.z * th;
    }

    public Vector3 randomVector() {
        return new Vector3(
                (float) (2f * Math.random() - 1f),
                (float) (2f * Math.random() - 1f),
                (float) (2f * Math.random() - 1f)).normalize();
    }

    public Color getDiffuse(ShadingState state) {
        return color;
    }
}
