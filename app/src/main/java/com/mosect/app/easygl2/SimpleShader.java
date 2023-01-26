package com.mosect.app.easygl2;

import android.content.Context;
import android.opengl.GLES20;

import com.mosect.lib.easygl2.GLException;
import com.mosect.lib.easygl2.GLShader;
import com.mosect.lib.easygl2.util.GLUtils;

import java.nio.Buffer;

public class SimpleShader extends GLShader {

    private final Context context;
    private int positionHandle;
    private int colorHandle;

    public SimpleShader(Context context) {
        this.context = context;
    }

    @Override
    protected String onLoadVertSource() {
        return GLUtils.loadAssetsText(context, "shader_simple.vert");
    }

    @Override
    protected String onLoadFragSource() {
        return GLUtils.loadAssetsText(context, "shader_simple.frag");
    }

    @Override
    protected void onInitProgram() {
        super.onInitProgram();
        positionHandle = getAttribLocation("position");
        colorHandle = getUniformLocation("color");
    }

    public void draw(Buffer position, float[] color) {
        GLES20.glUseProgram(getProgramId());
        GLException.checkGLError("glUseProgram");

        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        GLException.checkGLError("glUniform4fv/color");

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, position);
        GLException.checkGLError("glVertexAttribPointer/position");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLException.checkGLError("glEnableVertexAttribArray/position");

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLException.checkGLError("glDrawArrays");
    }
}
