package org.game;

public class OutlineRenderer extends Resource {
    
    private Shader shader;
    private RenderTarget renderTarget;
    private VertexArrayBuffer vertexBuffer;

    public OutlineRenderer() throws Exception {
        shader = new Shader(
            "#version 150 core\n" +
            "in vec2 iPosition;\n" +
            "in vec2 iTextureCoordinate;\n" +
            "out vec2 vTextureCoordinate;\n" +
            "void main() {\n" +
            "\tgl_Position = vec4(iPosition, 0.0, 1.0);\n" +
            "\tvTextureCoordinate = iTextureCoordinate;\n" +
            "}\n"
            ,
            "#version 150 core\n" +
            "in vec2 vTextureCoordinate;\n" +
            "out vec4 oColor;\n" +
            "uniform vec2 uPixelSize;\n" +
            "uniform sampler2D uTexture;\n" +
            "uniform int uWidth;\n" +
            "void main() {\n" +
            "\tvec4 color = texture(uTexture, vTextureCoordinate);\n" +
            "\tif(color.a > 0.5) {\n" +
            "\t\toColor = vec4(0.0, 0.0, 0.0, 0.0);\n" +
            "\t} else {\n" +
            "\t\tvec4 c = vec4(0.0, 0.0, 0.0, 0.0);\n" +
            "\t\tfor(int x = -uWidth; x != uWidth; x++) {\n" +
            "\t\t\tfor(int y = -uWidth; y != uWidth; y++) {\n" +
            "\t\t\t\tvec4 pC = texture(uTexture, vTextureCoordinate + uPixelSize * vec2(float(x) + 0.5, float(y) + 0.5));\n" +
            "\t\t\t\tif(pC.a > 0.5) {\n" +
            "\t\t\t\t\tc = pC;\n" +
            "\t\t\t\t}\n" +
            "\t\t\t}\n" +
            "\t\t}\n" +
            "\t\toColor = c;\n" +
            "\t}\n" +
            "}\n"
            ,
            new VertexAttribute[] {
                    new VertexAttribute("iPosition", 2),
                    new VertexAttribute("iTextureCoordinate", 2)
            }
        );

        renderTarget = null;

        vertexBuffer = new VertexArrayBuffer(0);

        vertexBuffer.begin(6 * 4);
        vertexBuffer.push(-1, 1);
        vertexBuffer.push(0, 1);
        vertexBuffer.push(-1, -1);
        vertexBuffer.push(0, 0);
        vertexBuffer.push(1, -1);
        vertexBuffer.push(1, 0);
        vertexBuffer.push(-1, 1);
        vertexBuffer.push(0, 1);
        vertexBuffer.push(1, -1);
        vertexBuffer.push(1, 0);
        vertexBuffer.push(1, 1);
        vertexBuffer.push(1, 1);
        vertexBuffer.end(BufferUsage.STATIC);
    }

    public RenderTarget getRenderTarget() {
        return renderTarget;
    }

    public void render(Texture texture, int lineWidth, int w, int h) throws Exception {
        boolean create = renderTarget == null;

        if(!create) {
            create = (renderTarget.getWidth() != w || renderTarget.getHeight() != h) && w > 10 && h > 10;
        }
        if(create) {
            if(renderTarget != null) {
                renderTarget.destroy();
            }
            renderTarget = new RenderTarget(w, h, false, ColorFormat.COLOR);
        }

        renderTarget.begin();
        GFX.clear(0, 0, 0, 0);
        GFX.setBlendState(BlendState.OPAQUE);
        GFX.setDepthState(DepthState.NONE);
        GFX.setCullState(CullState.NONE);
        shader.begin();
        shader.set("uTexture", texture, 0);
        shader.set("uPixelSize", 1.0f / w, 1.0f / h);
        shader.set("uWidth", lineWidth);
        shader.draw(GeometryType.TRIANGLES, vertexBuffer);
        shader.end();
        renderTarget.end();
    }

    @Override
    public void destroy() {
        shader.destroy();
        vertexBuffer.destroy();
        if(renderTarget != null) {
            renderTarget.destroy();
        }
        super.destroy();
    }
}
