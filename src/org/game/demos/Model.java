package org.game.demos;

import java.io.File;

import org.game.BasicRenderer;
import org.game.Frustum;
import org.game.GameComponent;
import org.game.GameNode;
import org.game.GameSystem;
import org.game.MeshLoader;
import org.game.Mesh;
import org.game.MeshPart;
import org.game.RenderPass;
import org.game.Vec3;
import org.game.Vec4;

public class Model extends GameComponent {

    private String meshName;

    public Model(String meshName) {
        this.meshName = meshName;
    }

    public String getMeshName() {
        return meshName;
    }

    @Override
    public void init(GameSystem gs, GameNode node) throws Exception {
        Mesh mesh = gs.game.getResources().load(new File(meshName));

        if(meshName.equals("map1.obj")) {
            mesh.put(MeshLoader.AMBIENT_COLOR, new Vec4(0.5f, 0.5f, 0.5f, 1));
        } else if(meshName.equals("door.obj")) {
            mesh.put(MeshLoader.AMBIENT_COLOR, new Vec4(0.25f, 0.25f, 0.25f, 1));
            node.data.put("y", node.position.y);
        } else if(meshName.equals("torch.obj")) {
            mesh.put(MeshLoader.AMBIENT_COLOR, new Vec4(0, 0, 0, 1));
            mesh.put(MeshLoader.DIFFUSE_COLOR, new Vec4(0, 0, 0, 1));
        } else if(meshName.equals("axis.obj")) {
            mesh.put(MeshLoader.AMBIENT_COLOR, new Vec4(1, 1, 1, 1));
            mesh.put(MeshLoader.DIFFUSE_COLOR, new Vec4(1, 1, 1, 1));
        }
        node.data.put("mesh", mesh);
    }

    @Override
    public void update(GameSystem gs, GameNode node) throws Exception {
        if(gs.inDesign()) {
            return;
        }

        if(meshName.equals("door.obj")) {
            float ex = gs.eye.x;
            float ez = gs.eye.z;
            float px = node.position.x;
            float pz = node.position.z;
            float dx = ex - px;
            float dz = ez - pz;
            float d = (float)Math.sqrt(dx * dx + dz * dz);
            float a = 1 - Math.min(d / 60, 1);
            float y = (Float)node.data.get("y");

            node.position.y = y - a * 150;
        }
    }

    @Override
    public boolean pick(GameSystem gs, GameNode node, Vec3 origin, Vec3 direction, float[] time) throws Exception {
        Mesh mesh = (Mesh)node.data.get("mesh");

        mesh.calcBoundsAndTransform(node);

        return mesh.pick(origin, direction, time);
    }

    @Override
    public void render(GameSystem gs, GameNode node, BasicRenderer basicRenderer, RenderPass renderPass) throws Exception {
        if(meshName.equals("axis.obj") && !gs.inDesign()) {
            return;
        }
        if(renderPass != RenderPass.SOLID) {
            return;
        }

        Mesh mesh = (Mesh)node.data.get("mesh");

        mesh.calcBoundsAndTransform(node);

        if(gs.frustum.intersectAab(
            mesh.bounds.minX, mesh.bounds.minY, mesh.bounds.minZ,
            mesh.bounds.maxX, mesh.bounds.maxY, mesh.bounds.maxZ
        ) != Frustum.OUTSIDE) {
            for(int j = 0; j != mesh.getPartCount(); j++) {
                MeshPart part = mesh.getPart(j);

                if(gs.frustum.intersectAab(
                    part.bounds.minX, part.bounds.minY, part.bounds.minZ,
                    part.bounds.maxX, part.bounds.maxY, part.bounds.maxZ
                ) != Frustum.OUTSIDE) {

                    basicRenderer.setModel(mesh.model);
                    basicRenderer.setModelIT(mesh.modelIT);
                    basicRenderer.setAmbientColor(mesh.get(MeshLoader.AMBIENT_COLOR));
                    basicRenderer.setDiffuseColor(mesh.get(MeshLoader.DIFFUSE_COLOR));
                    basicRenderer.setTexture(part.get(MeshLoader.TEXTURE));

                    DecalRenderer dr = (DecalRenderer)basicRenderer;

                    if(meshName.equals("door.obj")) {
                        dr.setDecal(gs.game.getResources().load(new File("door-glow.png")));
                    } else {
                        dr.setDecal(null);
                    }

                    basicRenderer.setLightingEnabled(true);

                    basicRenderer.draw(part.vertexBuffer, part.vertexElementBuffer);
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return meshName;
    }
}
