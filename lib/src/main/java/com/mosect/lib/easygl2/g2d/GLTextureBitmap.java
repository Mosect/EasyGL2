package com.mosect.lib.easygl2.g2d;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.mosect.lib.easygl2.GLException;
import com.mosect.lib.easygl2.GLTexture;

/**
 * 位图纹理
 */
public class GLTextureBitmap extends GLTexture implements GLTexture2D {

    private final GLBitmapProvider bitmapProvider;
    private Bitmap bitmap;

    public GLTextureBitmap(GLBitmapProvider bitmapProvider) {
        super(GLES20.GL_TEXTURE_2D);
        this.bitmapProvider = bitmapProvider;
    }

    @Override
    protected void onInit() throws GLException {
        super.onInit();
        bitmap = bitmapProvider.getBitmap(this);
        if (null != bitmap) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }
    }

    @Override
    protected void onClear() throws GLException {
        super.onClear();
        if (null != bitmap) {
            bitmapProvider.destroyBitmap(this, bitmap);
            bitmap = null;
        }
    }

    @Override
    public int getWidth() {
        if (null != bitmap) return bitmap.getWidth();
        return 0;
    }

    @Override
    public int getHeight() {
        if (null != bitmap) return bitmap.getHeight();
        return 0;
    }

    @Override
    public void getMatrix(float[] matrix, int offset) {
        Matrix.setIdentityM(matrix, offset);
    }
}
