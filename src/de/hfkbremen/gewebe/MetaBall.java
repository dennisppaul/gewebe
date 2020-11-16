package de.hfkbremen.gewebe;

import processing.core.PVector;

public class MetaBall {

    public PVector position;
    public float radius;
    public float strength;

    public MetaBall(PVector pPosition, float pStrength, float pRadius) {
        position = pPosition;
        strength = pStrength;
        radius = pRadius;
    }
}
