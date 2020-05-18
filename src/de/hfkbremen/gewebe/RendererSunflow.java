package de.hfkbremen.gewebe;

import org.sunflow.PluginRegistry;
import org.sunflow.SunflowAPI;
import org.sunflow.core.Display;
import org.sunflow.core.ParameterList;
import org.sunflow.core.ShadingState;
import org.sunflow.core.display.FileDisplay;
import org.sunflow.math.Matrix4;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;
import org.sunflow.system.ImagePanel;
import processing.core.PMatrix;
import processing.core.PVector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class RendererSunflow extends RendererMesh {

    // @TODO(separate lines from triangles to make use of hair renderer)
    // @TODO(find a way to export scene as `.sc` file. look into `renderObjects`)

    private static final String SHADER_AMBIENT_OCCLUSION = "ambient_occlusion";
    private static final String GI_PATH = "path";
    private static final String GI_FAKE = "fake";
    private static final String SHADER_PHONG = "phong";
    private static final String SHADER_NAME = "my_shader";
    private static final String SHADER_TEXTURED_PHONG = "textured_phong";
    private static final PVector _myFloor = new PVector();
    private static final float MAX_DIST = 600f;
    private static final String COLORSPACE_SRGB_NONLINEAR = "sRGB nonlinear";
    private static final String GI_INSTANT_GI = "igi";
    private static final String GI_IRRADIANCE_CACHE = "irr-cache";
    private static final String LIGHT_SUNSKY = "sunsky";
    private static final String FILTER_BLACKMAN_HARRIS = "blackman-harris";
    private static final String FILTER_BOX = "box";
    private static final String FILTER_CATMULL_ROM = "catmull-rom";
    private static final String FILTER_GAUSSIAN = "gaussian";
    private static final String FILTER_LANCZOS = "lanczos";
    private static final String FILTER_MITCHELL = "mitchell";
    private static final String FILTER_SINC = "sinc";
    private static final String FILTER_TRIANGLE = "triangle";
    private static final String FILTER_BSPLINE = "bspline";
    private static final String LIGHT_POINT = "point";
    private static final boolean headless = true; // @TODO(bring back render preview)
    public static String IMAGE_FILE_TYPE_PNG = ".png";
    public static String IMAGE_FILE_TYPE_TGA = ".tga";
    public static String SCENE_FILE_TYPE = ".xml";
    public static String OUTPUT_IMAGE_FILE_TYPE = IMAGE_FILE_TYPE_PNG;
    public static boolean preview = true;
    public static boolean floor = false;
    public static boolean auto_dispose_frame = false;
    public static boolean start_in_extra_thread = false;
    public static PVector floor_up = null;
    public static float RENDER_VIEWPORT_SCALE = 1;
    public static Color BACKGROUND_COLOR = new Color(0.5f);
    public static int SAMPLES = 128;
    public static int AA_MIN = 1;
    public static int AA_MAX = 2;
    static int mShaderID = 0;

    static {
        PluginRegistry.shaderPlugins.registerPlugin("my_test_shader", MyCustomShader.class);
        PluginRegistry.shaderPlugins.registerPlugin(ShaderTranslucent.name, ShaderTranslucent.class);
        PluginRegistry.shaderPlugins.registerPlugin(ShaderTranslucentSR.name, ShaderTranslucentSR.class);
        PluginRegistry.shaderPlugins.registerPlugin(ShaderStainedGlass.name, ShaderStainedGlass.class);
    }

    private final SunflowAPI mSunflow;
    private final String mColorSpace = COLORSPACE_SRGB_NONLINEAR;
    private Display _myDisplay;
    private int mGeometryID = 0;

    //        public static void render(String theFilePath) {
    //            RendererSunflow myRenderer = new RendererSunflow();
    //            myRenderer.width(theGestalt.displaycapabilities().width);
    //            myRenderer.height(theGestalt.displaycapabilities().height);
    //            myRenderer.setupScene();
    //            //        myRenderer.setSunSkyLight("my_sun");
    //            myRenderer.setupCamera(theGestalt.camera());
    //            myRenderer.setupLight(theGestalt.light());
    //
    //            if (headless) {
    //                myRenderer.setDisplay(new FileDisplay(theFilePath));
    //            } else {
    //                myRenderer.setDisplay(new MyFrameDisplay(theFilePath, myRenderer));
    //            }
    //
    //            /* --- */
    //            myRenderer.parse(theBin);
    //
    //            //        myRenderer.setInstantGIEngine(64, 1, 0.01f, 0); System.out.println("### using
    //            'InstantGIEngine'");
    //            myRenderer.setIrradianceCacheGIEngine(32, .4f, 1f, 15f, null);
    //            System.out.println("### using 'IrradianceCacheGIEngine'");
    //            //        myRenderer.setFakeGIEngine(new PVector(0, 1, 0));
    //
    //            if (start_in_extra_thread) {
    //                myRenderer.render();
    //            } else {
    //                myRenderer._render();
    //            }
    //        }

    public RendererSunflow() {
        mSunflow = new SunflowAPI();
    }

    public static PVector floor() {
        return _myFloor;
    }

    public static String name() {
        return RendererSunflow.class.getName();
    }

    public SunflowAPI sunflow() {
        return mSunflow;
    }

    public void setInstantGIEngine(int samples, int sets, float c, float bias_samples) {
        mSunflow.parameter("gi.engine", GI_INSTANT_GI);
        mSunflow.parameter("gi.igi.samples", samples);
        mSunflow.parameter("gi.igi.sets", sets);
        mSunflow.parameter("gi.igi.c", c);
        mSunflow.parameter("gi.igi.bias_samples", bias_samples);
    }

    public void setIrradianceCacheGIEngine(int samples,
                                           float tolerance,
                                           float minSpacing,
                                           float maxSpacing,
                                           String globalphotonmap) {
        mSunflow.parameter("gi.engine", GI_IRRADIANCE_CACHE);
        mSunflow.parameter("gi.irr-cache.samples", samples);
        mSunflow.parameter("gi.irr-cache.tolerance", tolerance);
        mSunflow.parameter("gi.irr-cache.min_spacing", minSpacing);
        mSunflow.parameter("gi.irr-cache.max_spacing", maxSpacing);
        mSunflow.parameter("gi.irr-cache.gmap", globalphotonmap);
    }

    public void setFakeGIEngine(PVector theUp, Color pSky, Color pGround) {
        mSunflow.parameter("gi.engine", GI_FAKE);
        mSunflow.parameter("gi.fake.up", new Vector3(theUp.x, theUp.y, theUp.z));
        mSunflow.parameter("gi.fake.sky",
                           mColorSpace, pSky.r, pSky.g, pSky.b);
        mSunflow.parameter("gi.fake.ground",
                           mColorSpace, pGround.r, pGround.g, pGround.b);
    }

    public void setPathTracingGIEngine(int samples) {
        mSunflow.parameter("gi.engine", GI_PATH);
        mSunflow.parameter("gi.path.samples", samples);
    }

    public void setupScene() {
        /* background */
        mSunflow.parameter("color", null, BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b);
        mSunflow.shader("background.shader", "constant");

        mSunflow.geometry("background", "background");
        mSunflow.parameter("shaders", "background.shader");
        mSunflow.instance("background.instance", "background");

        /* ground */
        if (floor) {
            mSunflow.parameter("maxdist", MAX_DIST);
            mSunflow.parameter("samples", SAMPLES);
            mSunflow.shader("ao_ground", "ambient_occlusion");

            mSunflow.parameter("center", new Point3(_myFloor.x, _myFloor.y, _myFloor.z));
            if (floor_up == null) {
                mSunflow.parameter("normal", new Vector3(0.0f, 1.0f, 0.0f));
            } else {
                mSunflow.parameter("normal", new Vector3(floor_up.x, floor_up.y, floor_up.z));
            }
            mSunflow.geometry("ground", "plane");
            mSunflow.parameter("shaders", "ao_ground");
            mSunflow.instance("ground.instance", "ground");
        }
    }

    public void setupCamera() {
        float mFOVY = 53.0f;
        final float myAspect = (float) width / (float) height;
        String mCameraName = "my_camera";

        final boolean mMyCameraSetup = false;
        if (mMyCameraSetup) {
            System.out.println("@TODO setupCamera / transform matrix");
            Point3 eye = new Point3(0, 0, 500);
            Point3 target = new Point3(0, 0, 0);
            Vector3 up = new Vector3(0, 1, 0);
            mSunflow.parameter("transform", Matrix4.lookAt(eye, target, up));
            mSunflow.parameter("fov", mFOVY);
            mSunflow.parameter("aspect", myAspect);
            mSunflow.parameter("shift.x", 0.0f);
            mSunflow.parameter("shift.y", 0.0f);
            mSunflow.camera("my_camera", "pinhole");
        } else {
            setPinholeCamera(mCameraName, mFOVY, myAspect, 0, 0);
        }

        /* ----------------------------------------------- */

        //        // Camera theCamera
        //        if (theCamera.getMode() != Gestalt.CAMERA_MODE_LOOK_AT) {
        //            System.out.println("### camera currently only works in LOOK_AT mode.");
        //        }
        //
        //        /* only works for LOOK_AT mode */
        //
        //        Point3 eye = new Point3(theCamera.position().x, theCamera.position().y, theCamera.position().z);
        //        Point3 target = new Point3(theCamera.lookat().x, theCamera.lookat().y, theCamera.lookat().z);
        //        Vector3 up = new Vector3(theCamera.upvector().x, theCamera.upvector().y, theCamera.upvector().z);
        //        mSunflow.parameter("transform", Matrix4.lookAt(eye, target, up));
        //
        //        //        PMatrix myCameraTransform = new PMatrix(PMatrix.IDENTITY);
        //        //        myCameraTransform.rotation.set(theCamera.getRotationMatrix());
        //        //        myCameraTransform.translation.set(theCamera.position());
        //        //        Matrix4 myMatrix4 = new Matrix4(myCameraTransform.toArray(), false);
        //        //        _mySunflow.parameter("transform", myMatrix4);
        //
        //        mSunflow.parameter("fov", theCamera.fovy);
        //        final float myAspect = (float) theCamera.viewport().width / (float) theCamera.viewport().height;
        //        mSunflow.parameter("aspect", myAspect);
        //        mSunflow.parameter("shift.x", 0.0f);
        //        mSunflow.parameter("shift.y", 0.0f);
        //        mSunflow.camera("my_camera", "pinhole");
        //
        //        //        System.out.println("theCamera.position() " + theCamera.position());
        //        //        System.out.println("theCamera.lookat() " + theCamera.lookat());
        //        //        System.out.println("theCamera.upvector() " + theCamera.upvector());
        //        //        System.out.println("theCamera.fovy() " + theCamera.fovy);
        //        //        System.out.println("myAspect " + myAspect);
    }

    public void sendShaderPhong(Color diffuse,
                                Color specular,
                                float power,
                                int samples,
                                String texture) {
        //		set parameter
        mSunflow.parameter("diffuse",
                           mColorSpace,
                           diffuse.r, diffuse.g, diffuse.b);

        mSunflow.parameter("specular",
                           mColorSpace,
                           specular.r, specular.g, specular.b);

        mSunflow.parameter("power", power);
        mSunflow.parameter("samples", samples);
        if (texture != null) { mSunflow.parameter("texture", texture); }

        //		set shader
        if (texture != null) {
            mSunflow.shader(SHADER_NAME + mShaderID, SHADER_TEXTURED_PHONG);
        } else {
            mSunflow.shader(SHADER_NAME + mShaderID, SHADER_PHONG);
        }
    }

    public void setPointLight(String name, Point3 center, Color color) {
        mSunflow.parameter("center", center);
        mSunflow.parameter("power",
                           mColorSpace,
                           color.r, color.g, color.b);
        mSunflow.light(name, LIGHT_POINT);
    }

    public void setupLight() {
        boolean mMyLightSetup = true;
        System.out.println("@TODO setupLight");
        if (mMyLightSetup) {
            mSunflow.parameter("power", mColorSpace, 1.0f, 1.0f, 1.0f);
            mSunflow.parameter("center", new Point3(300, 300, 300));
            mSunflow.light("myLight", LIGHT_POINT);
        } else {
            setPointLight("myLight",
                          new Point3(3000, 3000, 3000),
                          new Color(0.01f));
        }

        //        Light theLight
        //                /* light */
        //                mSunflow.parameter("power", colorSpace, 1.0f, 1.0f, 1.0f);
        //                mSunflow.parameter("center", new Point3(theLight.position().x,
        //                                                        theLight.position().y,
        //                                                        theLight.position().z));
        //                mSunflow.light("myLight", "point");
    }

    public void sendTexturedAmbientOcclusionMaterial(Color theColor, String theAbsoluteFilePath) {
        /* material */
        mSunflow.parameter("maxdist", MAX_DIST);
        mSunflow.parameter("samples", SAMPLES);
        if (theColor != null) {
            mSunflow.parameter("bright", mColorSpace, theColor.r, theColor.g, theColor.b);
        } else {
            mSunflow.parameter("bright", mColorSpace, 1, 1, 1, 1);
        }

        /* how do we get this into the pipeline? */
        mSunflow.parameter("dark", mColorSpace, 0, 0, 0);
        mSunflow.parameter("texture", theAbsoluteFilePath);
        //        _mySunflow.parameter("texture", Resource.getPath("person/person0001.jpg"));
        mSunflow.shader(SHADER_NAME + mShaderID, "textured_ambient_occlusion");
    }

    public void sendShaderAmbientOcclusion(Color theColor) {
        /* material */
        mSunflow.parameter("maxdist", MAX_DIST);
        mSunflow.parameter("samples", SAMPLES);
        if (theColor != null) {
            mSunflow.parameter("bright", mColorSpace, theColor.r, theColor.g, theColor.b);
        } else {
            mSunflow.parameter("bright", mColorSpace, 1, 1, 1);
        }

        /* how do we get this into the pipeline? */
        mSunflow.parameter("dark", mColorSpace, 0, 0, 0);
        mSunflow.shader(SHADER_NAME + mShaderID, SHADER_AMBIENT_OCCLUSION);
    }

    public void sendUberMaterial(final Color theColor,
                                 final String theAbsoluteDiffuseTexturePath,
                                 final String theAbsoluteSpecularTexturePath) {
        /* material */
        mSunflow.parameter("diffuse", mColorSpace, theColor.r, theColor.g, theColor.b);
        mSunflow.parameter("specular", mColorSpace, theColor.r, theColor.g, theColor.b);
        //        _mySunflow.parameter("diffuse.texture", theAbsoluteDiffuseTexturePath);
        //        _mySunflow.parameter("specular.texture", theAbsoluteSpecularTexturePath);
        mSunflow.parameter("diffuse.blend", 1 - theColor.a);
        mSunflow.parameter("specular.blend", 1 - theColor.a);
        mSunflow.parameter("glossyness", 1.0f);
        mSunflow.parameter("samples", SAMPLES);

        mSunflow.shader(SHADER_NAME + mShaderID, "uber");
    }

    public void sendGlassMaterial(final Color theColor,
                                  final float theETA,
                                  final float theAbsorptionDistance,
                                  final Color theAbsorptionColor) {
        /* material */
        mSunflow.parameter("color", mColorSpace,
                           theColor.r,
                           theColor.g,
                           theColor.b);
        mSunflow.parameter("eta", theETA);
        mSunflow.parameter("absorption.distance", theAbsorptionDistance);
        mSunflow.parameter("absorption.color", mColorSpace,
                           theAbsorptionColor.r,
                           theAbsorptionColor.g,
                           theAbsorptionColor.b);

        /* shader */
        mSunflow.shader(SHADER_NAME + mShaderID, "glass");
    }

    public void sendTranslucentMaterial(final Color theColor) {
        /* material */
        mSunflow.parameter("color", mColorSpace,
                           theColor.r,
                           theColor.g,
                           theColor.b);
        /* shader */
        mSunflow.shader(SHADER_NAME + mShaderID, ShaderTranslucent.name);
    }

    public void sendStainedGlasMaterial(final Color theColor) {
        /* material */
        mSunflow.parameter("color", mColorSpace,
                           theColor.r,
                           theColor.g,
                           theColor.b);
        /* shader */
        mSunflow.shader(SHADER_NAME + mShaderID, ShaderStainedGlass.name);
    }

    //    public void sendTriangles(final float[] theVertices,
    //                              final PMatrix theTransform,
    //                              final PVector theRotation,
    //                              final PVector theScale) {
    //        sendTriangles(theVertices,
    //                      null,
    //                      null,
    //                      theTransform,
    //                      theRotation,
    //                      theScale);
    //    }

    //    public void sendTriangles(final float[] pVertices,
    //                              final float[] pNormals,
    //                              final float[] pTexCoords,
    //                              final PMatrix pTransform,
    //                              final PVector pRotation,
    //                              final PVector pScale) {
    //
    //        float[] mTransformedVertices = transformCoords(pVertices, pTransform, pScale, pRotation);
    //
    //        /*
    //         * // create geometry @ SCParser
    //         * api.parameter("triangles", triangles);
    //         * api.parameter("points", "point", "vertex", points);
    //         * api.parameter("normals", "vector", "vertex", normals);
    //         * api.parameter("uvs", "texcoord", "vertex", uvs);
    //         * api.geometry(name, "triangle_mesh");
    //         *
    //         */
    //
    //        int[] mFaces = new int[pVertices.length / 3];
    //        for (int i = 0; i < mFaces.length; i++) {
    //            mFaces[i] = i;
    //        }
    //
    //        mSunflow.parameter("triangles", mFaces);
    //        mSunflow.parameter("points", "point", "vertex", mTransformedVertices);
    //
    //        if (pNormals != null) {
    //            mSunflow.parameter("normals", "vector", "vertex", pNormals); // PVector
    //        }
    //
    //        // @todo texture coordinates are never transformed by texture matrix transform
    //        if (pTexCoords != null) {
    //            mSunflow.parameter("uvs", "texcoord", "vertex", pTexCoords); // Vector2f
    //        }
    //
    //        mGeometryID++;
    //        final String mGeometryName = "myPrimitive" + mGeometryID;
    //        mSunflow.geometry(mGeometryName, "triangle_mesh");
    //
    //        mSunflow.parameter("shaders", SHADER_NAME + _myDrawableID);
    //        mSunflow.instance(mGeometryName + ".instance", mGeometryName);
    //    }

    public void sendTriangles(final ArrayList<Float> pVerticesList, final ArrayList<Float> pNormalsList) {
        if (pVerticesList.size() != pNormalsList.size()) {
            System.err.println("### vertex + normals list does not align!");
        }
        final float[] pVertices = new float[pVerticesList.size()];
        final float[] pNormals = new float[pNormalsList.size()];
        for (int i = 0; i < pVerticesList.size(); i++) {
            pVertices[i] = pVerticesList.get(i);
            pNormals[i] = pNormalsList.get(i);
        }
        /*
         * // create geometry @ SCParser
         * api.parameter("triangles", triangles);
         * api.parameter("points", "point", "vertex", points);
         * api.parameter("normals", "vector", "vertex", normals);
         * api.parameter("uvs", "texcoord", "vertex", uvs);
         * api.geometry(name, "triangle_mesh");
         *
         */

        int[] mFaces = new int[pVertices.length / 3];
        for (int i = 0; i < mFaces.length; i++) {
            mFaces[i] = i;
        }

        final String mGeometryName = "myPrimitive" + mGeometryID;

        mSunflow.parameter("points", "point", "vertex", pVertices);
        mSunflow.parameter("triangles", mFaces);
        // @TODO(sending normals hangs render process. why?)
        //        mSunflow.parameter("normals", "vector", "vertex", pNormals);
        //        mSunflow.parameter("uvs", "texcoord", "vertex", uvs);
        mSunflow.geometry(mGeometryName, "triangle_mesh");
        mSunflow.parameter("shaders", SHADER_NAME + mShaderID);
        mSunflow.instance(mGeometryName + ".instance", mGeometryName);
    }

    public void setDisplay(final Display theDisplay) {
        _myDisplay = theDisplay;
    }

    @Override
    protected void beginFrame() {
    }

    @Override
    protected void endFrame() {
    }

    //    public void sendSphere() {
    //        Sphere pDrawable
    //        final PVector p = pDrawable.position();
    //        final Matrix4 translate = Matrix4.IDENTITY.multiply(Matrix4.translation(p.x, p.y, p.z));
    //        final PVector s = pDrawable.scale();
    //        /* half scale? */
    //        final Matrix4 scale = Matrix4.IDENTITY.multiply(Matrix4.scale(s.x * 0.5f, s.y * 0.5f, s.z * 0.5f));
    //
    //        Matrix4 m = Matrix4.IDENTITY;
    //        m = scale.multiply(m);
    //        m = translate.multiply(m);
    //
    //        mSunflow.geometry("Sphere" + _myDrawableID, "sphere");
    //        mSunflow.parameter("shaders", SHADER_NAME + _myDrawableID);
    //        mSunflow.parameter("transform", m);
    //        mSunflow.instance("Sphere" + _myDrawableID + ".instance", "Sphere" + _myDrawableID);
    //    }

    //    public void sendLines(Line pDrawable) {
    //                float[] mTransformedVertices = transformCoords(convertPVectorToFloat(pDrawable.points),
    //                                                               pDrawable.transform(),
    //                                                               pDrawable.scale(),
    //                                                               pDrawable.rotation());
    //
    //                /* we ll do with a line-strip for now */
    //
    //                mSunflow.parameter("segments", pDrawable.points.length - 1);
    //                mSunflow.parameter("widths", pDrawable.linewidth);
    //                mSunflow.parameter("points", "point", "vertex", mTransformedVertices);
    //
    //                //        mSunflow.parameter("colors", "???", "???", werkzeug.Util.toArrayFromColor(pDrawable
    //                .colors));
    //
    //                mSunflow.geometry("myPrimitive" + _myDrawableID, "hair");
    //
    //                mSunflow.parameter("shaders", SHADER_NAME + _myDrawableID);
    //                mSunflow.instance("myPrimitive" + _myDrawableID + ".instance", "myPrimitive" + _myDrawableID);
    //    }

    //    public void run() {
    //        _render();
    //    }

    @Override
    protected void prepareFrame() {
    }

    @Override
    protected void finalizeFrame() {
        setupScene();
        //                setSunSkyLight("my_sun");
        setupCamera();
        //                setupLight();

        if (headless) {
            setDisplay(new FileDisplay(path + OUTPUT_IMAGE_FILE_TYPE));
        } else {
            setDisplay(new MyFrameDisplay(path + OUTPUT_IMAGE_FILE_TYPE, this));
        }

        //        /* --- */
        //        parse(theBin);

        //        setInstantGIEngine(64, 1, 0.01f, 0);
        //        setIrradianceCacheGIEngine(32, .4f, 1f, 15f, null);
        //        setFakeGIEngine(new PVector(0, 1, 0),
        //                        new Color(1.0f),
        //                        new Color(0.8f));
        //                setPathTracingGIEngine(64);

        for (ShaderTriangleBucket s : bucket()) {
            bumpShaderID();
            // @TODO(integrate materials better)
            //            sendShaderPhong(s.color, s.color, 0.8f, 16, null);
            sendShaderAmbientOcclusion(s.color);
            sendTriangles(s.vertices, s.normals);
            bumpGeometryID();
        }

        if (start_in_extra_thread) {
            renderInOwnThread();
        } else {
            _render();
        }
    }

    private void bumpShaderID() {
        mShaderID++;
    }

    private void bumpGeometryID() {
        mGeometryID++;
    }

    private void setPinholeCamera(String name, float fov, float aspect, float shiftX, float shiftY) {
        // save parameters
        //        this.fov = fov;
        //        this.aspect = aspect;
        //        this.shiftX = shiftX;
        //        this.shiftY = shiftY;

        float[] mCameraMatrix = {cameraInv.m00, cameraInv.m01, cameraInv.m02, cameraInv.m03,
                                 cameraInv.m10, cameraInv.m11, cameraInv.m12, cameraInv.m13,
                                 cameraInv.m20, cameraInv.m21, cameraInv.m22, cameraInv.m23,
                                 cameraInv.m30, cameraInv.m31, cameraInv.m32, cameraInv.m33};
        getMatrix().get(mCameraMatrix);
        mCameraMatrix[0] *= 1;
        mCameraMatrix[5] *= -1;
        mCameraMatrix[10] *= 1;

        mSunflow.parameter("transform", new Matrix4(mCameraMatrix, true));
        mSunflow.parameter("fov", fov);
        mSunflow.parameter("aspect", aspect);
        mSunflow.parameter("shift.x", shiftX);
        mSunflow.parameter("shift.y", shiftY);
        mSunflow.camera(name, "pinhole");
    }

    private void renderInOwnThread() {
        System.out.println("@TODO start in extra thread");
        _render();
    }

    private static float[] convertPVectorToFloat(final PVector[] pVector) {
        final float[] mFloats = new float[pVector.length * 3];
        for (int i = 0; i < pVector.length; i++) {
            final PVector v = pVector[i];
            mFloats[i * 3 + 0] = v.x;
            mFloats[i * 3 + 1] = v.y;
            mFloats[i * 3 + 2] = v.z;
        }
        return mFloats;
    }

    private void setSunSkyLight(String theName) {
        mSunflow.parameter("up", new Vector3(0, 0, 1));
        mSunflow.parameter("east", new Vector3(0, 1, 0));
        mSunflow.parameter("sundir", new Vector3(1, -1, 0.31f));
        mSunflow.parameter("turbidity", 0f);
        mSunflow.parameter("samples", 16);
        mSunflow.light(theName, LIGHT_SUNSKY);
    }

    private float[] transformCoords(final float[] pVertices,
                                    final PMatrix pTransform,
                                    final PVector pScale,
                                    final PVector pRotation) {
        final float[] mTransformedVertices = new float[pVertices.length];
        //        Matrix3f myRotationMatrix = null;
        //        if (pTransform != null) {
        //            myRotationMatrix = new Matrix3f(pTransform.rotation);
        //            myRotationMatrix.invert();
        //        }
        for (int i = 0; i < mTransformedVertices.length; i += 3) {
            final PVector myVertex = new PVector(pVertices[i + 0], pVertices[i + 1], pVertices[i + 2]);
            //            if (pScale != null) {
            //                myVertex.scale(pScale);
            //            }
            //            if (pRotation != null && (pRotation.x != 0 || pRotation.y != 0 || pRotation.z != 0)) {
            //                final PMatrix myTempRotationMatrix = new PMatrix(PMatrix.IDENTITY);
            //                myTempRotationMatrix.rotation.setXYZRotation(pRotation);
            //                myTempRotationMatrix.transform(myVertex);
            //            }
            //            if (pTransform != null) {
            //                myRotationMatrix.transform(myVertex);
            //                myVertex.add(pTransform.translation);
            //            }
            mTransformedVertices[i + 0] = myVertex.x;
            mTransformedVertices[i + 1] = myVertex.y;
            mTransformedVertices[i + 2] = myVertex.z;
        }
        return mTransformedVertices;
    }

    private void _render() {
        mSunflow.parameter("camera", "my_camera");
        mSunflow.parameter("resolutionX", (int) (width * RENDER_VIEWPORT_SCALE));
        mSunflow.parameter("resolutionY", (int) (height * RENDER_VIEWPORT_SCALE));

        if (preview) {
            mSunflow.parameter("aa.min", -3);
            mSunflow.parameter("aa.max", 0);
            mSunflow.parameter("bucket.order", "spiral");
        } else {
            mSunflow.parameter("aa.min", AA_MIN);
            mSunflow.parameter("aa.max", AA_MAX);
            mSunflow.parameter("bucket.order", "column");
            mSunflow.parameter("jitter", "true");
            //            _mySunflow.parameter("filter", FILTER_BLACKMAN_HARRIS);
            mSunflow.parameter("filter", FILTER_MITCHELL);
        }
        mSunflow.options(SunflowAPI.DEFAULT_OPTIONS);

        //        Hashtable<String, RenderObject> mScene = mSunflow.inspect();
        //        for (Map.Entry<String, RenderObject> entry : mScene.entrySet()) {
        //            String key = entry.getKey();
        //            RenderObject value = entry.getValue();
        //            System.out.println("Key: " + key + " Value: " + value);
        //        }
        //        System.out.println(mSunflow);

        mSunflow.render(SunflowAPI.DEFAULT_OPTIONS, _myDisplay);
    }

    public static class MyCustomShader
            implements org.sunflow.core.Shader {

        /**
         * Gets the radiance for a specified rendering state. When this method is
         * called, you can assume that a hit has been registered in the state and
         * that the hit surface information has been computed.
         *
         * @param state current render state
         * @return color emitted or reflected by the shader
         */
        public org.sunflow.image.Color getRadiance(ShadingState state) {
            return org.sunflow.image.Color.GREEN;
        }

        /**
         * Scatter a photon with the specied power. Incoming photon direction is
         * specified by the ray attached to the current render state. This method
         * can safely do nothing if photon scattering is not supported or relevant
         * for the shader type.
         *
         * @param state current state
         * @param power power of the incoming photon.
         */
        public void scatterPhoton(ShadingState state, org.sunflow.image.Color power) {
        }

        /**
         * Update this object given a list of parameters. This method is guarenteed
         * to be called at least once on every object, but it should correctly
         * handle empty parameter lists. This means that the object should be in a
         * valid state from the time it is constructed. This method should also
         * return true or false depending on whether the update was succesfull or
         * not.
         *
         * @param pl  list of parameters to read from
         * @param api reference to the current scene
         * @return <code>true</code> if the update is succesfull,
         * <code>false</code> otherwise
         */
        public boolean update(ParameterList pl, SunflowAPI api) {
            return true;
        }
    }

    private static class MyFrameDisplay
            implements Display {

        private String filename;

        private RenderFrame frame;

        private RendererSunflow mRenderer;

        public MyFrameDisplay(RendererSunflow pRenderer) {
            this(null, pRenderer);
        }

        public MyFrameDisplay(String filename, RendererSunflow pRenderer) {
            this.filename = filename;
            mRenderer = pRenderer;
            frame = null;
        }

        public void stop() {
            /* zauberlehring */
        }

        public void imageBegin(int w, int h, int bucketSize) {
            if (frame == null) {
                frame = new RenderFrame(this);
                frame.imagePanel.imageBegin(w, h, bucketSize);
                Dimension screenRes = Toolkit.getDefaultToolkit().getScreenSize();
                boolean needFit = false;
                if (w >= (screenRes.getWidth() - 200) || h >= (screenRes.getHeight() - 200)) {
                    frame.imagePanel.setPreferredSize(new Dimension((int) screenRes.getWidth() - 200,
                                                                    (int) screenRes.getHeight() - 200));
                    needFit = true;
                } else {
                    frame.imagePanel.setPreferredSize(new Dimension(w, h));
                }
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                if (needFit) {
                    frame.imagePanel.fit();
                }
            } else {
                frame.imagePanel.imageBegin(w, h, bucketSize);
            }
        }

        public void imagePrepare(int x, int y, int w, int h, int id) {
            frame.imagePanel.imagePrepare(x, y, w, h, id);
        }

        public void imageUpdate(int x, int y, int w, int h, org.sunflow.image.Color[] data, float[] alpha) {
            frame.imagePanel.imageUpdate(x, y, w, h, data, alpha);
        }

        public void imageFill(int x, int y, int w, int h, org.sunflow.image.Color c, float alpha) {
            frame.imagePanel.imageFill(x, y, w, h, c, alpha);
        }

        public void imageEnd() {
            frame.imagePanel.imageEnd();
            if (filename != null) {
                frame.imagePanel.save(filename);
            }
            if (auto_dispose_frame) {
                frame.dispose();
                frame = null;
            }
        }

        @SuppressWarnings("serial")
        private static class RenderFrame
                extends JFrame {

            ImagePanel imagePanel;

            MyFrameDisplay mParent;

            RenderFrame(MyFrameDisplay pParent) {
                super("Sunflow v" + SunflowAPI.VERSION);
                mParent = pParent;
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            setVisible(false);
                            mParent.stop();
                            System.out.println("### why not stop the render thread, eh?");
                            dispose();
                        }
                    }
                });
                imagePanel = new ImagePanel();
                setContentPane(imagePanel);
                pack();
            }
        }
    }
}
