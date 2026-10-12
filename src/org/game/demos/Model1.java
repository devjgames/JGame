package org.game.demos;

import java.io.File;

import org.game.BasicRenderer;
import org.game.BlendState;
import org.game.BufferUsage;
import org.game.ColorFormat;
import org.game.CullState;
import org.game.DepthState;
import org.game.GFX;
import org.game.Game;
import org.game.GeometryType;
import org.game.Mat4;
import org.game.Model;
import org.game.ModelLoader;
import org.game.ModelMesh;
import org.game.ModelMeshPart;
import org.game.RenderTarget;
import org.game.Resource;
import org.game.Shader;
import org.game.SpriteRenderer;
import org.game.Texture;
import org.game.Vec3;
import org.game.VertexArrayBuffer;
import org.game.VertexAttribute;

public class Model1 extends Demo {

    private Texture texture;
    private BasicRenderer basicRenderer;
    private Shader shader;
    private RenderTarget renderTarget;
    private VertexArrayBuffer vertexBuffer;
    private Model model;
    private Mat4 projection = new Mat4();
    private Mat4 view = new Mat4(); 
    private SpriteRenderer spriteRenderer;
    private float angle;
    
    @Override
    public void init(Game game) throws Exception {

        basicRenderer = game.getResources().manage(new BasicRenderer());
        model = ModelLoader.load(new File("assets", "model1.obj"), game.getResources());
        view.identity().lookAt(150, 150, 150, 0, 0, 0, 0, 1, 0);
        spriteRenderer = game.getResources().manage(new SpriteRenderer());
        texture = game.getResources().manage(Texture.load(new File("assets", "sprites.png")));

        shader = game.getResources().manage(
            new Shader(
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
                "void main() {\n" +
                "\tvec4 color = texture(uTexture, vTextureCoordinate);\n" +
                "\tif(color.a > 0.5) {\n" +
                "\t\toColor = vec4(0.0, 0.0, 0.0, 0.0);\n" +
                "\t} else {\n" +
                "\t\tvec4 c = vec4(0.0, 0.0, 0.0, 0.0);\n" +
                "\t\tfor(int x = -4; x != 4; x++) {\n" +
                "\t\t\tfor(int y = -4; y != 4; y++) {\n" +
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
            )
        );

        renderTarget = null;

        vertexBuffer = game.getResources().manage(new VertexArrayBuffer(0));

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

        angle = 0;
    }

    @Override
    public void update(Game game) throws Exception {
        int scale = game.getScale();

        projection.identity().perspective((float)Math.toRadians(60), game.getFrameWidth() / (float)game.getFrameHeight(), 1, 25000);
        basicRenderer.begin(game.getWindowWidth(), game.getWindowHeight(), 0, 0, 0, 1);
        basicRenderer.setLightCount(1);
        basicRenderer.setLightPosition(0, 100, 150, 100);
        basicRenderer.setLightColor(0, 2, 2, 2, 1);
        basicRenderer.setProjection(projection);
        basicRenderer.setView(view);
        for(int i = 0; i != model.getMeshCount(); i++) {
            ModelMesh mesh = model.getMesh(i);

            if(mesh.get(ModelLoader.NAME).equals("Ledge")) {
                Vec3 position = mesh.get(ModelLoader.POSITION);

                mesh.model.identity().translate(position).rotate((float)Math.toRadians(angle), 0, 1, 0);
                mesh.modelIT.set(mesh.model).invert().transpose();
                basicRenderer.setEmissiveColor(1, 0.5f, 0, 1);
            } else {
                basicRenderer.setEmissiveColor(0, 0, 0, 0);
            }
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
        basicRenderer.end();

        angle += 45 * game.getElapsedTime();

        Texture diffuseTexture = basicRenderer.getRenderTarget().getTexture(0);
        Texture emissiveTexture = basicRenderer.getRenderTarget().getTexture(1);
        int w = basicRenderer.getRenderTarget().getWidth();
        int h = basicRenderer.getRenderTarget().getHeight();
        boolean create = renderTarget == null;

        if(!create) {
            create = (renderTarget.getWidth() != w || renderTarget.getHeight() != h) && w > 10 && h > 10;
        }
        if(create) {
            if(renderTarget != null) {
                game.getResources().unManage(renderTarget);
            }
            renderTarget = game.getResources().manage(new RenderTarget(w, h, false, ColorFormat.COLOR));
        }

        renderTarget.begin();
        GFX.clear(0, 0, 0, 0);
        GFX.setBlendState(BlendState.OPAQUE);
        GFX.setDepthState(DepthState.NONE);
        GFX.setCullState(CullState.NONE);
        shader.begin();
        shader.set("uTexture", emissiveTexture, 0);
        shader.set("uPixelSize", 1.0f / w, 1.0f / h);
        shader.draw(GeometryType.TRIANGLES, vertexBuffer);
        shader.end();
        renderTarget.end();

        GFX.clear(0, 0, 0, 1);
        GFX.setDepthState(DepthState.NONE);
        GFX.setBlendState(BlendState.ALPHA);
        GFX.setCullState(CullState.NONE);
        spriteRenderer.setProjection(game.getFrameWidth(), game.getFrameHeight());
        spriteRenderer.begin(diffuseTexture);
        spriteRenderer.push(0, 0, w, h, 0, 0, game.getFrameWidth(), game.getFrameHeight(), 1, 1, 1, 1, 1, 1, 1, 1, true);
        spriteRenderer.end();
        spriteRenderer.begin(renderTarget.getTexture(0));
        spriteRenderer.push(0, 0, w, h, 0, 0, game.getFrameWidth(), game.getFrameHeight(), 1, 1, 1, 1, 1, 1, 1, 1, true);
        spriteRenderer.end();
        spriteRenderer.begin(texture);
        spriteRenderer.push(
            "FPS = " + game.getFPS() + "\n" + "RES = " + Resource.getInstances(), 
            8, 16, 16, scale, 5, 10 * scale, 10 * scale, 1, 1, 0, 1, 1, 0.5f, 0, 1
        );
        spriteRenderer.end();
    }
}
