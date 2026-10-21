package org.game;

import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL11C.GL_RGBA8;
import static org.lwjgl.opengl.GL11C.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_TRUE;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glDeleteTextures;
import static org.lwjgl.opengl.GL11C.glDepthMask;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL11C.glScissor;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL11C.glTexParameteri;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL14C.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14C.glBlendEquation;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15C.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL15C.glMapBuffer;
import static org.lwjgl.opengl.GL15C.glUnmapBuffer;
import static org.lwjgl.opengl.GL20C.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20C.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20C.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20C.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20C.glAttachShader;
import static org.lwjgl.opengl.GL20C.glCompileShader;
import static org.lwjgl.opengl.GL20C.glCreateProgram;
import static org.lwjgl.opengl.GL20C.glCreateShader;
import static org.lwjgl.opengl.GL20C.glDeleteProgram;
import static org.lwjgl.opengl.GL20C.glDeleteShader;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glGetAttribLocation;
import static org.lwjgl.opengl.GL20C.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20C.glGetProgrami;
import static org.lwjgl.opengl.GL20C.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20C.glGetShaderi;
import static org.lwjgl.opengl.GL20C.glGetUniformLocation;
import static org.lwjgl.opengl.GL20C.glLinkProgram;
import static org.lwjgl.opengl.GL20C.glShaderSource;
import static org.lwjgl.opengl.GL20C.glUniform1i;
import static org.lwjgl.opengl.GL20C.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

public class UI extends Resource {
    
    private static final int MAX_VERTEX_BUFFER = 512 * 1024;
    private static final int MAX_ELEMENT_BUFFER = 128 * 1024;

    private static final String VERTEX_SHADER =
            "#version 150 core\n" +
            "uniform mat4 ProjMtx;\n" +
            "in vec2 Position;\n" +
            "in vec2 TexCoord;\n" +
            "in vec4 Color;\n" +
            "out vec2 Frag_UV;\n" +
            "out vec4 Frag_Color;\n" +
            "void main() {\n" +
            "    Frag_UV = TexCoord;\n" +
            "    Frag_Color = Color;\n" +
            "    gl_Position = ProjMtx * vec4(Position.xy, 0, 1);\n" +
            "}\n";

    private static final String FRAGMENT_SHADER =
            "#version 150 core\n" +
            "uniform sampler2D Texture;\n" +
            "in vec2 Frag_UV;\n" +
            "in vec4 Frag_Color;\n" +
            "out vec4 Out_Color;\n" +
            "void main() {\n" +
            "    Out_Color = Frag_Color * texture(Texture, Frag_UV.st);\n" +
            "}\n";

    private final NkAllocator ALLOCATOR = NkAllocator.create()
            .alloc((handle, old, size) -> nmemAllocChecked(size))
            .mfree((handle, ptr) -> nmemFree(ptr));

    private final NkDrawVertexLayoutElement.Buffer VERTEX_LAYOUT = NkDrawVertexLayoutElement.create(4)
            .position(0).attribute(NK_VERTEX_POSITION).format(NK_FORMAT_FLOAT).offset(0)
            .position(1).attribute(NK_VERTEX_TEXCOORD).format(NK_FORMAT_FLOAT).offset(8)
            .position(2).attribute(NK_VERTEX_COLOR).format(NK_FORMAT_R8G8B8A8).offset(16)
            .position(3).attribute(NK_VERTEX_ATTRIBUTE_COUNT).format(NK_FORMAT_COUNT).offset(0)
            .flip();

    private int program, vao, vbo, ebo, uniformTexture, uniformProjection, fontTexture;

    private NkContext ctx;
    private NkBuffer cmds;
    private NkFontAtlas atlas;
    private NkDrawNullTexture nullTexture;
    private NkFont font;

