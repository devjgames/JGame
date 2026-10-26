package org.game;


public final class ParticleSystem extends Resource {

    public Texture texture = null;
    public final VertexArrayBuffer vertexBuffer;

    private float[] particles;
    private float[] temp;
    private int count;
    private final Mat4 m = new Mat4();
    private final Vec3 r = new Vec3();
    private final Vec3 u = new Vec3();
    private final Vec3 f = new Vec3();
    private float time = 0;
    private final AABB bounds = new AABB();
    private final AABB tBounds = new AABB();
    private final int max;

    public ParticleSystem(int maxParticles) throws Exception {
        max = maxParticles;

        vertexBuffer = new VertexArrayBuffer(0);

        count = 0;
        particles = new float[maxParticles * 20];
        temp = new float[maxParticles * 20];
    }

    public AABB getBounds() {
        return (AABB)tBounds.set(bounds);
    }

    public void update(GameSystem gs, Mat4 model) throws Exception {
        time = gs.game.getTotalTime();
        int count = 0;
        for(int i = 0; i != this.count; i += 20) {
            float n = (time - particles[i + 18]) / particles[i + 19];
            if(n <= 1) {
                for(int j = 0; j != 20; j++) {
                    temp[count++] = particles[i + j];
                }
            }
        }
        float[] t = particles;
        particles = temp;
        temp = t;
        this.count = count;

        m.set(gs.view).mul(model);
        m.getRow(0, r).normalize();
        m.getRow(1, u).normalize();
        m.getRow(2, f).normalize();

        bounds.clear();
        vertexBuffer.begin(BasicRenderer.VERTEX_STRIDE * max * 6);
        for(int i = 0; i != count;) {
            float vX = particles[i++];
            float vY = particles[i++];
            float vZ = particles[i++];
            float pX = particles[i++];
            float pY = particles[i++];
            float pZ = particles[i++];
            float sR = particles[i++];
            float sG = particles[i++];
            float sB = particles[i++];
            float sA = particles[i++];
            float eR = particles[i++];
            float eG = particles[i++];
            float eB = particles[i++];
            float eA = particles[i++];
            float sX = particles[i++];
            float sY = particles[i++];
            float eX = particles[i++];
            float eY = particles[i++];
            float s = time - particles[i++];
            float n = s / particles[i++];
            float cR = sR + n * (eR - sR);
            float cG = sG + n * (eG - sG);
            float cB = sB + n * (eB - sB);
            float cA = sA + n * (eA - sA);
            float x = (sX + n * (eX - sX)) * 0.5f;
            float y = (sY + n * (eY - sY)) * 0.4f;

            pX += s * vX;
            pY += s * vY;
            pZ += s * vZ;
            push(pX - r.x * x - u.x * y, pY - r.y * x - u.y * y, pZ - r.z * x - u.z * y, 0, 0, f.x, f.y, f.z, cR, cG, cB, cA);
            push(pX + r.x * x - u.x * y, pY + r.y * x - u.y * y, pZ + r.z * x - u.z * y, 1, 0, f.x, f.y, f.z, cR, cG, cB, cA);
            push(pX + r.x * x + u.x * y, pY + r.y * x + u.y * y, pZ + r.z * x + u.z * y, 1, 1, f.x, f.y, f.z, cR, cG, cB, cA);
            push(pX + r.x * x + u.x * y, pY + r.y * x + u.y * y, pZ + r.z * x + u.z * y, 1, 1, f.x, f.y, f.z, cR, cG, cB, cA);
            push(pX - r.x * x + u.x * y, pY - r.y * x + u.y * y, pZ - r.z * x + u.z * y, 0, 1, f.x, f.y, f.z, cR, cG, cB, cA);
            push(pX - r.x * x - u.x * y, pY - r.y * x - u.y * y, pZ - r.z * x - u.z * y, 0, 0, f.x, f.y, f.z, cR, cG, cB, cA);
            bounds.union(pX - r.x * x - u.x * y, pY - r.y * x - u.y * y, pZ - r.z * x - u.z * y);
            bounds.union(pX + r.x * x - u.x * y, pY + r.y * x - u.y * y, pZ + r.z * x - u.z * y);
            bounds.union(pX + r.x * x + u.x * y, pY + r.y * x + u.y * y, pZ + r.z * x + u.z * y);
            bounds.union(pX - r.x * x + u.x * y, pY - r.y * x + u.y * y, pZ - r.z * x + u.z * y);
        }
        vertexBuffer.end(BufferUsage.DYNAMIC);
    }

    private void push(float x, float y, float z, float s, float t, float nx, float ny, float nz, float r, float g, float b, float a) {
        vertexBuffer.push(x, y, z);
        vertexBuffer.push(s, t);
        vertexBuffer.push(nx, ny, nz);
        vertexBuffer.push(r, g, b, a);
    }
    
    public void emit(Particle particle, GameSystem gs) {
        if(count != particles.length) {
            particles[count++] = particle.velocityX;
            particles[count++] = particle.velocityY;
            particles[count++] = particle.velocityZ;
            particles[count++] = particle.positionX;
            particles[count++] = particle.positionY;
            particles[count++] = particle.positionZ;
            particles[count++] = particle.startR;
            particles[count++] = particle.startG;
            particles[count++] = particle.startB;
            particles[count++] = particle.startA;
            particles[count++] = particle.endR;
            particles[count++] = particle.endG;
            particles[count++] = particle.endB;
            particles[count++] = particle.endA;
            particles[count++] = particle.startX;
            particles[count++] = particle.startY;
            particles[count++] = particle.endX;
            particles[count++]  = particle.endY;
            particles[count++] = gs.game.getTotalTime();
            particles[count++] = particle.lifeSpan;
        }
    }

    @Override
    public void destroy() {
        vertexBuffer.destroy();

        super.destroy();
    }
}
