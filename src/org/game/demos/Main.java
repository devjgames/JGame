package org.game.demos;

import org.game.Game;
import org.game.GameSystem;
import org.game.ThreadHelper;

public class Main {
    public static void main(String[] args) throws Exception {
       ThreadHelper.startOnFirstThread(args, "JGame.jar");

        Game game = null;

        try {
            game = new Game(900, 600, "JGame", true);

            GameSystem gs = new GameSystem2(
                new Torch(0, 2, 4, 1),
                new Model("map1.obj"),
                new Model("door.obj"),
                new Axis(),
                new Player()
            );

            game.resetTimer();

            while(game.beginFrame()) {
                gs.update();
                game.endFrame();
            }
        } finally {
            if(game != null) {
                game.destroy();
            }
        }
    }
}