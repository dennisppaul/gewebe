package gewebe;

import processing.core.PVector;
import quickhull3d.Point3d;
import quickhull3d.QuickHull3D;

import java.util.ArrayList;

public class ConvexHull3 {

    public static float[] hull_vertices(ArrayList<PVector> pVertices) {
        PVector[] mVertices = new PVector[pVertices.size()];
        pVertices.toArray(mVertices);
        return hull_vertices(mVertices);
    }

    public static float[] hull_vertices(PVector[] pVertices) {
        final QuickHull3D mHull = new QuickHull3D();
        final ArrayList<Float> mVertexData = new ArrayList<>();

        final Point3d[] mVertices = new Point3d[pVertices.length];
        for (int i = 0; i < pVertices.length; i++) {
            mVertices[i] = new Point3d(pVertices[i].x, pVertices[i].y, pVertices[i].z);
        }

        mHull.build(mVertices);
        mHull.triangulate();
        final Point3d[] vertices = mHull.getVertices();

        final int[][] mFaces = mHull.getFaces();
        for (int[] mIndices : mFaces) {
            for (int i : mIndices) {
                final Point3d p = vertices[i];
                mVertexData.add((float) p.x);
                mVertexData.add((float) p.y);
                mVertexData.add((float) p.z);
            }
        }

        final float[] mVertexDataf = new float[mVertexData.size()];
        for (int i = 0; i < mVertexData.size(); i++) {
            mVertexDataf[i] = mVertexData.get(i);
        }
        return mVertexDataf;
    }
}
