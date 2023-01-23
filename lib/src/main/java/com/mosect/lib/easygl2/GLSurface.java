package com.mosect.lib.easygl2;

import android.opengl.EGL14;
import android.opengl.EGLSurface;
import android.opengl.GLES20;

/**
 * opengl图像输出对象
 */
public abstract class GLSurface extends GLObject {

    private EGLSurface eglSurface = EGL14.EGL_NO_SURFACE;

    @Override
    protected void onInit() throws GLException {
        eglSurface = createSurface();
    }

    @Override
    protected void onClear() throws GLException {
        if (eglSurface != EGL14.EGL_NO_SURFACE) {
            getContext().makeCurrent();
            EGL14.eglDestroySurface(getContext().getEGLDisplay(), eglSurface);
            eglSurface = EGL14.EGL_NO_SURFACE;
        }
    }

    /**
     * 创建EGLSurface
     *
     * @return EGLSurface
     * @throws GLException opengl异常
     */
    protected abstract EGLSurface createSurface() throws GLException;

    /**
     * 切换成当前EGLSurface
     *
     * @return true，切换成功；false，切换失败
     */
    public boolean makeCurrent() {
        if (eglSurface != EGL14.EGL_NO_SURFACE) {
            boolean ok = EGL14.eglMakeCurrent(getContext().getEGLDisplay(),
                    eglSurface, eglSurface, getContext().getEGLContext());
            if (ok) {
                GLES20.glViewport(0, 0, getWidth(), getHeight());
                GLException.checkGLError("glViewport");
                return true;
            }
        }
        return false;
    }

    /**
     * 提交绘制的内容
     *
     * @return true，绘制成功；false，绘制失败
     */
    public boolean commit() {
        if (eglSurface != EGL14.EGL_NO_SURFACE) {
            return EGL14.eglSwapBuffers(getContext().getEGLDisplay(), eglSurface);
        }
        return false;
    }

    /**
     * 清空背景色为黑色
     */
    public void clearToBlack() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLException.checkGLError("glClear");
    }

    /**
     * EGLSurface宽度
     *
     * @return 宽度
     */
    public abstract int getWidth();

    /**
     * EGLSurface高度
     *
     * @return 高度
     */
    public abstract int getHeight();
}
