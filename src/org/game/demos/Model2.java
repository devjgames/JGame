package org.game.demos;

import java.io.File;

import org.game.BasicRenderer;
import org.game.BlendState;
import org.game.CullState;
import org.game.DepthState;
import org.game.GFX;
import org.game.Game;
import org.game.Intersection;
import org.game.Mat4;
import org.game.Model;
import org.game.ModelLoader;
import org.game.ModelMesh;
import org.game.ModelMeshPart;
import org.game.Resource;
import org.game.SpriteRenderer;
import org.game.Texture;
import org.game.Vec3;

public class Model2 extends Demo {

    private static final float GROUND_SLOPE = 60;
    private static final float SPEED = 150;
    private static final float RADIUS = 32;
    
    private Texture texture;
    private BasicRenderer basicRenderer;
    private Model model;
    private Mat4 projection = new Mat4();
    private Mat4 view = new Mat4(); 
    private SpriteRenderer spriteRenderer;
    private Vec3 eye = new Vec3();
    private Vec3 target = new Vec3();
    private Vec3 up = new Vec3();
    private Mat4 rotMatrix = new Mat4();
    private Vec3 r = new Vec3();
    private Vec3 u = new Vec3();
    private Vec3 f = new Vec3();
    private Vec3 forward = new Vec3();
    private Vec3 d = new Vec3();
    private Mat4 groundMatrix = new Mat4();
    private Vec3 groundNormal = new Vec3();
    private boolean onGround;
    private Vec3 velocity = new Vec3();
    private Vec3 delta = new Vec3();
    private Vec3 normal = new Vec3();
    private Vec3 triNormal = new Vec3();
    private Vec3 resolvedEye = new Vec3();
    private Vec3 closest = new Vec3();
    private Vec3 cp = new Vec3();
    private Vec3 ab = new Vec3();
    private Vec3 ap = new Vec3();
    private Vec3 p1 = new Vec3();
    private Vec3 p2 = new Vec3();
    private Vec3 p3 = new Vec3();
    private float time;
    private ModelMesh map;
    private boolean start;
    
    @Override
    public void init(Game game) throws Exception {
        basicRenderer = game.getResources().manage(new BasicRenderer());
        model = ModelLoader.load(new File("assets", "model2.obj"), game.getResources());
        spriteRenderer = game.getResources().manage(new SpriteRenderer());
        texture = game.getResources().manage(Texture.load(new File("assets", "sprites.png")));

        for(int i = 0; i != model.getMeshCount(); i++) {
            ModelMesh mesh = model.getMesh(i);

            if(mesh.get(ModelLoader.NAME).equals("Player")) {
                eye.set((Vec3)mesh.get(ModelLoader.POSITION));
                target.set(eye).add(1, 0, -1);
                up.set(0, 1, 0);
            } else if(mesh.get(ModelLoader.NAME).equals("map")) {
                map = mesh;
            }
        }
        game.setMouseGrabbed(true);

        groundMatrix.identity();
        groundNormal.zero();
        onGround = false;
        velocity.zero();

        start = true;
    }

    @Override
    public void update(Game game) throws Exception {
        if(start) {
            int w = game.getFrameWidth();
            int h = game.getFrameHeight();
            int scale = game.getScale();

            GFX.clear(0, 0, 0, 1);
            GFX.setDepthState(DepthState.NONE);
            GFX.setBlendState(BlendState.ALPHA);
            GFX.setCullState(CullState.NONE);
            spriteRenderer.setProjection(w, h);
            spriteRenderer.begin(texture);
            spriteRenderer.push(
                "click to start", 16, 10, 12, 
                scale, 0, w / 2 - 10 * 7 * scale, h / 2 - 6 * scale, 
                1, 1, 0, 1, 1, 0.5f, 0, 1);
            spriteRenderer.end();

            if(game.isMouseButtonDown(0)) {
                start = false;
            }
        } else {
            run(game);
        }
    }

    private void rotateAroundEye(Game game) {
        int dx = game.getDeltaX();
        int dy = game.getDeltaY();

        target.sub(eye, f);
        u.set(up);
        rotMatrix.identity().rotate(dx * 0.025f, 0, 1, 0);
        f.cross(u, r).mulDirection(rotMatrix).normalize();
        f.mulDirection(rotMatrix);
        rotMatrix.identity().rotate(dy * 0.025f, r);
        up.set(r.cross(f, u).mulDirection(rotMatrix).normalize());
        f.mulDirection(rotMatrix).normalize();
        target.set(eye).add(f);
    }

