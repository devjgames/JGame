package org.game;

import java.util.Vector;

public class Model extends TaggedResource {
    
    public final AABB bounds = new AABB();

    private Vector<ModelMesh> meshes = new Vector<>();

    public Model() {
    }

    public int getMeshCount() {
        return meshes.size();
    }

    public ModelMesh getMesh(int i) {
        return meshes.get(i);
    }

    public void addMesh(ModelMesh mesh) {
        meshes.add(mesh);
    }

    public void calcBounds(VertexAttribute ... attributes) throws Exception {
        bounds.clear();
        for(ModelMesh mesh : meshes) {
            mesh.calcBounds(attributes);
            bounds.union(mesh.bounds);
        }
    }

    @Override
    public void destroy() {
        for(ModelMesh mesh : meshes) {
            mesh.destroy();
        }
        meshes.clear();
        
        super.destroy();
    }
}
