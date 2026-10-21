package org.game.demos;

import org.game.BasicRenderer;
import org.game.BlendState;
import org.game.BufferUsage;
import org.game.ColorFormat;
import org.game.CullState;
import org.game.DepthState;
import org.game.GFX;
import org.game.Game;
import org.game.Mat4;
import org.game.Model;
import org.game.ModelMesh;
import org.game.ModelMeshPart;
import org.game.RenderTarget;
import org.game.Resource;
import org.game.SpriteRenderer;
import org.game.Texture;
import org.game.UI;
import org.game.UIString;

import java.io.File;
import java.util.Vector;

public class UI2 extends Demo {

    private UI ui;
    private SpriteRenderer spriteRenderer;
    private BasicRenderer basicRenderer;
    private Texture texture;
    private Model model;
    private Mat4 projection = new Mat4();
    private Mat4 view = new Mat4();
    private Vector<Object> items = new Vector<>();
    private String[] menu = new String[] {
        "Add",
        "Delete",
        "Clear"
    };
    private boolean[] menuOpen = new boolean[1];
    private int[] selected = new int[1];
    private float[] menuX = new float[1];
    private float[] menuY = new float[1];
    private UIString text;
    private boolean showAddItem;
    private RenderTarget renderTarget;
    private boolean rotate;
    private boolean x2;

    @Override
    public void init(Game game) throws Exception {
        ui = Game.getInstance().getResources().manage(new UI());

        spriteRenderer = game.getResources().manage(new SpriteRenderer());
        basicRenderer = Game.getInstance().getResources().manage(new BasicRenderer());

        model = game.getResources().manage(new Model());

        ModelMesh mesh = new ModelMesh();
        ModelMeshPart part = new ModelMeshPart(0, 0);

        mesh.addPart(part);
        model.addMesh(mesh);

        part.vertexBuffer.begin(4 * BasicRenderer.VERTEX_STRIDE);
        part.vertexBuffer.push(-100, 0, -100);
        part.vertexBuffer.push(0, 0);
        part.vertexBuffer.push(0, 1, 0);
        part.vertexBuffer.push(1, 1, 1, 1);

        part.vertexBuffer.push(-100, 0, 100);
        part.vertexBuffer.push(0, 0);
        part.vertexBuffer.push(0, 1, 0);
        part.vertexBuffer.push(1, 1, 1, 1);

        part.vertexBuffer.push(100, 0, 100);
        part.vertexBuffer.push(0, 0);
        part.vertexBuffer.push(0, 1, 0);
        part.vertexBuffer.push(1, 1, 1, 1);

        part.vertexBuffer.push(100, 0, -100);
        part.vertexBuffer.push(0, 0);
        part.vertexBuffer.push(0, 1, 0);
        part.vertexBuffer.push(1, 1, 1, 1);
        part.vertexBuffer.end(BufferUsage.STATIC);

        part.vertexElementBuffer.begin(6);
        part.vertexElementBuffer.push(0, 1, 2, 3);
        part.vertexElementBuffer.end(BufferUsage.STATIC);

        model.calcBounds(BasicRenderer.getVertexAttributes());

        texture = game.getResources().manage(Texture.load(new File("assets", "sprites.png")));

        view.identity().lookAt(200, 200, 200, 0, 0, 0, 0, 1, 0);

        selected[0] = -1;

        items.clear();

        menuOpen[0] = false;

        text = game.getResources().manage(new UIString(100));
        showAddItem = false;

        renderTarget = null;

        x2 = false;
        rotate = true;
    }

