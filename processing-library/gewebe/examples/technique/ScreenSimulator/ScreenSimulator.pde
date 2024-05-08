import gewebe.*; 
import org.sunflow.*; 
/*
 * this example shows how to draw into an @{@link gewebe.OffscreenContext} and use it as an image.
 */
MyScreen mScreen;
void settings() {
    size(1024, 768, P3D);
}
void setup() {
    mScreen = new MyScreen(this);
}
void draw() {
    mScreen.update();
    background(50);
    /* draw thumbnail */
    image(mScreen.texture(), 10, 10, 64 * 2, 48 * 2);
    /* draw a plane in space */
    translate(mScreen.width / -2.0f, mScreen.height / -2.0f, 0);
    rotateX(sin(frameCount * 0.033f) * 0.1f);
    rotateY(sin(frameCount * 0.05f) * 0.25f);
    translate(0, 0, 50);
    image(mScreen.texture(), mouseX, mouseY);
}
static class MyScreen extends OffscreenContext {
    MyScreen(PApplet pParent) {
        super(pParent);
    }
    void settings() {
        size(640, 480);
    }
    void setup(PGraphics graphics) {
        /* call processing methods with a `parent.` e.g: */
        System.out.println("screen height is " + parent.height);
        /* call processing drawing methods with a `graphics.` e.g: */
        graphics.background(255);
    }
    void draw(PGraphics graphics) {
        graphics.stroke(0);
        float mY = parent.random(0, graphics.height);
        graphics.line(parent.random(0, graphics.width), mY, parent.random(0, graphics.width), mY);
    }
}
