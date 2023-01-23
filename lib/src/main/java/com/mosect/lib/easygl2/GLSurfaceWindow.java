package com.mosect.lib.easygl2;

import android.opengl.EGL14;
import android.opengl.EGLSurface;

/**
 * 窗口surface，可以将图像输出到窗口对象中
 */
public class GLSurfaceWindow extends GLSurface {

    private final Object window;
    private final int width;
    private final int height;

    /**
     * 创建窗口surface
     *
     * @param window 窗口对象，可以是{@link android.view.Surface}、{@link android.graphics.SurfaceTexture}、{@link android.view.SurfaceHolder}
     * @param width  宽
     * @param height 高
     */
    public GLSurfaceWindow(Object window, int width, int height) {
        this.window = window;
        this.width = width;
        this.height = height;
    }

    @Override
    protected EGLSurface createSurface() throws GLException {
        int[] attribList = {
                EGL14.EGL_NONE,
        };
        EGLSurface surface = EGL14.eglCreateWindowSurface(getContext().getEGLDisplay(),
                getContext().getEGLConfig(), window, attribList, 0);
        if (surface == EGL14.EGL_NO_SURFACE) {
            throw new GLException("eglCreateWindowSurface failed", "GL_SURFACE_ERROR");
        }
        return surface;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