    private void run(Game game) throws Exception {
        int scale = game.getScale();
        int n = 0;
    
        rotateAroundEye(game);

        target.sub(eye, d);
        forward.set(d).mul(1, 0, 1);
        velocity.mul(0, 1, 0);
        if(game.isMouseButtonDown(0)) {
            velocity.add(forward.mul(SPEED));
        } else if(game.isMouseButtonDown(1)) {
            velocity.add(forward.mul(-SPEED));
        }
        velocity.y -= 2000 * game.getElapsedTime();
        collide(game);
        eye.add(d, target);

        projection.identity().perspective((float)Math.toRadians(60), game.getFrameWidth() / (float)game.getFrameHeight(), 1, 25000);
        view.identity().lookAt(eye, target, up);
        basicRenderer.begin(game.getFrameWidth(), game.getFrameHeight(), 0, 0, 0, 1);
        basicRenderer.setProjection(projection);
        basicRenderer.setView(view);
        for(int i = 0; i != model.getMeshCount(); i++) {
            ModelMesh mesh = model.getMesh(i);
            String name = mesh.get(ModelLoader.NAME);

            if(name.startsWith("Light")) {
                basicRenderer.setLightPosition(n, mesh.get(ModelLoader.POSITION));
                basicRenderer.setLightColor(n, 1, 2, 4, 1);
                basicRenderer.setLightRaius(n, (name.startsWith("LightSmall")) ? 200 : 300);
                n++;
            } 
        }
        basicRenderer.setLightCount(Math.min(n, BasicRenderer.MAX_LIGHTS));
        for(int i = 0; i != model.getMeshCount(); i++) {
            ModelMesh mesh = model.getMesh(i);
            String name = mesh.get(ModelLoader.NAME);

            if(name.equals("map")) {
                basicRenderer.setModel(mesh.model);
                basicRenderer.setModelIT(mesh.modelIT);
                basicRenderer.setAmbientColor(mesh.get(ModelLoader.AMBIENT_COLOR));
                basicRenderer.setDiffuseColor(mesh.get(ModelLoader.DIFFUSE_COLOR));
                for(int j = 0; j != mesh.getPartCount(); j++) {
                    ModelMeshPart part = mesh.getPart(j);

                    basicRenderer.setTexture(part.get(ModelLoader.TEXTURE));
                    basicRenderer.draw(part.vertexBuffer, part.vertexElementBuffer);
                }
            }
        }
        basicRenderer.end();

        GFX.clear(0, 0, 0, 1);
        GFX.setDepthState(DepthState.NONE);
        GFX.setBlendState(BlendState.ALPHA);
        GFX.setCullState(CullState.NONE);
        spriteRenderer.setProjection(game.getFrameWidth(), game.getFrameHeight());
        spriteRenderer.begin(basicRenderer.getRenderTarget().getTexture(0));
        spriteRenderer.push(
            0, 0, basicRenderer.getRenderTarget().getWidth(), basicRenderer.getRenderTarget().getHeight(), 
            0, 0, game.getFrameWidth(), game.getFrameHeight(), 1, 1, 1, 1, 1, 1, 1, 1, true);
        spriteRenderer.end();
        spriteRenderer.begin(texture);
        spriteRenderer.push(
            "FPS = " + game.getFPS() + "\n" + "RES = " + Resource.getInstances() + "\n" + 
            "POS = " + (int)eye.x + ", " + (int)eye.y + ", " + (int)eye.z, 
            16, 10, 12, scale, 5, 10 * scale, 10 * scale, 1, 1, 0, 1, 1, 0.5f, 0, 1
        );
        spriteRenderer.end();
    }

