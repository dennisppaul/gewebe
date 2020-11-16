package de.hfkbremen.gewebe;

public class Vector3i {

    int x;
    int y;
    int z;

    public Vector3i(int pX, int pY, int pZ) {
        set(pX, pY, pZ);
    }

    public void set(int pX, int pY, int pZ) {
        x = pX;
        y = pY;
        z = pZ;
    }
}
