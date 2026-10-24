package org.game.demos;

import java.io.File;
import java.util.Random;

import org.game.BasicRenderer;
import org.game.BlendState;
import org.game.GFX;
import org.game.GameLight;
import org.game.GameNode;
import org.game.GameSystem;
import org.game.Mesh;
import org.game.Particle;
import org.game.ParticleSystem;
import org.game.RenderPass;
import org.game.Vec4;

public class Torch extends Model {

    private Random random = new Random(100);
    private Particle particle = new Particle();
    private Vec4 color;
    private float radius;

    public Torch(float r, float g, float b, float a, float radius) {
        super("torch.obj");

        color = new Vec4(r, g, b, a);
        
        this.radius = radius;
    }

    @Override
    public GameLight getLight(GameSystem gs, GameNode node, int i) throws Exception {
        if(i == 0) {
            GameLight light = (GameLight)node.data.get("gameLight");

            if(light == null) {
                light = new GameLight();

                light.color.set(color);
                light.radius = radius;

                node.data.put("gameLight", light);
            }

            Mesh mesh = (Mesh)node.data.get("mesh");

            mesh.calcBoundsAndTransform(node);

            light.position.set(0, 0, 0).mulPosition(mesh.model);

            return light;
        }
        return null;
    }

    @Override
    public void init(GameSystem gs, GameNode node) throws Exception {
        super.init(gs, node);

        ParticleSystem ps;

        node.data.put("fire", gs.game.getResources().manage(ps = new ParticleSystem(500)));

        ps.texture = gs.game.getResources().load(new File("fire.png"));
    }

    @Override
    public void update(GameSystem gs, GameNode node) throws Exception {
        ParticleSystem ps = (ParticleSystem)node.data.get("fire");
        float startColor = 0.5f + random.nextFloat() * 0.5f;
        float startSize = 10 + random.nextFloat() * 10;
        
        particle.velocityY = 10 + random.nextFloat() * 20;
        particle.positionX = -5 + random.nextFloat() * 10;
        particle.positionY = -5 + random.nextFloat() * 10;
        particle.positionZ = -5 + random.nextFloat() * 10;
        particle.startR = startColor;
        particle.startG = startColor;
        particle.startB = startColor;
        particle.startA = 1;
        particle.endR = 0;
        particle.endG = 0;
        particle.endB = 0;
        particle.endA = 1;
        particle.startX = startSize;
        particle.startY = startSize;
        particle.endX = 1;
        particle.endY = 0;
        particle.lifeSpan = 0.5f + random.nextFloat() * 1.5f;

        ps.emit(particle, gs);

        Mesh mesh = (Mesh)node.data.get("mesh");

        ps.update(gs, mesh.model);
    }

    @Override
    public void render(GameSystem gs, GameNode node, BasicRenderer basicRenderer, RenderPass renderPass) throws Exception {
        super.render(gs, node, basicRenderer, renderPass);

        if(renderPass != RenderPass.TRANSPARENT) {
            return;
        }

        Mesh mesh = (Mesh)node.data.get("mesh");

        mesh.calcBoundsAndTransform(node);

        GFX.setBlendState(BlendState.ADDITIVE);

        ParticleSystem ps = (ParticleSystem)node.data.get("fire");

        basicRenderer.setLightingEnabled(false);
        basicRenderer.setTexture(ps.texture);
        basicRenderer.setModel(mesh.model);
        basicRenderer.setModelIT(mesh.modelIT);
        basicRenderer.draw(ps.vertexBuffer);
    }

    @Override
    public String toString() {
        return "torch_" + radius;
    }
}
