package org.game;

public class Log {
    
    private static Log instance = null;

    static {
        new Log();
    }

    public static Log getInstance() {
        return instance;
    }

    private int level = 1;

    public Log() {
        instance = this;
    }

    public final int getLevel() {
        return level;
    }

    public final void setLevel(int level) {
        this.level = level;
    }

    public void put(int level, String message) {
        if(level <= this.level) {
            System.out.println(message);
        }
    }
}
