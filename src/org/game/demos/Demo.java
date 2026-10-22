package org.game.demos;

import org.game.Game;

public abstract class Demo {
    
    public abstract void init(Game game) throws Exception;

    public abstract void update(Game game) throws Exception;

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
