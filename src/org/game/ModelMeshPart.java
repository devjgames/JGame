package org.game;

public class ModelMeshPart extends TaggedResource {

    public final VertexArrayBuffer vertexBuffer;
    public final VertexElementBuffer vertexElementBuffer;
    public final AABB bounds = new AABB();

    public ModelMeshPart(int vertexBufferCountAdder, int vertexElementBufferCountAdder) {
        vertexBuffer = new VertexArrayBuffer(vertexBufferCountAdder);
        vertexElementBuffer = new VertexElementBuffer(vertexElementBufferCountAdder);
    }

    public void calcBounds(VertexAttribute ... attributes) throws Exception {
        vertexBuffer.calcBounds(GeometryType.TRIANGLES, bounds, attributes);
    }

    @Override
    public void destroy() {
        vertexBuffer.destroy();
        vertexElementBuffer.destroy();
        super.destroy();
    }
}