    private void collide(Game game) throws Exception {
        boolean hit;

        delta.set(velocity).mulDirection(groundMatrix).mul(game.getElapsedTime());
        if(delta.length() > RADIUS * 0.5f) {
            delta.normalize().mul(RADIUS * 0.5f);
        }
        eye.add(delta);
        onGround = false;
        groundNormal.zero();
        groundMatrix.identity();

        for(int i = 0; i != 3; i++) {
            normal.zero();
            resolvedEye.zero();
            hit = false;
            time = RADIUS;

            for(int j = 0; j != map.getPartCount(); j++) {
                ModelMeshPart part = map.getPart(j);

                for(int k = 0; k != part.vertexElementBuffer.getCount(); k += 3) {
                    int i1 = part.vertexElementBuffer.get(k + 0);
                    int i2 = part.vertexElementBuffer.get(k + 1);
                    int i3 = part.vertexElementBuffer.get(k + 2);
                    float x1 = part.vertexBuffer.get(i1 * BasicRenderer.VERTEX_STRIDE + 0);
                    float y1 = part.vertexBuffer.get(i1 * BasicRenderer.VERTEX_STRIDE + 1);
                    float z1 = part.vertexBuffer.get(i1 * BasicRenderer.VERTEX_STRIDE + 2);
                    float x2 = part.vertexBuffer.get(i2 * BasicRenderer.VERTEX_STRIDE + 0);
                    float y2 = part.vertexBuffer.get(i2 * BasicRenderer.VERTEX_STRIDE + 1);
                    float z2 = part.vertexBuffer.get(i2 * BasicRenderer.VERTEX_STRIDE + 2);
                    float x3 = part.vertexBuffer.get(i3 * BasicRenderer.VERTEX_STRIDE + 0);
                    float y3 = part.vertexBuffer.get(i3 * BasicRenderer.VERTEX_STRIDE + 1);
                    float z3 = part.vertexBuffer.get(i3 * BasicRenderer.VERTEX_STRIDE + 2);

                    p1.set(x1, y1, z1).mulPosition(map.model);
                    p2.set(x2, y2, z2).mulPosition(map.model);
                    p3.set(x3, y3, z3).mulPosition(map.model);

                    x1 = p1.x;
                    y1 = p1.y;
                    z1 = p1.z;
                    x2 = p2.x;
                    y2 = p2.y;
                    z2 = p2.z;
                    x3 = p3.x;
                    y3 = p3.y;
                    z3 = p3.z;

                    float ex1 = x2 - x1;
                    float ey1 = y2 - y1;
                    float ez1 = z2 - z1;
                    float ex2 = x3 - x2;
                    float ey2 = y3 - y2;
                    float ez2 = z3 - z2;
                    
                    triNormal.set(ex1, ey1, ez1).cross(ex2, ey2, ez2).normalize().negate();

                    float dx = triNormal.x;
                    float dy = triNormal.y;
                    float dz = triNormal.z;

                    triNormal.negate();

                    float dist = -triNormal.dot(x1, y1, z1);
                    float t = triNormal.dot(dx, dy, dz);
                    boolean isects = Math.abs(t) > 0.0000001;

                    if(isects) {
                        t = (-dist - eye.dot(triNormal)) / t;
                        isects = true;
                    } 

                    if(isects) {
                        float ix = eye.x + dx * t;
                        float iy = eye.y + dy * t;
                        float iz = eye.z + dz * t;

                        if(t > 0.0000001 && t < time) {
                            if(Intersection.testPointInTriangle(ix, iy, iz, x1, y1, z1, x2, y2, z2, x3, y3, z3)) {
                                time = t;
                                normal.set(triNormal);
                                resolvedEye.set(ix, iy, iz).add(normal.x * RADIUS, normal.y * RADIUS, normal.z * RADIUS);
                                hit = true;
                            } else {
                                closestPoint();
                                eye.sub(closest, delta);
                                t = delta.length();
                                if(t > 0.0000001 && t < time) {
                                    time = t;
                                    normal.set(delta).normalize();
                                    resolvedEye.set(closest).add(normal.x * RADIUS, normal.y * RADIUS, normal.z * RADIUS);
                                    hit = true;
                                }
                             }
                        }
                    }
                }
            }

            if(hit) {
                if(Math.acos(Math.max(-0.999, Math.min(0.999, normal.dot(0, 1, 0)))) < Math.toRadians(GROUND_SLOPE)) {
                    onGround = true;
                    groundNormal.add(normal);
                    velocity.y = 0;
                }
                eye.set(resolvedEye);
            } else {
                break;
            }
        }

        if(onGround) {
            groundNormal.normalize(u);
            r.set(1, 0, 0);
            r.cross(u, f).normalize();
            u.cross(f, r).normalize();
            groundMatrix.set(
                r.x, r.y, r.z, 1,
                u.x, u.y, u.z, 1,
                f.x, f.y, f.z, 1,
                0, 0, 0, 1
            );
        }
    }

    private Vec3 getPoint(int i) {
        if(i == 1) {
            return p2;
        } else if(i == 2) {
            return p3;
        }
        return p1;
    }

    private void closestPoint() {
        float min = Float.MAX_VALUE;

        closest.set(p1);
        for(int i = 0; i != 3; i++) {
            Vec3 a = getPoint(i);
            Vec3 b = getPoint(i + 1);

            b.sub(a, ab);
            eye.sub(a, ap);

            float s = ab.dot(ap);

            cp.set(a);

            if(s > 0) {
                s /= ab.lengthSquared();
                if(s < 1) {
                    cp.add(ab.mul(s));
                } else {
                    cp.set(b);
                }
            }
            eye.sub(cp, delta);
            
            float l = delta.length();

            if(l < min) {
                min = l;
                closest.set(cp);
            }
        }
    }
}
