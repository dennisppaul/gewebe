package gewebe.examples.rendering;

import gewebe.RendererCycles;
import processing.core.PApplet;
import processing.data.XML;

public class SketchRenderingWithCyclesAndCustomShaders extends PApplet {

    /*
     * this example demonstrates how to render high-resolution images with [Cycles](https://www.cycles-renderer.org/)
     * a physically based renderer included in the [Blender](https://www.blender.org/) project.
     *
     * it also demonstrates how to use simple processing colors to assign custom *cycles shaders* to geometry.
     */

    private RendererCycles mCycles;
    private boolean        mRecord = false;
    private int            mShaderIDSpheres;
    private int            mShaderIDBlue;
    private int            mShaderIDGlass;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        noStroke();
        sphereDetail(12);

        /* use `color` to assign shader IDs */
        mShaderIDSpheres = color(254);
        mShaderIDBlue    = color(200);
        mShaderIDGlass   = color(1, 0, 0, 0);
    }

    public void draw() {
        String mOutputFile = "";
        if (mRecord) {
            RendererCycles.NUMBER_OF_SAMPLES           = 100;
            RendererCycles.OUTPUT_IMAGE_FILE_TYPE      = RendererCycles.IMAGE_FILE_TYPE_PNG;
            RendererCycles.RENDERING_PROCESS_BLOCKING  = true;
            RendererCycles.DEBUG_PRINT_RENDER_PROGRESS = true;
            RendererCycles.BACKGROUND_COLOR.set(0.5f);
            RendererCycles.RENDER_VIEWPORT_SCALE                 = 1.0f;
            RendererCycles.KEEP_XML_SCENE_FILE                   = true;
            mOutputFile                                          = "cycles-" + nf(frameCount, 4);
            mCycles                                              = RendererCycles.create(this, mOutputFile);
            RendererCycles.PARSE_COLORS_AS_CUSTOM_CYCLES_SHADERS = true;
            /* register shader configuration with ID generated from processing colors */
            mCycles.registerShader(mShaderIDSpheres, createShaderCubeVolume());
            mCycles.registerShader(mShaderIDBlue, createShaderBlue());
            mCycles.registerShader(mShaderIDGlass, createShaderMonkey());
            beginRaw(mCycles);
        }

        drawScene();

        if (mRecord) {
            endRaw();
            saveFrame(mOutputFile + ".screen.png");
            mRecord = false;
        }
    }

    public void keyPressed() {
        if (key == ' ') {
            mRecord = true;
        }
    }

    private XML createShaderCubeVolume() {
        return RendererCycles.getXML(
                "<shader name=\"cube\">\n" +
                "\t<checker_texture name=\"tex\" scale=\"2.0\" color1=\"0.8, 0.8, 0.8\" color2=\"0.2, 0.2, 0.2\" />\n" +
                "\t<math name=\"math\" type=\"multiply\" value2=\"20.0\" />\n" +
                "\n" +
                "\t<scatter_volume name=\"volume1\" color=\"0.8, 0.8, 0.8\" />\n" +
                "\t<absorption_volume name=\"volume2\" color=\"0.8, 0.8, 0.8\" />\n" +
                "\t<add_closure name=\"add\" />\n" +
                "\t\n" +
                "\t<connect from=\"tex fac\" to=\"math value1\" />\n" +
                "\t<connect from=\"math value\" to=\"volume1 density\" />\n" +
                "\t<connect from=\"math value\" to=\"volume2 density\" />\n" +
                "\n" +
                "\t<connect from=\"volume1 volume\" to=\"add closure1\" />\n" +
                "\t<connect from=\"volume2 volume\" to=\"add closure2\" />\n" +
                "\t<connect from=\"add closure\" to=\"output volume\" />\n" +
                "</shader>");
    }

    private XML createShaderBlue() {
        return RendererCycles.getXML(
                "<shader name=\"custom_cycles_shader\"><diffuse_bsdf color=\"0.0, 0.5, 1.0\" " +
                "name=\"diffuse\" roughness=\"0.0\"/><connect from=\"diffuse bsdf\" to=\"output " +
                "surface\"/></shader>");
    }

    private XML createShaderMonkey() {
        return RendererCycles.getXML(
                "<shader name=\"monkey\">\n" +
                "\t<noise_texture name=\"tex\" scale=\"2.0\"/>\n" +
                "\t<glass_bsdf name=\"monkey_closure\" distribution=\"beckmann\" ior=\"1.4\" roughness=\"0.2\" />\n" +
                "\t<connect from=\"tex color\" to=\"monkey_closure color\" />\n" +
                "\t<connect from=\"monkey_closure bsdf\" to=\"output surface\" />\n" +
                "</shader>");
    }

    private XML createShaderXML() {
        XML mShaderNode = new XML("shader");
        mShaderNode.setString("name", "custom_cycles_shader");

        XML mDiffuseNode = new XML("diffuse_bsdf");
        mDiffuseNode.setString("name", "diffuse");
        mDiffuseNode.setFloat("roughness", 0.0f);
        mDiffuseNode.setString("color", RendererCycles.getColorAttr(0, 0.5f, 1.0f));
        mShaderNode.addChild(mDiffuseNode);

        XML mConnectNode = new XML("connect");
        mConnectNode.setString("from", "diffuse bsdf");
        mConnectNode.setString("to", "output surface");
        mShaderNode.addChild(mConnectNode);

        return mShaderNode;
    }

    private void drawScene() {
        background(50);
        float mZoom = 6.0f;
        camera(height / 2.0f / mZoom, height / mZoom, width / mZoom,
               0, 0, 0,
               0, 1, 0);

        /* floor */
        noStroke();
        fill(200); /* note that `color(200)` ( i.e `fill(200)` ) was previously assigned to a custom shader */
        rect(-500, -500, 1000, 1000);

        rotateX(frameCount * 0.007f);
        rotateY(frameCount * 0.013f);

        for (int i = 0; i < 100; i++) {
            pushMatrix();
            noStroke();
            if (i < 50) {
                fill(random(255), random(255), random(255));
            } else {
                fill(mShaderIDGlass);
            }
            final float mRange = 100;
            translate(
                    random(-mRange, mRange),
                    random(-mRange, mRange),
                    random(-mRange, mRange)
                     );
            sphere(random(10, 30));
            popMatrix();
        }

        /* this ID ( color(254) ) assigns the custom cycles shader to geometry */
        fill(mShaderIDSpheres);
        noStroke();
        sphere(100);

        translate(-50, 0);
        sphere(80);

        translate(100, 0);
        sphere(90);
    }

    public static void main(String[] args) {
        PApplet.main(SketchRenderingWithCyclesAndCustomShaders.class.getName());
    }
}
