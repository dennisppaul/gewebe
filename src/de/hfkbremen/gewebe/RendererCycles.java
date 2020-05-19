package de.hfkbremen.gewebe;

import org.xml.sax.SAXException;
import processing.core.PApplet;
import processing.data.XML;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static de.hfkbremen.gewebe.Location.exists;

public class RendererCycles extends RendererMesh {

    /**
     * # Cycles Resources
     *
     * - [Cycles @ Blender](https://developer.blender.org/project/view/26/)
     * - [Cycles Open Source Production Rendering](https://www.cycles-renderer.org/development/)
     */

    private static final String XML_NODE_CYCLES = "cycles";
    private static final String XML_NODE_CAMERA = "camera";
    private static final String XML_NODE_BACKGROUND = "background";
    private static final String XML_NODE_MESH = "mesh";
    private static final String XML_NODE_OBJECT = "state";
    private static final String XML_NODE_CONNECT = "connect";
    private static final String XML_NODE_DIFFUSE = "diffuse_bsdf";
    private static final String XML_NODE_TRANSFORM = "transform";
    private static final String XML_ATTR_WIDTH = "width";
    private static final String XML_ATTR_HEIGHT = "height";
    private static final String XML_ATTR_TYPE = "type";
    private static final String XML_ATTR_MATRIX = "matrix";
    private static final String XML_ATTR_NAME = "name";
    private static final String XML_ATTR_POINTS = "P";
    private static final String XML_ATTR_NVERTS = "nverts";
    private static final String XML_ATTR_VERTS = "verts";
    private static final String XML_ATTR_COLOR = "color";
    private static final String XML_ATTR_FROM = "from";
    private static final String XML_ATTR_TO = "to";
    private static final String XML_UNI_SHADER = "shader";
    private static final String VALUE_FROM_DIFFUSE = "diffuse bsdf";
    private static final String VALUE_OUTPUT_SURFACE = "output surface";
    private static final String SHADER_TYPE_DIFFUSE = "diffuse";
    private static final char DELIMITER = ' ';
    private static final String SHADER_NAME = "s_";
    private static final String OPTION_SAMPLES = "--samples";
    private static final String OPTION_BACKGROUND = "--background";
    private static final String OPTION_OUTPUT = "--output";
    private static final String OPTION_WIDTH = "--width";
    private static final String OPTION_HEIGHT = "--height";
    public static float RENDER_VIEWPORT_SCALE = 1.0f;
    public static boolean DEBUG_PRINT_CYCLES_BINARY_LOCATION = false;
    public static boolean DEBUG_PRINT_CAMERA_MATRIX = false;
    public static String IMAGE_FILE_TYPE_PNG = ".png";
    public static String SCENE_FILE_TYPE = ".xml";
    public static String IMAGE_FILE_TYPE_JPG = ".jpg";
    public static String IMAGE_FILE_TYPE_TGA = ".tga";
    public static String OUTPUT_IMAGE_FILE_TYPE = IMAGE_FILE_TYPE_PNG;
    public static String CYCLES_BINARY_NAME = "cycles";
    public static String CYCLES_BINARY_PATH = null;
    public static int NUMBER_OF_SAMPLES = 10;
    public static String CAMERA_TYPE_PERSPECTIVE = "perspective";
    public static String CAMERA_TYPE = CAMERA_TYPE_PERSPECTIVE;
    public static Color BACKGROUND_COLOR = new Color(0.0f);
    public static boolean KEEP_XML_SCENE_FILE = true;
    public static boolean RENDER_IMAGE = true;
    public static boolean PARSE_COLORS_AS_CUSTOM_CYCLES_SHADERS = false;
    private final ArrayList<CyclesShader> mShaders = new ArrayList<CyclesShader>();
    private XML mXML;
    private String mExecPath;
    private int mShaderNameID = 0;

