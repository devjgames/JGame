package org.game.demos;

import org.game.BasicRenderer;
import org.game.GameComponent;
import org.game.GameSystem;

public class GameSystem2 extends GameSystem {

    public GameSystem2(GameComponent ... factories) throws Exception {
        super("scene1.scene", true, true, factories);
    }

    @Override
    protected BasicRenderer createRenderer() throws Exception {
        return new BasicRenderer2();
    }
}