    public UI() {
        createGlObjects();

        ctx = NkContext.create();
        cmds = NkBuffer.create();
        atlas = NkFontAtlas.create();
        nullTexture = NkDrawNullTexture.create();

        setupFont(ctx, atlas, nullTexture);
        nk_buffer_init(cmds, ALLOCATOR, 4 * 1024);

        long window = Game.getInstance().getWindow();

        final double[] cursor = new double[2];
        glfwSetCursorPosCallback(window, (w, x, y) -> {
            cursor[0] = x;
            cursor[1] = y;
            nk_input_motion(ctx, (int) x, (int) y);
        });
        glfwSetMouseButtonCallback(window, (w, button, action, mods) -> {
            try (MemoryStack stack = stackPush()) {
                DoubleBuffer cx = stack.mallocDouble(1);
                DoubleBuffer cy = stack.mallocDouble(1);
                glfwGetCursorPos(window, cx, cy);

                int nkButton;
                switch (button) {
                    case GLFW_MOUSE_BUTTON_RIGHT:  nkButton = NK_BUTTON_RIGHT;  break;
                    case GLFW_MOUSE_BUTTON_MIDDLE: nkButton = NK_BUTTON_MIDDLE; break;
                    default:                       nkButton = NK_BUTTON_LEFT;
                }
                nk_input_button(ctx, nkButton, (int) cx.get(0), (int) cy.get(0),
                                action == GLFW_PRESS);
            }
        });
        glfwSetCharCallback(window, (w, codepoint) -> nk_input_unicode(ctx, codepoint));

        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            boolean press = action == GLFW_PRESS || action == GLFW_REPEAT;
            switch (key) {
                case GLFW_KEY_BACKSPACE: nk_input_key(ctx, NK_KEY_BACKSPACE, press); break;
                case GLFW_KEY_DELETE: nk_input_key(ctx, NK_KEY_DEL, press); break;
                case GLFW_KEY_ENTER: nk_input_key(ctx, NK_KEY_ENTER, press); break;
                case GLFW_KEY_TAB: nk_input_key(ctx, NK_KEY_TAB, press); break;
                case GLFW_KEY_LEFT: nk_input_key(ctx, NK_KEY_LEFT, press); break;
                case GLFW_KEY_RIGHT: nk_input_key(ctx, NK_KEY_RIGHT, press); break;
                case GLFW_KEY_HOME: nk_input_key(ctx, NK_KEY_TEXT_LINE_START, press); break;
                case GLFW_KEY_END: nk_input_key(ctx, NK_KEY_TEXT_LINE_END, press); break;
            }
        });
    }

    private void setupFont(NkContext ctx, NkFontAtlas atlas, NkDrawNullTexture nullTexture) {
        nk_font_atlas_init(atlas, ALLOCATOR);
        nk_font_atlas_begin(atlas);
        
        font = Objects.requireNonNull(nk_font_atlas_add_default(atlas, 13f, null));

        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            ByteBuffer image = Objects.requireNonNull(nk_font_atlas_bake(atlas, w, h, NK_FONT_ATLAS_RGBA32));

            fontTexture = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, fontTexture);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w.get(0), h.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

            nk_font_atlas_end(atlas, NkHandle.create().id(fontTexture), nullTexture);
        }

        if (!nk_init(ctx, ALLOCATOR, font.handle())) {
            throw new IllegalStateException("Failed to initialise Nuklear");
        }
    }

    private void createGlObjects() {
        program = glCreateProgram();
        int vs = compile(GL_VERTEX_SHADER, VERTEX_SHADER);
        int fs = compile(GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        glAttachShader(program, vs);
        glAttachShader(program, fs);
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) != GL_TRUE) {
            throw new IllegalStateException("Program link failed: " + glGetProgramInfoLog(program));
        }
        glDeleteShader(vs);
        glDeleteShader(fs);

        uniformTexture = glGetUniformLocation(program, "Texture");
        uniformProjection = glGetUniformLocation(program, "ProjMtx");
        int attribPosition = glGetAttribLocation(program, "Position");
        int attribTexCoord = glGetAttribLocation(program, "TexCoord");
        int attribColor = glGetAttribLocation(program, "Color");

        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

        glEnableVertexAttribArray(attribPosition);
        glEnableVertexAttribArray(attribTexCoord);
        glEnableVertexAttribArray(attribColor);

        glVertexAttribPointer(attribPosition, 2, GL_FLOAT, false, 20, 0);
        glVertexAttribPointer(attribTexCoord, 2, GL_FLOAT, false, 20, 8);
        glVertexAttribPointer(attribColor, 4, GL_UNSIGNED_BYTE, true, 20, 16);

        glBindTexture(GL_TEXTURE_2D, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    private int compile(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) != GL_TRUE) {
            throw new IllegalStateException("Shader compile failed: " + glGetShaderInfoLog(shader));
        }
        return shader;
    }

    public NkContext getContext() {
        return ctx;
    }

    public void handleEvents() {
        nk_input_begin(ctx);
        glfwPollEvents();
        nk_input_end(ctx);
    }

    public void render() {
        int width, height, displayW, displayH;
        long window = Game.getInstance().getWindow();

        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            glfwGetWindowSize(window, w, h);
            width = w.get(0);
            height = h.get(0);
            glfwGetFramebufferSize(window, w, h);
            displayW = w.get(0);
            displayH = h.get(0);
        }
        if (width == 0 || height == 0) {
            nk_clear(ctx);
            return;
        }

        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glEnable(GL_SCISSOR_TEST);

        glUseProgram(program);
        glUniform1i(uniformTexture, 0);
        glActiveTexture(GL_TEXTURE0);

        try (MemoryStack stack = stackPush()) {
            glUniformMatrix4fv(uniformProjection, false, stack.floats(
                    2.0f / width, 0.0f, 0.0f, 0.0f,
                    0.0f, -2.0f / height, 0.0f, 0.0f,
                    0.0f, 0.0f, -1.0f, 0.0f,
                    -1.0f, 1.0f, 0.0f, 1.0f
            ));
        }

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ARRAY_BUFFER, MAX_VERTEX_BUFFER, GL_STREAM_DRAW);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, MAX_ELEMENT_BUFFER, GL_STREAM_DRAW);

        ByteBuffer vertices = Objects.requireNonNull(glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, MAX_VERTEX_BUFFER, null));
        ByteBuffer elements = Objects.requireNonNull(glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_WRITE_ONLY, MAX_ELEMENT_BUFFER, null));

        try (MemoryStack stack = stackPush()) {
            NkConvertConfig config = NkConvertConfig.calloc(stack)
                    .vertex_layout(VERTEX_LAYOUT)
                    .vertex_size(20)
                    .vertex_alignment(4)
                    .tex_null(nullTexture)
                    .circle_segment_count(22)
                    .curve_segment_count(22)
                    .arc_segment_count(22)
                    .global_alpha(1.0f)
                    .shape_AA(NK_ANTI_ALIASING_ON)
                    .line_AA(NK_ANTI_ALIASING_ON);

            NkBuffer vbuf = NkBuffer.malloc(stack);
            NkBuffer ebuf = NkBuffer.malloc(stack);
            nk_buffer_init_fixed(vbuf, vertices);
            nk_buffer_init_fixed(ebuf, elements);
            nk_convert(ctx, cmds, vbuf, ebuf, config);
        }
        glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
        glUnmapBuffer(GL_ARRAY_BUFFER);

        float scaleX = (float) displayW / width;
        float scaleY = (float) displayH / height;

        long offset = NULL;
        for (NkDrawCommand cmd = nk__draw_begin(ctx, cmds); cmd != null; cmd = nk__draw_next(cmd, cmds, ctx)) {
            if (cmd.elem_count() == 0) {
                continue;
            }
            glBindTexture(GL_TEXTURE_2D, cmd.texture().id());
            NkRect clip = cmd.clip_rect();
            glScissor(
                    (int) (clip.x() * scaleX),
                    (int) ((height - (int) (clip.y() + clip.h())) * scaleY),
                    (int) (clip.w() * scaleX),
                    (int) (clip.h() * scaleY)
            );
            glDrawElements(GL_TRIANGLES, cmd.elem_count(), GL_UNSIGNED_SHORT, offset);
            offset += cmd.elem_count() * 2L;
        }
        nk_clear(ctx);
        nk_buffer_clear(cmds);

        glUseProgram(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        glDisable(GL_BLEND);
        glDisable(GL_SCISSOR_TEST);
    }

    public float textWidth(String text) {
        float w = 0;

        for (int i = 0; i < text.length(); ) {
            int cp = Character.codePointAt(text, i);
            i += Character.charCount(cp);
            NkFontGlyph g = nk_font_find_glyph(font, cp);
            if (g != null) w += g.xadvance();
        }
        return w;
    }

    public float buttonWidth(String text) {
        NkStyleButton b = ctx.style().button();

        return textWidth(text) +
         + 2 * b.padding().x()
         + b.border()
         + 2 * b.rounding()
         + 2;
    }

    public float optionWidth(String text) {
        NkStyleToggle t = ctx.style().option();
        return t.padding().x()                   
            + ctx.style().font().height()        
            + t.spacing()                        
            + textWidth(text)         
            + t.padding().x()                   
            + 2;                                
    }

    public float checkboxWidth(String text) {
        NkStyleToggle t = ctx.style().checkbox();
        return t.padding().x() * 2
            + ctx.style().font().height()
            + t.spacing()
            + textWidth(text)
            + 2;
    }

    public float labelWidth(String text) {
        return textWidth(text) + 4;
    }

    public AutoCloseable pushStack() {
        return MemoryStack.stackPush();
    }

    public boolean beginWindow(String name, int x, int y, int w, int h, boolean border, AutoCloseable stack) {
        NkRect rect = NkRect.malloc((MemoryStack)stack);

        return nk_begin(ctx, name, nk_rect(x, y, w, h, rect), NK_WINDOW_NO_SCROLLBAR | ((border) ? NK_WINDOW_BORDER : 0));
    }

    public void endWindow() {
        nk_end(ctx);
    }

    public void beginRow(float height, int columns) {
        nk_layout_row_begin(ctx, NK_STATIC, height, columns);
    }

    public void rowPush(float width) {
        nk_layout_row_push(ctx, width);
    }

    public void endRow() {
        nk_layout_row_end(ctx);
    }

    public void label(String text) {
        nk_label(ctx, text, NK_TEXT_ALIGN_LEFT);
    }

    public boolean button(String text) {
        return nk_button_label(ctx, text);
    }

    public boolean option(String text, boolean selected) {
        return nk_option_label(ctx, text, selected);
    }

    public boolean checkbox(String text, boolean selected) {
        return nk_check_label(ctx, text, selected);
    }

    public boolean beginPopup(String title, int x, int y, int w, int h, AutoCloseable stack) {
        NkRect r = NkRect.malloc((MemoryStack)stack).set(x, y, w, h);

        return nk_popup_begin(ctx, NK_POPUP_STATIC, title, 0, r);
    }

    public void endPopup() {
        nk_popup_end(ctx);
    }

    public Integer popup(String text, String title, Vector<Object> items, int menuW, int menuH, boolean[] menuOpen, boolean above, AutoCloseable stack) {
        Integer hit = null;

        NkRect buttonBounds = NkRect.malloc((MemoryStack)stack);

        nk_widget_bounds(ctx, buttonBounds);

        NkVec2 p = NkVec2.malloc((MemoryStack)stack);

        nk_window_get_position(ctx, p);

        float x = buttonBounds.x() - p.x();
        float y = buttonBounds.y() - p.y();
        float h = buttonBounds.h();

        y = (above) ? y - menuH : y + h;

        if(button(text)) {
            menuOpen[0] = !menuOpen[0];
        }

        if(menuOpen[0]) {
            NkRect r = NkRect.malloc((MemoryStack)stack).set(x, y, menuW, menuH);

            if (nk_popup_begin(ctx, NK_POPUP_STATIC, title, 0, r)) {
                NkStyleButton btn = ctx.style().button();
                float oldBorder = btn.border();
                float oldRounding = btn.rounding();
                int oldAlign = btn.text_alignment();
                NkColor oldNormal = NkColor.malloc((MemoryStack)stack).set(btn.normal().data().color());
                NkVec2 oldPadding = NkVec2.malloc((MemoryStack)stack).set(btn.padding());
                NkColor popupBg = ctx.style().window().fixed_background().data().color();

                btn.border(0f);
                btn.rounding(0f);
                btn.text_alignment(NK_TEXT_LEFT);        
                btn.padding().set(8f, 4f);               
                btn.normal().data().color().set(popupBg); 

                nk_layout_row_dynamic(ctx, 25, 1);
                for(int i = 0; i != items.size(); i++) {
                    if(nk_button_label(ctx, items.get(i).toString())) {
                        hit = i;
                        nk_popup_close(ctx);
                        menuOpen[0] = false;
                    }
                }
                btn.border(oldBorder);
                btn.rounding(oldRounding);
                btn.text_alignment(oldAlign);
                btn.padding().set(oldPadding);
                btn.normal().data().color().set(oldNormal);

                nk_popup_end(ctx);
            } 
        }
        return hit;
    }
    
    @Override
    public void destroy() {
        nk_font_atlas_clear(atlas);
        nk_free(ctx);
        nk_buffer_free(cmds);
        glDeleteTextures(fontTexture);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
        glDeleteProgram(program);
        super.destroy();
    }
}