    public RendererCycles() {
        final String pPathToCycles =
                (CYCLES_BINARY_PATH == null ? getLocation() : CYCLES_BINARY_PATH) + "/" + CYCLES_BINARY_NAME;
        mExecPath = de.hfkbremen.gewebe.Location.get(pPathToCycles);
        if (!exists(mExecPath)) {
            /* try default location */
            error("couldn t find `cycles` at location `" + mExecPath + "` trying default location `" + de.hfkbremen.gewebe.Location
                    .get(
                            CYCLES_BINARY_NAME) + "`");
            mExecPath = Location.get(CYCLES_BINARY_NAME);
            if (!exists(mExecPath)) {
                error("couldn t find `cycles` at default location. try to set path manually via `RendererCycles" +
                      ".CYCLES_BINARY_PATH`.");
            }
        }
        if (DEBUG_PRINT_CYCLES_BINARY_LOCATION && exists(mExecPath)) {
            System.out.println("### found `cycles` at location " + mExecPath);
        }
    }

    public static String DEBUG_output_path() {
        return System.getProperty("user.home") + "/Desktop/cycles/";
    }

    public static RendererCycles create(PApplet pApplet, String pOutputFile) {
        return (RendererCycles) pApplet.createGraphics(pApplet.width, pApplet.height, name(), pOutputFile);
    }

    public static String name() {
        return RendererCycles.class.getName();
    }

    public static String getColorAttr(float r, float g, float b) {
        final String D = ", ";
        return r + D + g + D + b;
    }

    public static XML createShaderDiffuse(String pShaderName, float r, float g, float b) {
        final float mRoughness = 0.0f;
        XML mShaderNode = new XML(XML_UNI_SHADER);
        mShaderNode.setString(XML_ATTR_NAME, pShaderName);

        XML mDiffuseNode = new XML(XML_NODE_DIFFUSE);
        mDiffuseNode.setString(XML_ATTR_NAME, SHADER_TYPE_DIFFUSE);
        mDiffuseNode.setFloat("roughness", mRoughness);
        mDiffuseNode.setString(XML_ATTR_COLOR, getColorAttr(r, g, b));
        mShaderNode.addChild(mDiffuseNode);

        XML mConnectNode = new XML(XML_NODE_CONNECT);
        mConnectNode.setString(XML_ATTR_FROM, VALUE_FROM_DIFFUSE);
        mConnectNode.setString(XML_ATTR_TO, VALUE_OUTPUT_SURFACE);
        mShaderNode.addChild(mConnectNode);

        return mShaderNode;
    }

