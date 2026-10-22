package org.game.demos;

import java.io.File;
import java.util.Vector;

import org.game.BlendState;
import org.game.CullState;
import org.game.DepthState;
import org.game.GFX;
import org.game.Game;
import org.game.Resource;
import org.game.SpriteRenderer;
import org.game.Texture;
import org.game.UI;
import org.game.UIColors;

public class UI1 extends Demo {

    private final String[] MODES = new String[] {
        "ZM",
        "RT",
        "PXZ",
        "PY",
        "SEL",
        "MXZ",
        "MY",
        "RX",
        "RY",
        "RZ"
    };

    private UI ui;
    private SpriteRenderer spriteRenderer;
    private Texture texture;
    private boolean selected;
    private int mode;
    private Vector<Object> items = new Vector<>();
    private int selectedIndex;
    
    @Override
    public void init(Game game) throws Exception {
        spriteRenderer = game.getResources().manage(new SpriteRenderer());
        texture = game.getResources().manage(Texture.load(new File("assets", "sprites.png")));

        ui = new UI(spriteRenderer, texture, 16, 10, 12, 14, 5);
        selected = false;
        mode = 0;
        selectedIndex = -1;
        items.clear();
    }

    @Override
    public void update(Game game) throws Exception {
        String text = 
            "FPS = " + game.getFPS() + "\n" +
            "RES = " + Resource.getInstances() + "\n";
        int s = game.getScale();

        if(selectedIndex >= 0 && selectedIndex < items.size()) {
            text += "ITM = " + items.get(selectedIndex) + "\n";
        }
        if(!items.isEmpty()) {
            text += "LST = " + items.lastElement();
        }

        GFX.clear(UIColors.BACKGROUND);
        GFX.setDepthState(DepthState.NONE);
        GFX.setBlendState(BlendState.ALPHA);
        GFX.setCullState(CullState.NONE);
        spriteRenderer.setProjection(game.getFrameWidth(), game.getFrameHeight());
        spriteRenderer.begin(texture);
        spriteRenderer.push(text, 16, 10, 12, s, 5, 150 * s, 50 * s, 1, 1, 0, 1, 1, 0.5f, 0, 1);
        spriteRenderer.end();

        ui.begin();
        ui.moveTo(10, 10);
        for(int i = 0; i != MODES.length; i++) {
            if(ui.button(MODES[i], MODES[i], i == mode, (i == 0) ? 0 : 5)) {
                mode = i;
            }
        }
        if(ui.button("b1", "X2", selected, 5)) {
            selected = !selected;
        }
        ui.addRow(10);
        selectedIndex = ui.list("list", items, 8, 10, -2, 0);
        ui.addRow(10);
        if(ui.button("add", "ADD", false, 0)) {
            items.add("Item    " + (items.size() + 1));
        }
        if(ui.button("clr", "CLR", false, 5)) {
            items.clear();
        }
        if(selectedIndex != -1) {
            if(ui.button("del", "DEL", false, 5)) {
                items.remove(selectedIndex);
                selectedIndex = -1;
            }
        }
        ui.end(game);
    }
}
