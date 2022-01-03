package gewebe;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class IsoSurface2 {

    /**
     * cleaned up version of http://v3ga.free.fr 'liquid balls'
     */
    private static final int[][] mSquareEdge = {{0, 1}, {1, 2}, {2, 3}, {3, 0}};

    private static final int[][] mOffset = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};

    private static final int[][] mLine = {{-1, -1, -1, -1, -1},
                                          {0, 3, -1, -1, -1},
                                          {0, 1, -1, -1, -1},
                                          {3, 1, -1, -1, -1},
                                          {1, 2, -1, -1, -1},
                                          {1, 2, 0, 3, -1},
                                          {0, 2, -1, -1, -1},
                                          {3, 2, -1, -1, -1},
                                          {3, 2, -1, -1, -1},
                                          {0, 2, -1, -1, -1},
                                          {3, 2, 0, 2, -1},
                                          {1, 2, -1, -1, -1},
                                          {3, 1, -1, -1, -1},
                                          {0, 1, -1, -1, -1},
                                          {0, 3, -1, -1, -1},
                                          {-1, -1, -1, -1, -1}};

    public static ArrayList<ArrayList<PVector>> extractBlobs(ArrayList<Linef> pLines) {
        ArrayList<Linef> mLines = new ArrayList<Linef>(pLines);
        ArrayList<ArrayList<PVector>> mBlobs = new ArrayList<>();
        while (!mLines.isEmpty()) {
            ArrayList<PVector> mBlob = extractBlob(mLines);
            if (mBlob != null) {
                mBlobs.add(mBlob);
            }
        }
        return mBlobs;
    }

    public static ArrayList<ArrayList<PVector>> getBlobs(float[][] theArray, float theIsoValue) {
        return extractBlobs(getLines(theArray, theIsoValue));
    }

    public static ArrayList<Linef> getLines(float[][] theArray, float theIsoValue) {
        final ArrayList<Linef> myLines = new ArrayList<>();
        for (int x = 0; x < theArray.length - 1; x++) {
            for (int y = 0; y < theArray[x].length - 1; y++) {
                int square_idx = 0;
                if (theArray[x][y] < theIsoValue) {
                    square_idx |= 1;
                }
                if (theArray[x][y + 1] < theIsoValue) {
                    square_idx |= 2;
                }
                if (theArray[x + 1][y + 1] < theIsoValue) {
                    square_idx |= 4;
                }
                if (theArray[x + 1][y] < theIsoValue) {
                    square_idx |= 8;
                }
                if (square_idx != 0 || square_idx != 15) {
                    int n = 0;
                    while (mLine[square_idx][n] != -1) {
                        Linef myLine = new Linef();
                        myLines.add(myLine);
                        getPoint(theArray, theIsoValue, x, y, myLine.p1, mLine[square_idx][n++]);
                        getPoint(theArray, theIsoValue, x, y, myLine.p2, mLine[square_idx][n++]);
                    }
                }
            }
        }

        return myLines;
    }

    public static PVector[] getLinesAsArray(float[][] theArray, float theIsoValue) {
        ArrayList<Linef> myLines = getLines(theArray, theIsoValue);
        PVector[] myLinesArray = new PVector[myLines.size() * 2];
        int i = 0;
        for (Linef myLine : myLines) {
            myLinesArray[i++] = myLine.p1;
            myLinesArray[i++] = myLine.p2;
        }
        return myLinesArray;
    }

    private static boolean close(PVector a, PVector b) {
        final float mEpsilon = 0.001f;
        return a.dist(b) < mEpsilon;
    }

    private static ArrayList<PVector> extractBlob(ArrayList<Linef> pLines) {
        ArrayList<PVector> mBlob = new ArrayList<>();
        Linef mFirstLine = pLines.remove(0);
        mBlob.add(mFirstLine.p1);
        mBlob.add(mFirstLine.p2);

        int mShapesAdded;
        do {
            mShapesAdded = 0;
            for (Iterator<Linef> iterator = pLines.iterator(); iterator.hasNext(); ) {
                Linef l = iterator.next();
                PVector mFirst = mBlob.get(0);
                PVector mLast = mBlob.get(mBlob.size() - 1);
                if (close(l.p2, mFirst) && close(l.p1, mLast)) {
                    iterator.remove();
                    return mBlob;
                } else if (close(l.p1, mFirst) && close(l.p2, mLast)) {
                    iterator.remove();
                    return mBlob;
                } else if (close(l.p1, mFirst)) {
                    mBlob.add(0, l.p1);
                    mBlob.add(0, l.p2);
                    mShapesAdded++;
                    iterator.remove();
                } else if (close(l.p2, mFirst)) {
                    mBlob.add(0, l.p2);
                    mBlob.add(0, l.p1);
                    mShapesAdded++;
                    iterator.remove();
                } else if (close(l.p1, mLast)) {
                    mBlob.add(l.p1);
                    mBlob.add(l.p2);
                    mShapesAdded++;
                    iterator.remove();
                } else if (close(l.p2, mLast)) {
                    mBlob.add(l.p2);
                    mBlob.add(l.p1);
                    mShapesAdded++;
                    iterator.remove();
                }
            }
        } while (mShapesAdded > 0);
        final boolean RETURN_CLOSED_ONLY = false;
        return RETURN_CLOSED_ONLY ? null : (mBlob.size() == 2 ? null : mBlob);
    }

    private static void getPoint(float[][] theArray, float theIsoValue, int x, int y, PVector thePoint,
                                 int theEdgeIDx) {
        final int P1_idx = mSquareEdge[theEdgeIDx][0];
        final int P2_idx = mSquareEdge[theEdgeIDx][1];

        final float myValueA = theArray[x + mOffset[P1_idx][1]][y + mOffset[P1_idx][0]];
        final float myValueB = theArray[x + mOffset[P2_idx][1]][y + mOffset[P2_idx][0]];
        final float temp;
        if (myValueB - myValueA != 0) {
            temp = (theIsoValue - myValueA) / (myValueB - myValueA);
        } else {
            temp = 0.5f;
        }

        thePoint.y = (y + mOffset[P1_idx][0]) + temp * ((y + mOffset[P2_idx][0]) - (y + mOffset[P1_idx][0]));
        thePoint.x = (x + mOffset[P1_idx][1]) + temp * ((x + mOffset[P2_idx][1]) - (x + mOffset[P1_idx][1]));
    }
}
