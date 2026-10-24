package org.game;

import java.util.Hashtable;

public class GameNode {
    
    public final Vec3 position = new Vec3();
    public final Vec3 r = new Vec3(1, 0, 0);
    public final Vec3 u = new Vec3(0, 1, 0);
    public final Vec3 f = new Vec3(0, 0, 1);
    public final Vec3 scale = new Vec3(1, 1, 1);
    public final Hashtable<String, Object> data = new Hashtable<>();

    private Mat4 m = new Mat4();

    public void rotate(int axis, float degrees) {
        if(axis == 0) {
            m.identity().rotate((float)Math.toRadians(degrees), r);
            u.mulDirection(m).normalize();
            f.mulDirection(m).normalize();
        } else if(axis == 1) {
            m.identity().rotate((float)Math.toRadians(degrees), u);
            r.mulDirection(m).normalize();
            f.mulDirection(m).normalize();
        } else if(axis == 2) {
            m.identity().rotate((float)Math.toRadians(degrees), f);
            u.mulDirection(m).normalize();
            r.mulDirection(m).normalize();
        }
    }

    public Mat4 calcModel(Mat4 model) {
        model.identity()
            .translate(position)
            .mul(
                r.x, r.y, r.z, 0,
                u.x, u.y, u.z, 0,
                f.x, f.y, f.z, 0,
                0, 0, 0, 1
            )
            .scale(scale);
        return model;
    }
}
