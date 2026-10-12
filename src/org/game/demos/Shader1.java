package org.game.demos;

import java.io.File;

import org.game.BlendState;
import org.game.BufferUsage;
import org.game.CullState;
import org.game.DepthState;
import org.game.GFX;
import org.game.Game;
import org.game.GeometryType;
import org.game.Mat4;
import org.game.Resource;
import org.game.Shader;
import org.game.SpriteRenderer;
import org.game.Texture;
import org.game.VertexArrayBuffer;
import org.game.VertexAttribute;
import org.game.VertexElementBuffer;

public class Shader1 extends Demo {

    private VertexArrayBuffer vertexBuffer;
    private VertexElementBuffer vertexElementBuffer;
    private VertexAttribute[] attributes;
    private Shader shader;
    private Texture texture;
    private Mat4 projection = new Mat4();
    private Mat4 view = new Mat4(); 
    private Mat4 model = new Mat4();
    private SpriteRenderer spriteRenderer;

    @Override
    public void init(Game game) throws Exception {

        vertexBuffer = game.getResources().manage(new VertexArrayBuffer(0));
        vertexElementBuffer = game.getResources().manage(new VertexElementBuffer(0));
        attributes =                     
            new VertexAttribute[] {
                new VertexAttribute("iPosition", 3),
                new VertexAttribute("iColor", 4)
            };

        vertexBuffer.begin(4 * GFX.stride(attributes));
        vertexBuffer.push(-100, 0, -100);
        vertexBuffer.push(1, 1, 1, 1);
        vertexBuffer.push(-100, 0, 100);
        vertexBuffer.push(0.5f, 0.5f, 0.5f, 1);
        vertexBuffer.push(100, 0, 100);
        vertexBuffer.push(0.5f, 0.5f, 0.5f, 1);
        vertexBuffer.push(100, 0, -100);
        vertexBuffer.push(1, 1, 1, 1);
        vertexBuffer.end(BufferUsage.STATIC);

        vertexElementBuffer.begin(6);
        vertexElementBuffer.push(0, 1, 2, 3);
        vertexElementBuffer.end(BufferUsage.STATIC);

        view.identity().lookAt(200, 200, 200, 0, 0, 0, 0, 1, 0);
        projection.identity();
        model.identity();

        shader = game.getResources().manage(
            new Shader(
                "#version 150 core\n" +
                "in vec3 iPosition;\n" +
                "in vec4 iColor;\n" +
                "out vec4 vColor;\n" +
                "uniform mat4 uProjection;\n" +
                "uniform mat4 uView;\n" +
                "uniform mat4 uModel;\n" +
                "void main() {\n" +
                "\tgl_Position = uProjection * uView * uModel * vec4(iPosition, 1.0);\n" +
                "\tvColor = iColor;\n" +
                "}\n",
                "#version 150 core\n" +
                "in vec4 vColor;\n" +
                "out vec4 oColor;\n" +
                "void main() {\n" +
                "\toColor = vColor;\n" +
                "}\n", attributes
            )
        );

        spriteRenderer = game.getResources().manage(new SpriteRenderer());
        texture = game.getResources().manage(Texture.load(new File("assets", "sprites.png")));
    }

    @Override
    public void update(Game game) throws Exception {
        int scale = game.getScale();

        GFX.clear(0, 0, 0, 1);
        projection.identity().perspective((float)Math.toRadians(60), game.getFrameWidth() / (float)game.getFrameHeight(), 1, 25000);
        model.rotate((float)Math.toRadians(45 * game.getElapsedTime()), 0,1, 0);
        shader.begin();
        shader.set("uProjection", projection);
        shader.set("uView", view);
        shader.set("uModel", model);
        shader.draw(GeometryType.TRIANGLES, vertexBuffer, vertexElementBuffer);
        shader.end();
        GFX.setDepthState(DepthState.NONE);
        GFX.setBlendState(BlendState.ALPHA);
        GFX.setCullState(CullState.NONE);
        spriteRenderer.setProjection(game.getFrameWidth(), game.getFrameHeight());
        spriteRenderer.begin(texture);
        spriteRenderer.push(
            "FPS = " + game.getFPS() + "\n" + "RES = " + Resource.getInstances(), 
            8, 16, 16, scale, 5, 10 * scale, 10 * scale, 1, 1, 0, 1, 1, 0.5f, 0, 1
        );
        spriteRenderer.end();
    }
}
