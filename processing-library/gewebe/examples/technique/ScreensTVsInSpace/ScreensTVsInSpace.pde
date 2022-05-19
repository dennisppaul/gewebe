import gewebe.*; 
import org.sunflow.*; 
/*
 * this example shows how to draw on @{@link gewebe.TV} *screens* and arrange them in a 3D space. use arrow keys to
 * navigate space.
 */

MyTV mScreen;

float mCameraPositionAngle;

float mCameraPositionRadius;

void settings() {
    size(1024, 768, P3D);
}

void setup() {
    imageMode(CENTER);
    mScreen = new MyTV(this);
    mCameraPositionAngle = 0;
    mCameraPositionRadius = width;
}

void draw() {
    mScreen.update();
    background(50);
    PVector mCamera = getCameraPosition();
    camera(mCamera.x, mCamera.y, mCamera.z, 0, 0, 0, 0, 0, -1);
    final float mBounds = 1000;
    noFill();
    stroke(255);
    ellipse(0, 0, mBounds * 2, mBounds * 2);
    translate(0, 0, 200);
    for (float r = 0; r < TWO_PI; r += TWO_PI * 0.05f) {
        float x = sin(r) * mBounds;
        float y = cos(r) * mBounds;
        pushMatrix();
        translate(x, y);
        rotateZ(PI - r);
        scale(0.35f);
        mScreen.drawTV(g, color(0), color(0));
        popMatrix();
    }
}

void keyPressed() {
    final float mStepA = 0.1f;
    final float mStepR = 100.0f;
    switch (keyCode) {
        case LEFT:
            mCameraPositionAngle += mStepA;
            break;
        case RIGHT:
            mCameraPositionAngle -= mStepA;
            break;
        case UP:
            mCameraPositionRadius -= mStepR;
            break;
        case DOWN:
            mCameraPositionRadius += mStepR;
            break;
    }
}

PVector getCameraPosition() {
    PVector p = new PVector();
    p.x = sin(mCameraPositionAngle) * mCameraPositionRadius;
    p.y = cos(mCameraPositionAngle) * mCameraPositionRadius;
    p.z = (height / 2.0f) / tan(PI * 30.0f / 180.0f);
    return p;
}
static class MyTV extends TV {
    MyTV(PApplet pParent) {
        super(pParent);
    }
    
void settings() {
        size(640, 480);
    }
    
void setup(PGraphics graphics) {
        /* call processing methods with a `parent.` e.g: */
        System.out.println("parent screen height is " + parent.height);
        /* call processing drawing methods with a `graphics.` e.g: */
        graphics.background(255);
    }
    
void draw(PGraphics graphics) {
        /* call processing drawing methods with a `graphics.` e.g: */
        graphics.stroke(0);
        float x = parent.random(0, graphics.width);
        float y = parent.random(0, graphics.height);
        float d = parent.random(50, 100);
        graphics.ellipse(x, y, d, d);
    }
}
