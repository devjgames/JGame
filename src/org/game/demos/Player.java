package org.game.demos;

import java.io.File;

import org.game.BasicRenderer;
import org.game.CameraHelper;
import org.game.Collider;
import org.game.GameComponent;
import org.game.GameNode;
import org.game.GameSystem;
import org.game.Mesh;
import org.game.Resource;
import org.game.SpriteRenderer;
import org.game.Texture;
import org.game.Vec3;

public class Player extends Model {

    private Collider collider = new Collider();
    private Vec3 f = new Vec3();
    private Vec3 d = new Vec3();
    
    public Player() {
        super("axis.obj");
    }

    @Override
    public void init(GameSystem gs, GameNode node) throws Exception {
        super.init(gs, node);

        gs.backgroundColor.set(0.1f, 0.1f, 0.1f, 1);

        if(gs.inDesign()) {
            return;
        }

        collider.selectors.clear();

        for(int i = 0; i != gs.getCount(); i++) {
            GameComponent component = gs.getComponent(i);

            if(component instanceof Model) {
                Model model = (Model)component;
                String name = model.getMeshName();

                if(name.equals("map1.obj")) {
                    collider.selectors.add((Mesh)((GameNode)gs.getNode(i)).data.get("mesh"));
                }
            }
        }

        gs.eye.set(node.position);
        gs.eye.add(node.r, gs.target);
        gs.up.set(node.u);

        gs.game.setMouseGrabbed(true);
    }

    @Override
    public void update(GameSystem gs, GameNode node) throws Exception {
        if(gs.inDesign()) {
            return;
        }

        CameraHelper.rotateAroundEye(gs.eye, gs.target, gs.up, gs.game.getDeltaX() * 0.025f, gs.game.getDeltaY() * 0.025f);

        gs.target.sub(gs.eye, d).normalize();
        f.set(d).mul(1, 0, 1);
        collider.velocity.mul(0, 1, 0);
        if(f.length() > 0.0000001) {
            f.normalize();
            if(gs.game.isMouseButtonDown(0)) {
                f.mul(75);
            } else if(gs.game.isMouseButtonDown(1)) {
                f.mul(-75);
            } else {
                f.zero();
            };
            collider.velocity.add(f);
        }
        collider.velocity.y -= 2000 * gs.game.getElapsedTime();
        collider.resolve(gs.eye);
        gs.eye.add(d, gs.target);
    }

    @Override
    public void render(GameSystem gs, GameNode node, SpriteRenderer spriteRenderer, BasicRenderer basicRenderer) throws Exception {
        Texture texture = gs.game.getResources().load(new File("sprites.png"));
        int s = gs.game.getScale();

        spriteRenderer.begin(texture);
        spriteRenderer.push(
            "FPS = " + gs.game.getFPS() + "\n" +
            "RES = " + Resource.getInstances() + "\n" +
            "TRI = " + basicRenderer.getTrianglesRendered() + "\n" +
            "TST = " + collider.getTested() + "\n" +
            "POS = " + (int)gs.eye.x + ", " + (int)gs.eye.y + ", " + (int)gs.eye.z + "\n",
            16, 10, 12, s, 5, 10 * s, 10 * s, 1, 1, 0, 1, 1, 0.5f, 0, 1
        );
        spriteRenderer.end();
    }

    @Override
    public String toString() {
        return "player";
    }
}
