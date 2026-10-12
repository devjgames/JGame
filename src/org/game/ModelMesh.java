package org.game;

import java.util.Vector;

public class ModelMesh extends TaggedResource {
    
    public final AABB bounds = new AABB();
    public final Mat4 model = new Mat4();
    public final Mat4 modelIT = new Mat4();

    private Vector<ModelMeshPart> parts = new Vector<>();

    public ModelMesh() {
    }

    public int getPartCount() {
        return parts.size();
    }

    public ModelMeshPart getPart(int i) {
        return parts.get(i);
    }

    public void addPart(ModelMeshPart part) {
        parts.add(part);
    }

    public void calcBounds(VertexAttribute ... attributes) throws Exception {
        bounds.clear();
        for(ModelMeshPart part : parts) {
            part.calcBounds(attributes);
            bounds.union(part.bounds);
        }
    }

    @Override
    public void destroy() {
        for(ModelMeshPart part : parts) {
            part.destroy();
        }
        parts.clear();

        super.destroy();
    }
}
