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
import org.sunflow.core.ShadingState;
import org.sunflow.core.shader.GlassShader;
import org.sunflow.core.shader.TexturedDiffuseShader;
import org.sunflow.image.Color;

public class ShaderStainedGlass
        implements org.sunflow.core.Shader {

    //    /* Free to use for any purpose.  Just do my PR a favour and leave this message here :-) */
    //    /* usage:  Insert the stained glass texture in the update() method block. MPT 13/04/07. */
    public static final String name = "stained_glass";

    public static String texture_path = "./foobar.png";

    private GlassShader s_1 = new GlassShader();

    private TexturedDiffuseShader s_2 = new TexturedDiffuseShader();

    private boolean b1, b2, b3 = false;

    private SunflowAPI myapi = new SunflowAPI();

    public Color getDiffuse(ShadingState state) {
        return s_2.getDiffuse(state);
    }

    public Color getRadiance(ShadingState state) {
        ParameterList mypl = new ParameterList();
        mypl.addColor("color", s_2.getDiffuse(state));
        b3 = s_1.update(mypl, myapi);
        return s_1.getRadiance(state);
    }

    public void scatterPhoton(ShadingState state, Color power) {
        ParameterList mypl = new ParameterList();
        mypl.addColor("color", s_2.getDiffuse(state).mul(3f));
        b3 = s_1.update(mypl, myapi);
        s_1.scatterPhoton(state, power);
    }

    public boolean update(ParameterList pl, SunflowAPI api) {
        pl.addString("texture", texture_path);
        b2 = s_2.update(pl, api);
        pl.addFloat("eta", 1.000000001f);
        b1 = s_1.update(pl, api);
        return (b1 && b2);
    }
}
