package org.game.demos;

import org.game.Game;
import org.game.UI;

public abstract class Demo {
    
    public abstract void init(Game game) throws Exception;

    public abstract void update(Game game) throws Exception;

    public UI getUI() {
        return null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
