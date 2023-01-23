package com.mosect.lib.easygl2.g2d;

import android.opengl.GLES20;

import com.mosect.lib.easygl2.GLException;
import com.mosect.lib.easygl2.GLShader;

import java.nio.Buffer;

/**
 * 矩形shader，常用于绘制{@link android.graphics.Bitmap}
 */
public class GLShaderRect extends GLShader implements GLShader2D {

    private int uniCameraMatrix;
    private int uniTextureMatrix;
    private int attrPosition;
    private int attrTextureCoord;
    private int uniTextureNum;

    private final float[] textureMatrix = new float[16];

    @Override
    protected String onLoadVertSource() {
        return "uniform mat4 cameraMatrix;\n" +
                "uniform mat4 textureMatrix;\n" +
                "attribute vec4 position;\n" +
                "attribute vec4 textureCoord;\n" +
                "varying vec2 colorCoord;\n" +
                "\n" +
                "void main() {\n" +
                "    gl_Position = cameraMatrix * position;\n" +
                "    colorCoord = (textureMatrix * textureCoord).xy;\n" +
                "}\n";
    }

    @Override
    protected String onLoadFragSource() {
        return "precision mediump float;\n" +
                "varying vec2 colorCoord;\n" +
                "uniform sampler2D textureNum;\n" +
                "\n" +
                "void main() {\n" +
                "    gl_FragColor = texture2D(textureNum, colorCoord);\n" +
                "}";
    }

    @Override
    protected void onLinkProgramBefore() {
        super.onLinkProgramBefore();
        attrTextureCoord = 1;
        GLES20.glBindAttribLocation(getProgramId(), attrTextureCoord, "textureCoord");
    }

    @Override
    protected void onInitProgram() {
        uniCameraMatrix = getUniformLocation("cameraMatrix");
        uniTextureMatrix = getUniformLocation("textureMatrix");
        attrPosition = getAttribLocation("position");
        uniTextureNum = getUniformLocation("textureNum");
    }

    public void draw(int textureID, float[] textureMatrix, float[] cameraMatrix, Buffer vertices, Buffer textureCoords) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);

        GLES20.glUseProgram(getProgramId());
        GLException.checkGLError("glUseProgram");

        GLES20.glVertexAttribPointer(attrPosition, 3, GLES20.GL_FLOAT, false, 12, vertices);
        GLException.checkGLError("glVertexAttribPointer/attrPosition");
        GLES20.glEnableVertexAttribArray(attrPosition);
        GLException.checkGLError("glEnableVertexAttribArray/attrPosition");

        GLES20.glVertexAttribPointer(attrTextureCoord, 2, GLES20.GL_FLOAT, false, 8, textureCoords);
        GLException.checkGLError("glVertexAttribPointer/attrTextureCoord");
        GLES20.glEnableVertexAttribArray(attrTextureCoord);
        GLException.checkGLError("glEnableVertexAttribArray/attrTextureCoord");

        GLES20.glUniformMatrix4fv(uniCameraMatrix, 1, false, cameraMatrix, 0);
        GLException.checkGLError("glUniformMatrix4fv/uniCameraMatrix");
        GLES20.glUniformMatrix4fv(uniTextureMatrix, 1, false, textureMatrix, 0);
        GLException.checkGLError("glUniformMatrix4fv/uniTextureMatrix");
        GLES20.glUniform1i(uniTextureNum, 0);
        GLException.checkGLError("glUniformMatrix4fv/uniTextureNum");

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLException.checkGLError("glDrawArrays");

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void draw(GLTexture2D texture2D, float[] cameraMatrix, Buffer vertices, Buffer textureCoords) {
        texture2D.getMatrix(textureMatrix, 0);
        draw(texture2D.getTextureId(), textureMatrix, cameraMatrix, vertices, textureCoords);
    }
}
