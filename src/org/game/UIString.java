package org.game;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.nuklear.NkPluginFilter;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

public class UIString extends Resource {
    
    private ByteBuffer editBuffer;
    private IntBuffer editLength = memAllocInt(1);
    private int maxChars;
    private NkPluginFilter filter = NkPluginFilter.create(Nuklear::nnk_filter_default);

    public UIString(int maxChars) {
        editBuffer = memAlloc(maxChars);

        this.maxChars = maxChars;
    }

    int getMaxChars() {
        return maxChars;
    }

    ByteBuffer getEditBuffer() {
        return editBuffer;
    }

    IntBuffer getEditLength() {
        return editLength;
    }

    NkPluginFilter getFilter() {
        return filter;
    }

    public String getText() {
        return memUTF8(editBuffer, editLength.get(0));
    }

    public void setText(String text) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer bytes = stack.UTF8(text, false);

            int len = Math.min(bytes.remaining(), maxChars - 1);

            memCopy(memAddress(bytes), memAddress(editBuffer), len);
            editLength.put(0, len);
        }
    }

    @Override
    public void destroy() {
        filter.free();
        memFree(editLength);
        memFree(editBuffer);
        super.destroy();
    }
}
