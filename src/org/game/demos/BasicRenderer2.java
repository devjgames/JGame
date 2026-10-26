package org.game.demos;

import org.game.BasicRenderer;
import org.game.Texture;
import org.game.Vec4;

public class BasicRenderer2 extends BasicRenderer {
    
    public BasicRenderer2() throws Exception {
    }

    @Override
    public void begin(int width, int height, Vec4 backgroundColor) throws Exception {
        super.begin(width, height, backgroundColor);
        setDecal(null);
    }

    public void setDecal(Texture texture) {
        getShader().set("uDecalEnabled", texture != null);
        if(texture != null) {
            getShader().set("uDecal", texture, 1);
        }
    }

    @Override
    protected String getCustomFragmentUniforms() {
        return 
            "uniform sampler2D uDecal;\n" + 
            "uniform int uDecalEnabled;\n";
    }

    @Override
    protected String getCustomFragmentMain() {
        return 
            "\tif(uDecalEnabled != 0) {\n" +
            "\t\tvec4 c = texture(uDecal, vTextureCoordinate);\n" +
            "\t\tcolor.rgb = (1.0 - c.a) * color.rgb + c.a * c.rgb;\n" +
            "\t}\n";
    }
}