    @Override
    public void update(Game game) throws Exception {
        if(rotate) {
            model.getMesh(0).model.rotate((float)Math.toRadians(45 * game.getElapsedTime()), 0, 1, 0);
            model.getMesh(0).modelIT.set(model.getMesh(0).model).invert().transpose();
        }

        int scale = game.getScale();
        int fw = game.getFrameWidth() - 250 * scale;
        int fh = game.getFrameHeight() - 20 * scale;
        int tw = fw;
        int th = fh;

        if(!x2) {
            tw /= 2;
            th /= 2;
        }

        projection.identity().perspective((float)Math.toRadians(60), tw / (float)th, 1, 25000);

        basicRenderer.begin(tw, th, 0.2f, 0.2f, 0.2f, 1);
        basicRenderer.setProjection(projection);
        basicRenderer.setView(view);
        basicRenderer.setModel(model.getMesh(0).model);
        basicRenderer.setModelIT(model.getMesh(0).modelIT);
        basicRenderer.setLightCount(1);
        basicRenderer.setLightPosition(0, 0, 100, 0);
        basicRenderer.setLightRaius(0, 300);
        basicRenderer.setLightColor(0, 1, 1, 1, 1);
        basicRenderer.setAmbientColor(0.35f, 0.35f, 0.35f, 1);
        basicRenderer.setDiffuseColor(1, 1, 1, 1);
        basicRenderer.draw(model.getMesh(0).getPart(0).vertexBuffer, model.getMesh(0).getPart(0).vertexElementBuffer);
        basicRenderer.end();

        boolean create = renderTarget == null;

        if(!create) {
            int w = renderTarget.getWidth();
            int h = renderTarget.getHeight();

            if(tw > 10 && th > 10) {
                create = w != tw || h != th;
            }
        }
        if(create) {
            if(renderTarget != null) {
                game.getResources().unManage(renderTarget);
            }
            renderTarget = game.getResources().manage(new RenderTarget(tw, th, false, ColorFormat.COLOR));
        }

        renderTarget.begin();
        GFX.clear(0.2f, 0.2f, 0.2f, 1);
        GFX.setDepthState(DepthState.NONE);
        GFX.setCullState(CullState.NONE);
        GFX.setBlendState(BlendState.ALPHA);
        spriteRenderer.setProjection(tw, th);
        spriteRenderer.begin(basicRenderer.getRenderTarget().getTexture(0)); 
        spriteRenderer.push(
            0, 0, tw, th, 
            0, 0, tw, th, 
            1, 1, 1, 1, 1, 1, 1, 1, true
        );
        spriteRenderer.end();
        renderTarget.end();

        GFX.clear(0.2f, 0.2f, 0.2f, 1);
        GFX.setDepthState(DepthState.NONE);
        GFX.setCullState(CullState.NONE);
        GFX.setBlendState(BlendState.ALPHA);      
        spriteRenderer.setProjection(game.getFrameWidth(), game.getFrameHeight());
        spriteRenderer.begin(renderTarget.getTexture(0));
        spriteRenderer.push(
            0, 0, tw, th,
            0, 30 * scale, tw * ((x2) ? 1 : 2), th * ((x2) ? 1 : 2), 1, 1, 1, 1, 1, 1, 1, 1, true
        );
        spriteRenderer.end();
        spriteRenderer.begin(texture);
        spriteRenderer.push(
            "FPS = " + game.getFPS() + "\nRES = " + Resource.getInstances(), 
            8, 16, 16, scale, 5, 10 * scale, 10 * scale + 30 * scale, 1, 1, 0, 1, 1, 0.5f, 0, 1
        );
        spriteRenderer.end();
    
        try (AutoCloseable stack = ui.pushStack()) {
            if(ui.beginWindow("top", 0, 0, game.getWindowWidth(), 30, true, stack)) {
                ui.beginRow(20, 2);
                ui.rowPush(ui.checkboxWidth("Rotate"));
                rotate = ui.checkbox("Rotate", rotate);
                ui.rowPush(ui.checkboxWidth("X2"));
                x2 = ui.checkbox("X2", x2);
                ui.endRow();
            }
            ui.endWindow();
            if(ui.beginWindow("test", game.getWindowWidth() - 250, 30, 250, game.getWindowHeight() - 30, true, stack)) {
                String item = ui.list("test1", "test2", items, menu, game.getWindowHeight(), menuOpen, 150, 100, selected, menuX, menuY, stack);

                if(showAddItem) {
                    if(ui.beginPopup("popup", 10, 10, 200, 100, stack)) {
                        ui.beginRow(20, 2);
                        ui.rowPush(ui.buttonWidth("Add"));
                        if(ui.button("Add")) {
                            String s = text.getText();

                            if(!s.isBlank()) {
                                items.add(s);
                            }
                            selected[0] = -1;
                            showAddItem = false;
                        }
                        ui.rowPush(ui.buttonWidth("Cancel"));
                        if(ui.button("Cancel")) {
                            showAddItem = false;
                        }
                        ui.endRow();
                        ui.beginRow(20, 1);
                        ui.rowPush(150);
                        ui.edit(text);
                        ui.endRow();
                        ui.endPopup();
                    }
                    menuOpen[0] = false;
                }

                if(item != null) {
                    if(item.equals("Add")) {
                        showAddItem = true;
                    } else if(item.equals("Delete")) {
                        if(selected[0] >= 0 && selected[0] < items.size()) {
                            items.remove(selected[0]);
                            selected[0] = -1;
                        }
                    } else if(item.equals("Clear")) {
                        selected[0] = -1;
                    }
                }
            }
            ui.endWindow();
        }
        ui.render();
    }

    @Override
    public UI getUI() {
        return ui;
    }
}
