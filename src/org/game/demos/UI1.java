package org.game.demos;

import java.io.File;

import org.game.Game;
import org.game.SpriteRenderer;
import org.game.Texture;
import org.game.UI;

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
    
    @Override
    public void init(Game game) throws Exception {
        spriteRenderer = game.getResources().manage(new SpriteRenderer());
        texture = game.getResources().manage(Texture.load(new File("assets", "sprites.png")));

        ui = new UI(spriteRenderer, texture, 16, 10, 12, 14, 5);
        selected = false;
        mode = 0;
    }

    @Override
    public void update(Game game) throws Exception {
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
        ui.end(game);
    }
}
