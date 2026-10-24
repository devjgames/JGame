package org.game.demos;

import org.game.Game;
import org.game.GameComponent;
import org.game.GameSystem;
import org.game.ThreadHelper;

public class Main {
    public static void main(String[] args) throws Exception {
        ThreadHelper.startOnFirstThread(args, "JGame.jar");

        Game game = null;

        try {
            game = new Game(900, 500, "Game", true);

            GameSystem gs = new GameSystem2(
                new Torch(0, 2, 4, 1, 200),
                new Torch(0, 2, 4, 1, 50),
                new Model("map1.obj"),
                new Model("door.obj"),
                new Axis(),
                new Player()
            );

            game.resetTimer();

            while(game.beginFrame()) {
                gs.update();

                int iNode = gs.getSelectedNode();
                String name = "";

                if(iNode >= 0) {
                    GameComponent component = gs.getComponent(iNode);

                    name = ", " + component.toString();
                }

                game.endFrame();
                game.setTitle(gs.getSceneName() + name + ", ESC = QUIT");
            }
        } finally {
            if(game != null) {
                game.destroy();
            }
        }
    }
}