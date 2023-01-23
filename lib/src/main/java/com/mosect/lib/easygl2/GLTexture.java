package com.mosect.lib.easygl2;

import android.opengl.GLES20;

/**
 * opengl纹理
 */
public class GLTexture extends GLObject {

    private final int type;
    private int textureId = 0;

    public GLTexture(int type) {
        this.type = type;
    }

    @Override
    protected void onClear() throws GLException {
        if (textureId != 0) {
            GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
            textureId = 0;
        }
    }

    @Override
    protected void onInit() throws GLException {
        textureId = generateTexture();
        initTexture();
    }

    /**
     * 创建纹理
     *
     * @return 纹理id
     */
    protected int generateTexture() throws GLException {
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        GLException.checkGLError("glGenTextures");
        return textureIds[0];
    }

    /**
     * 初始化纹理
     *
     * @throws GLException opengl异常
     */
    protected void initTexture() throws GLException {
        GLES20.glBindTexture(type, getTextureId());
        GLException.checkGLError("glBindTexture");
        GLES20.glTexParameterf(type, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(type, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(type, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(type, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLException.checkGLError("glTexParameter");
    }

    /**
     * 获取纹理id
     *
     * @return 纹理id
     */
    public int getTextureId() {
        return textureId;
    }

    /**
     * 获取纹理类型
     *
     * @return 纹理类型
     */
    public int getType() {
        return type;
    }
}
