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

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PGL;
import processing.opengl.PShader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.jogamp.opengl.GL2ES3.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL2ES3.GL_TEXTURE_BASE_LEVEL;
import static com.jogamp.opengl.GL2ES3.GL_TEXTURE_MAX_LEVEL;
import static com.jogamp.opengl.GL2ES3.GL_VIEWPORT;

public class PointSprites {

    private PShader fPointSpriteShader;

    private final String VERTEX_SHADER_FILE;
    private final String FRAGMENT_SHADER_FILE;
    private static final int VERT_CMP_COUNT = 4; // assumes 4 components (x, y, z, w)
    private final PVector[] fPoints;

    private float[] fVBOFloatArray;
    private FloatBuffer fVBOFloatBuffer;
    private int fVBOBufferID;
    private int fTextureID;
    private float fPointSize;

    public PointSprites(PApplet p,
                        PGL pgl,
                        PVector[] points,
                        String path_to_sprite,
                        String path_to_vertex_shader,
                        String path_to_fragment_shader) {
        VERTEX_SHADER_FILE = path_to_vertex_shader;
        FRAGMENT_SHADER_FILE = path_to_fragment_shader;
        fPoints = points;
        fPointSize = 1.0f;

        setupShader(p);
        setupVBO(pgl);
        setupTexture(pgl, path_to_sprite);
        update();
    }

    public void draw(PGL pgl) {
        pgl.enable(GL2.GL_PROGRAM_POINT_SIZE_EXT); // enable setting point size in shader

        fPointSpriteShader.bind();

        pgl.bindBuffer(PGL.ARRAY_BUFFER, fVBOBufferID);
        pgl.bufferData(PGL.ARRAY_BUFFER, Float.BYTES * fVBOFloatArray.length, fVBOFloatBuffer, PGL.DYNAMIC_DRAW);

        final int mPointPositionLoc = pgl.getAttribLocation(fPointSpriteShader.glProgram, "point_position");
        pgl.enableVertexAttribArray(mPointPositionLoc);
        pgl.vertexAttribPointer(mPointPositionLoc, VERT_CMP_COUNT, PGL.FLOAT, false, 0, 0);

        final int mHeightNearPlaneLoc = pgl.getUniformLocation(fPointSpriteShader.glProgram, "height_near_plane");
        IntBuffer viewport = GLBuffers.newDirectIntBuffer(4);
        pgl.getIntegerv(GL_VIEWPORT, viewport);
        final float mFOVY = PApplet.radians(60); // degrees
        final float mHeightNearPlane = PApplet.abs(viewport.get(3) - viewport.get(1)) / (2 * PApplet.tan(0.5f * mFOVY));
        pgl.uniform1f(mHeightNearPlaneLoc, mHeightNearPlane);

        final int mPointSizeLoc = pgl.getUniformLocation(fPointSpriteShader.glProgram, "point_size");
        pgl.uniform1f(mPointSizeLoc, fPointSize);

        final int mPointTextureLoc = pgl.getUniformLocation(fPointSpriteShader.glProgram, "point_texture");
        pgl.uniform1i(mPointTextureLoc, 0);
        pgl.activeTexture(GL.GL_TEXTURE0);
        pgl.bindTexture(GL.GL_TEXTURE_2D, fTextureID);

        pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);

        pgl.drawArrays(GL.GL_POINTS, 0, fPoints.length);

        pgl.disableVertexAttribArray(mPointPositionLoc);
        fPointSpriteShader.unbind();
    }

    public void set_point_size(float point_size) {
        fPointSize = point_size;
    }

    private void setupShader(PApplet p) {
        fPointSpriteShader = p.loadShader(FRAGMENT_SHADER_FILE, VERTEX_SHADER_FILE);
    }

    private void setupVBO(PGL pgl) {
        fVBOFloatArray = new float[VERT_CMP_COUNT * fPoints.length];
        fVBOFloatBuffer = allocateDirectFloatBuffer(fVBOFloatArray.length);

        IntBuffer intBuffer = IntBuffer.allocate(1);
        pgl.genBuffers(1, intBuffer);
        fVBOBufferID = intBuffer.get(0);
    }

    private void setupTexture(PGL pgl, String path_to_sprite) {
        fTextureID = loadTexture(pgl, path_to_sprite);
    }

    private int loadTexture(PGL gl, String texture_path) {
        try {
            URL texture = new File(texture_path).toURI().toURL();

            TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), texture, false, TextureIO.PNG);

            int level = 0;
            int mTextureID = 0;

            IntBuffer textureName = GLBuffers.newDirectIntBuffer(1);
            gl.genTextures(1, textureName);
            mTextureID = textureName.get(0);

            gl.bindTexture(GL_TEXTURE_2D, mTextureID);
            {
                gl.texImage2D(GL_TEXTURE_2D, level, data.getInternalFormat(), data.getWidth(), data.getHeight(),
                              data.getBorder(), data.getPixelFormat(), data.getPixelType(), data.getBuffer());

                gl.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
                gl.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, level);

                // TODO test with colors
                //IntBuffer swizzle = GLBuffers.newDirectIntBuffer(new int[]{GL_RED, GL_GREEN, GL_BLUE, GL_ONE});
                //gl.texParameteriv(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_RGBA, swizzle);
                //gl.glTexParameterIiv(GL_TEXTURE_2D, GL_TEXTURE_SWIZZLE_RGBA, swizzle);
                //destroyBuffer(swizzle);
            }
            gl.bindTexture(GL_TEXTURE_2D, 0);
            return mTextureID;
        } catch (IOException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    private FloatBuffer allocateDirectFloatBuffer(int n) {
        return ByteBuffer.allocateDirect(n * Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public void update() {
        for (int i = 0; i < fPoints.length; i++) {
            final int j = i * VERT_CMP_COUNT;
            final PVector p = fPoints[i];
            fVBOFloatArray[j + 0] = p.x;
            fVBOFloatArray[j + 1] = p.y;
            fVBOFloatArray[j + 2] = p.z;
            fVBOFloatArray[j + 3] = 1.0f;
        }

        fVBOFloatBuffer.rewind();
        fVBOFloatBuffer.put(fVBOFloatArray);
        fVBOFloatBuffer.rewind();
    }

    /* REF */
    // https://www.khronos.org/opengl/wiki/Primitive
    // https://dev.to/samthor/webgl-point-sprites-a-tutorial-4m6p
    // https://github.com/jvm-graphics-labs/hello-triangle/blob/master/src/main/java/gl3/HelloTexture.java
    // https://stackoverflow.com/questions/17397724/point-sprites-for-particle-system
}