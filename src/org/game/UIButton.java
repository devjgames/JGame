package org.game;

class UIButton extends UIControl {

    private final String text;
    private boolean selected = false;
    private boolean down = false;
    private Boolean clicked = null;

    public  UIButton(String text) {
        this.text = text;
    }

    public boolean clicked() {
        boolean c = false;

        if(clicked != null) {
            c = clicked;
            clicked = null;
        }
        return c;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int w(UI ui) {
        return text.length() * ui.charW + 16;
    }

    @Override
    public int h(UI ui) {
        return ui.charH + 16;
    }

    @Override
    public void draw(UI ui) {
        ui.drawRect(
            x(), y(), text.length() * ui.charW + 16, ui.charH + 16, 
            UIColors.BUTTON2, UIColors.BUTTON1, 
            (selected) ? UIColors.BUTTON_SEL_TEXT : UIColors.BORDER
        );
        ui.drawString(
            text, x() + 8, y() + 8, text.length(), 
            (selected) ? UIColors.BUTTON_SEL_TEXT : UIColors.BUTTON_TEXT
        );
    }

    @Override
    public void mouseDown(UI ui, int x, int y) {
        if(hitTest(ui, x, y)) {
            down = true;
        }
    }

    @Override
    public void mouseUp(UI ui, int x, int y) {
        if(down) {
            if(hitTest(ui, x, y)) {
                clicked = true;
            }
            down = false;
        }
    }
}
