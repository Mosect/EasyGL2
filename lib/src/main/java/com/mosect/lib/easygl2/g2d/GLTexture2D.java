package com.mosect.lib.easygl2.g2d;

/**
 * 2D纹理
 */
public interface GLTexture2D {

    int getTextureId();

    int getWidth();

    int getHeight();

    void getMatrix(float[] matrix, int offset);
}