    public static XML getXML(String pXMLString) {
        XML mXML = null;
        try {
            mXML = XML.parse(pXMLString);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return mXML;
    }

    public void registerShader(int pColorID, XML pPayload) {
        CyclesShader m = new CyclesShader(pColorID, pPayload);
        CyclesShader mQuery = findShader(m);
        if (mQuery == null) {
            mShaders.add(m);
        } else {
            console("@registerShader warning, shader ID already registered: " + pColorID);
            console("new shader ........ : " + m);
            console("existing shader ... : " + mQuery);
        }
    }

    protected void beginFrame() {
    }

    protected void endFrame() {
    }

    protected void prepareFrame() {
    }

    protected void finalizeFrame() {
        File mXMLOutputFile = null;

        if (path != null) {
            mXMLOutputFile = new File(path + SCENE_FILE_TYPE);
            if (!mXMLOutputFile.isAbsolute()) {
                mXMLOutputFile = null;
            }
        }
        if (mXMLOutputFile == null) {
            error("could not create output file `" + path + "`");
        }
        if (mXML == null) {
            mXML = new XML(XML_NODE_CYCLES);
        }

        buildCamera(mXML);
        buildBackground(mXML);

        for (ShaderTriangleBucket s : bucket()) {
            String mCurrentShaderName = SHADER_NAME + PApplet.nf(mShaderNameID++, 4);
            if (PARSE_COLORS_AS_CUSTOM_CYCLES_SHADERS) {
                if (findShader(s.shaderID) != null) {
                    mCurrentShaderName = buildShaderFromXML(mXML, s);
                } else {
                    buildShaderFromRGBColor(mXML, mCurrentShaderName, s.color.r, s.color.g, s.color.b);
                }
            } else {
                buildShaderFromRGBColor(mXML, mCurrentShaderName, s.color.r, s.color.g, s.color.b);
            }
            buildObject(mXML, mCurrentShaderName, toArray(s.vertices));
        }

        if (mXML != null && mXMLOutputFile != null) {
            mXML.save(mXMLOutputFile);
            if (RENDER_IMAGE) {
                compileRenderCLICommands(mXMLOutputFile.getPath());
            }
            mXML = null;
            if (!KEEP_XML_SCENE_FILE) {
                if (!mXMLOutputFile.delete()) { error("could not remove XML file at " + mXMLOutputFile.getPath()); }
            }
        }
    }

    private CyclesShader findShader(CyclesShader pShader) {
        for (CyclesShader n : mShaders) {
            if (n.ID == pShader.ID) {
                return n;
            }
        }
        return null;
    }

    private CyclesShader findShader(int pID) {
        for (CyclesShader n : mShaders) {
            if (n.ID == pID) {
                return n;
            }
        }
        return null;
    }

    private void buildCamera(XML pXML) {
        //        if (!(g instanceof PGraphics3D)) {
        //            error("camera requires a `PGraphics3D` context to function");
        //        }
        int mWidth = width;
        int mHeight = height;
        float[] mCameraMatrix = new float[16];
        getMatrix().get(mCameraMatrix);
        mCameraMatrix[14] = height;// TODO find out why this is necessary / still off 105%
        mCameraMatrix[0] *= 1;
        mCameraMatrix[5] *= -1;
        mCameraMatrix[10] *= -1;

        float[] mParentCameraMatrix = new float[16];
        parent.g.getMatrix().get(mParentCameraMatrix);

        // @DEBUG
        if (DEBUG_PRINT_CAMERA_MATRIX) {
            for (int i = 0; i < mParentCameraMatrix.length; i++) {
                System.out.print(mParentCameraMatrix[i] + ", ");
                if (i % 4 == 3) {
                    System.out.println();
                }
            }
        }

        {
            XML mCamera = new XML(XML_NODE_CAMERA);
            mCamera.setInt(XML_ATTR_WIDTH, mWidth);
            mCamera.setInt(XML_ATTR_HEIGHT, mHeight);
            pXML.addChild(mCamera);
        }
        {
            XML mCameraTransform = new XML(XML_NODE_TRANSFORM);
            setMatrix4x4(mCameraTransform, mCameraMatrix);
            XML mCamera = new XML(XML_NODE_CAMERA);
            mCamera.setString(XML_ATTR_TYPE, CAMERA_TYPE);
            mCameraTransform.addChild(mCamera);
            pXML.addChild(mCameraTransform);
        }
        //          <camera width="800" height="500" />
        //          <transform matrix="1 0 0 0
        //                             0 1 0 0
        //                             0 0 1 0
        //                             0 0 -4 1">
        //              <camera type="perspective" />
        //          </transform>
    }

    private void buildBackground(XML pXML) {
        final String BACKGROUND_NAME = "bg";
        XML mBackgroundNode = new XML(XML_NODE_BACKGROUND);
        {
            XML mBackgroundPropertyNode = new XML(XML_NODE_BACKGROUND);
            mBackgroundPropertyNode.setString(XML_ATTR_NAME, BACKGROUND_NAME);
            mBackgroundPropertyNode.setFloat("strength", 2.0f);
            mBackgroundPropertyNode.setString(XML_ATTR_COLOR,
                                              getColorAttr(BACKGROUND_COLOR.r,
                                                           BACKGROUND_COLOR.g,
                                                           BACKGROUND_COLOR.b));
            mBackgroundPropertyNode.setFloat("SurfaceMixWeight", 1.0f);
            mBackgroundNode.addChild(mBackgroundPropertyNode);

            XML mConnectNode = new XML(XML_NODE_CONNECT);
            mConnectNode.setString(XML_ATTR_FROM, BACKGROUND_NAME + DELIMITER + "background");
            mConnectNode.setString(XML_ATTR_TO, VALUE_OUTPUT_SURFACE);
            mBackgroundNode.addChild(mConnectNode);
        }
        pXML.addChild(mBackgroundNode);
        //      <background>
        //	        <background name="bg" strength="2.0" color="0.5, 0.5, 0.5" SurfaceMixWeight="1.0"/>
        //	        <connect from="bg background" to="output surface" />
        //      </background>
    }

    private void buildShaderFromRGBColor(XML pXML, String pShaderName, float r, float g, float b) {
        pXML.addChild(createShaderDiffuse(pShaderName, r, g, b));
        //      <shader name="floor">
        //	        <diffuse_bsdf name="diffuse" roughness="0.0" color="1.0, 0.5, 0.0" />
        //	        <connect from="diffuse bsdf" to="output surface" />
        //      </shader>
    }

    private String buildShaderFromXML(XML pXML, ShaderTriangleBucket pBucket) {
        final String DEFAULT_SHADER_NAME = "default_shader";
        CyclesShader mShader = findShader(pBucket.shaderID);
        if (mShader != null) {
            pXML.addChild(mShader.payload);
            final String mShaderName = mShader.payload.getString(XML_ATTR_NAME);
            return mShaderName == null ? "" : mShaderName;
        } else {
            console("@buildShaderFromXML could not find shader. using default shader instead.");
            Color c = pBucket.color;
            buildShaderFromRGBColor(mXML, DEFAULT_SHADER_NAME, c.r, c.g, c.b);
            return DEFAULT_SHADER_NAME;
        }
    }

    private void buildObject(XML pXML, String pShaderName, float[] pTriangleList) {
        XML mObjNode = new XML(XML_NODE_OBJECT);
        mObjNode.setString(XML_UNI_SHADER, pShaderName);
        XML mMeshNode = buildMesh(pTriangleList);
        mObjNode.addChild(mMeshNode);
        pXML.addChild(mObjNode);
        //      <state shader="floor">
        //	        <mesh P="-3 3 0  3 3 0  3 -3 0  -3 -3 0" nverts="4" verts="0 1 2 3" />
        //      </state>
    }

    private XML buildMesh(float[] pTriangleList) {
        StringBuilder mPoints = new StringBuilder();
        StringBuilder mNumberOfVertices = new StringBuilder();
        StringBuilder mVertexList = new StringBuilder();

        for (int i = 0; i < pTriangleList.length; i += 9) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    mPoints.append(pTriangleList[i + j * 3 + k]).append(DELIMITER);
                }
                mPoints.append(DELIMITER);
                mVertexList.append(i / 3 + j).append(DELIMITER);
            }
            mVertexList.append(DELIMITER);
            mNumberOfVertices.append(3).append(DELIMITER);
        }

        XML mMeshNode = new XML(XML_NODE_MESH);
        mMeshNode.setString(XML_ATTR_POINTS, mPoints.toString());
        mMeshNode.setString(XML_ATTR_NVERTS, mNumberOfVertices.toString());
        mMeshNode.setString(XML_ATTR_VERTS, mVertexList.toString());
        return mMeshNode;
        //	        <mesh P="-3 3 0  3 3 0  3 -3 0  -3 -3 0" nverts="4" verts="0 1 2 3" />
        //	        <mesh P="-3 3 0  3 3 0  3 -3 0" nverts="3" verts="0 1 2 " />
    }

    private void setMatrix4x4(XML pNode, float[] pMatrix4x4) {
        StringBuilder s = new StringBuilder();
        for (float aPMatrix4x4 : pMatrix4x4) {
            s.append(aPMatrix4x4);
            s.append(' ');
        }
        pNode.setString(XML_ATTR_MATRIX, s.toString());
    }

    private void compileRenderCLICommands(String pXMLPath) {
        final String mOptionSamplesValue = String.valueOf(NUMBER_OF_SAMPLES);
        final String mOptionOutputValue = path + OUTPUT_IMAGE_FILE_TYPE;
        final String[] mCommandString = new String[]{mExecPath,
                                                     OPTION_SAMPLES,
                                                     mOptionSamplesValue,
                                                     OPTION_BACKGROUND,
                                                     OPTION_WIDTH,
                                                     String.valueOf(width * RENDER_VIEWPORT_SCALE),
                                                     OPTION_HEIGHT,
                                                     String.valueOf(height * RENDER_VIEWPORT_SCALE),
                                                     OPTION_OUTPUT,
                                                     mOptionOutputValue,
                                                     pXMLPath};
        launchRenderProcess(mCommandString);
    }

    public static class CyclesShader {

        public final int ID;
        public final XML payload;

        CyclesShader(int pID, XML pPayload) {
            ID = pID;
            payload = pPayload;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() +
                   "{" +
                   "ID=" + ID +
                   ", payload=" + payload +
                   '}';
        }
    }
}
