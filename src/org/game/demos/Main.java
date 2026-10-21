package org.game.demos;

import java.io.File;

import org.game.BlendState;
import org.game.CullState;
import org.game.DepthState;
import org.game.GFX;
import org.game.Game;
import org.game.Keys;
import org.game.SpriteRenderer;
import org.game.Texture;
import org.game.Utils;
import org.game.Vec4;

public class Main {
    public static void main(String[] args) throws Exception {
        Utils.startOnFirstThread(args, "JGame.jar");

        Game game = null;
        boolean fDown = false;
        boolean sDown = false;
        boolean dDown = false;
        boolean spaceDown = false;

        try {
            game = new Game(900, 500, "Game", true);

            SpriteRenderer spriteRenderer = game.getResources().manage(new SpriteRenderer());
            Texture texture = game.getResources().manage(Texture.load(new File("assets", "sprites.png")));
            int scale = game.getScale();
            Vec4 color1 = new Vec4();
            Vec4 color2 = new Vec4();

            Demo[] demos = new Demo[] {
                new Shader1(),
                new Shader2(),
                new Model1(),
                new Model2(),
                new UI1(),
                new UI2()
            };
            int iDemo = 0;
            int selDemo = -1;

            game.resetTimer();

            while(game.beginFrame()) {
                if(selDemo == -1) {
                    GFX.clear(0, 0, 0, 1);
                    GFX.setDepthState(DepthState.NONE);
                    GFX.setBlendState(BlendState.ALPHA);
                    GFX.setCullState(CullState.NONE);
                    spriteRenderer.setProjection(game.getFrameWidth(), game.getFrameHeight());
                    spriteRenderer.begin(texture);
                    for(int i = 0; i != demos.length; i++) {
                        color1.set(0.75f, 0.75f, 0.75f, 1);
                        color2.set(0.25f, 0.25f, 0.25f, 1);
                        if(i == iDemo) {
                            color1.set(1, 1, 1, 1);
                            color2.set(0, 0, 1, 1);
                        }
                        spriteRenderer.push(demos[i].toString(), 8, 16, 16, scale, 5, 10 * scale, 10 * scale + (16 + 5) * scale * i, color1, color2);
                    }
                    spriteRenderer.end();
                    game.endFrame();
                } else {
                    demos[selDemo].update(game);
                    game.endFrame(demos[selDemo].getUI());
                }
                game.setTitle("Game - FPS = " + game.getFPS());

                if(game.isKeyDown(Keys.KEY_F)) {
                    if(!fDown) {
                        fDown = true;
                        game.toggleFullscreen();
                    }
                } else {
                    fDown = false;
                }
                if(game.isKeyDown(Keys.KEY_S)) {
                    if(!sDown) {
                        sDown = true;
                        game.toggleSwapInterval();
                    }
                } else {
                    sDown = false;
                }
                if(selDemo == -1) {
                    if(game.isKeyDown(Keys.KEY_DOWN)) {
                        if(!dDown) {
                            dDown = true;
                            iDemo = (iDemo + 1) % demos.length;
                        }
                    } else {
                        dDown = false;
                    }
                    if(game.isKeyDown(Keys.KEY_SPACE)) {
                        if(!spaceDown) {
                            spaceDown = true;
                            selDemo = iDemo;
                            game.getResources().clear();
                            demos[selDemo].init(game);
                            game.resetTimer();
                        }
                    } else {
                        spaceDown = false;
                    }
                } else if(game.isKeyDown(Keys.KEY_ESCAPE)) {
                    game.setMouseGrabbed(false);
                    selDemo = -1;
                    game.getResources().clear();
                    spriteRenderer = game.getResources().manage(new SpriteRenderer());
                    texture = game.getResources().manage(Texture.load(new File("assets", "sprites.png")));
                }
            }
        } finally {
            if(game != null) {
                game.destroy();
            }
        }
    }
}