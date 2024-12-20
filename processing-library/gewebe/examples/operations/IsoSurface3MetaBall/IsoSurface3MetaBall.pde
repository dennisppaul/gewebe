import gewebe.*; 
import org.sunflow.*; 
static final int             MAXIMUM_META_BALLS = 200;
             ArcBall         mArcBall;
             MetaBallManager mMetaBallManager;
void settings() {
    size(1024, 768, P3D);
}
void setup() {
    mArcBall = new ArcBall(width / 2.0f, height / 2.0f, -height, 400.0f, this, true);
    mMetaBallManager = new MetaBallManager();
    mMetaBallManager.dimension.set(width, height, height);
    mMetaBallManager.resolution.set(50, 50, 50);
    mMetaBallManager.position.set(width / -2.0f, height / -2.0f, height / -2.0f);
    mMetaBallManager.isovalue(5.0f);
}
void draw() {
    background(50);
    directionalLight(126, 126, 126, 0, 0, -1);
    ambientLight(102, 102, 102);
    /* draw iso surface */
    mArcBall.update();
    if (mMetaBallManager.metaballs().size() < MAXIMUM_META_BALLS) {
        mMetaBallManager.add(new MovingMetaBall());
    }
    for (MetaBall m : mMetaBallManager.metaballs()) {
        MovingMetaBall mm = (MovingMetaBall) m;
        mm.update(1.0f / frameRate);
    }
    /* remove metaballs if off the grid */
    final java.util.Iterator<MetaBall> i = mMetaBallManager.metaballs().iterator();
    while (i.hasNext()) {
        final MovingMetaBall c = (MovingMetaBall) i.next();
        if (c.isOffTheGrid()) {
            i.remove();
        }
    }
    final ArrayList<PVector> mData = mMetaBallManager.createSurface();
    /* draw metaballs */
    translate(width / 2.0f, height / 2.0f, -height);
    fill(240);
    noStroke();
    beginShape(TRIANGLES);
    for (PVector mDatum : mData) {
        vertex(mDatum.x, mDatum.y, mDatum.z);
    }
    endShape();
}
void keyPressed() {
    switch (key) {
        case '+':
            mMetaBallManager.isovalue(mMetaBallManager.isovalue() + 0.1f);
            println(mMetaBallManager.isovalue());
            break;
        case '-':
            mMetaBallManager.isovalue(mMetaBallManager.isovalue() - 0.1f);
            println(mMetaBallManager.isovalue());
            break;
    }
}
class MovingMetaBall extends MetaBall {
    final PVector velocity;
    MovingMetaBall() {
        super(new PVector(random(width / -3.0f, width / 3.0f),
                          random(height / -3.0f, height / 3.0f),
                          height / -2.0f), random(1.5f, 3), random(150, 300));
        velocity   = PVector.random3D();
        velocity.z = abs(velocity.z);
        velocity.mult(random(50, 100));
    }
    void update(float pDeltaTime) {
        PVector v = PVector.mult(velocity, pDeltaTime);
        position.add(v);
    }
    boolean isOffTheGrid() {
        return position.x > width / 2.0f || position.x < width / -2.0f || position.y > height / 2.0f || position.y < height / -2.0f || position.z > height / 2.0f || position.z < height / -2.0f;
    }
}
