package org.game;

import org.joml.GeometryUtils;

public class Triangle {
    
    public final Vec3 p1 = new Vec3();
    public final Vec3 p2 = new Vec3();
    public final Vec3 p3 = new Vec3();
    public final Vec3 n = new Vec3();
    public float d = 0;

    public Triangle() {
    }

    public void calcPlane() {
        GeometryUtils.normal(p1, p2, p3, n);
        n.normalize();
        d = -p1.dot(n);
    }
}
