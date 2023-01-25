package com.mosect.lib.easygl2;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.view.Surface;

import com.mosect.lib.easygl2.GLException;
import com.mosect.lib.easygl2.GLTexture;
import com.mosect.lib.easygl2.g2d.GLTexture2D;

/**
 * 窗口纹理，用于将窗口内容绘制到EGLSurface中
 */
public class GLTextureWindow extends GLTexture implements GLTexture2D {

    private final int width;
    private final int height;
    private final boolean createSurface;
    private final float[] matrix = new float[16];

    private SurfaceTexture surfaceTexture;
    private Surface surface;

    private boolean frameAvailable;
    private final byte[] textureLock = new byte[0];

    public GLTextureWindow(int width, int height, boolean createSurface) {
        super(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        if (width <= 0) throw new IllegalArgumentException("Invalid width: " + width);
        if (height <= 0) throw new IllegalArgumentException("Invalid height: " + height);
        this.width = width;
        this.height = height;
        this.createSurface = createSurface;
    }

    @Override
    protected void onInit() throws GLException {
        super.onInit();
        surfaceTexture = new SurfaceTexture(getTextureId());
        surfaceTexture.setDefaultBufferSize(width, height);
        surfaceTexture.setOnFrameAvailableListener(surfaceTexture -> {
            synchronized (textureLock) {
                frameAvailable = true;
            }
        });
        if (createSurface) {
            surface = new Surface(surfaceTexture);
        }
    }

    @Override
    protected void onClear() throws GLException {
        super.onClear();
        if (null != surface) {
            surface.release();
            surface = null;
        }
        if (null != surfaceTexture) {
            surfaceTexture.release();
            surfaceTexture = null;
        }
    }

    /**
     * 更新纹理图像，{@link SurfaceTexture#updateTexImage()}
     *
     * @return true，更新成功；false，更新失败，使用旧图像
     */
    public boolean updateTexImage() {
        synchronized (textureLock) {
            if (frameAvailable) {
                surfaceTexture.updateTexImage();
                frameAvailable = false;
                return true;
            }
            return false;
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void getMatrix(float[] matrix, int offset) {
        surfaceTexture.getTransformMatrix(this.matrix);
        System.arraycopy(this.matrix, 0, matrix, offset, this.matrix.length);
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public Surface getSurface() {
        return surface;
    }

    public boolean isFrameAvailable() {
        return frameAvailable;
    }
}
