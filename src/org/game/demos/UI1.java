package org.game.demos;

import java.util.Vector;

import org.game.GFX;
import org.game.Game;
import org.game.Resource;
import org.game.UI;

public class UI1 extends Demo {

    private static final String[] OPTIONS = new String[] {
        "One",
        "Two",
        "Three"
    };
    
    private UI ui;
    private int counter;
    private int option;
    private boolean checked;
    private Vector<Object> items = new Vector<>();
    private boolean[] menuOpen = new boolean[1];
    private String text;

    @Override
    public void init(Game game) throws Exception {
        ui = game.getResources().manage(new UI());
        counter = 0;
        option = 1;
        checked = false;

        items.clear();
        for(int i = 1; i != 16; i++) {
            items.add("Item " + i);
        }
        menuOpen[0] = false;
        text = "";
    }

    @Override
    public void update(Game game) throws Exception {
        String s = "";

        GFX.clear(0.2f, 0.2f, 0.2f, 1);

        try (AutoCloseable stack = ui.pushStack()) {
            if(ui.beginWindow("test", 10, 10, 400, 200, true, stack)) {
                ui.beginRow(20, 4);
                for(int i = 1; i != 4; i++) {
                    ui.rowPush(ui.optionWidth(OPTIONS[i - 1]));
                    if(ui.option(OPTIONS[i - 1], option == i)) {
                        option = i;
                    }
                }
                ui.rowPush(ui.checkboxWidth("Enabled"));
                checked = ui.checkbox("Enabled", checked);
                ui.endRow();
                ui.beginRow(20, 1);
                s = "FPS = " + game.getFPS() + ", RES = " + Resource.getInstances();
                ui.rowPush(ui.labelWidth(s));
                ui.label(s);
                ui.endRow();
                ui.beginRow(20, 2);
                ui.rowPush(ui.buttonWidth("Add"));
                if(ui.button("Add")) {
                    counter++;
                }
                s = "Count = " + counter;
                ui.rowPush(ui.labelWidth(s));
                ui.label(s);
                ui.endRow();
                ui.beginRow(20, 3);
                ui.rowPush(ui.buttonWidth("+"));
                Integer i;
                if((i = ui.popup("+", "items", items, 150, 200, menuOpen, false, stack)) != null) {
                    text = items.get(i).toString();
                }
                ui.rowPush(ui.labelWidth(text));
                ui.label(text);
                ui.endRow();
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
