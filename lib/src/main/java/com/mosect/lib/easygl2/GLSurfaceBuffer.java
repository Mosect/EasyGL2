package com.mosect.lib.easygl2;

import android.opengl.EGL14;
import android.opengl.EGLSurface;

/**
 * PBuffer
 */
public class GLSurfaceBuffer extends GLSurface {

    private final int width;
    private final int height;

    public GLSurfaceBuffer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    protected EGLSurface createSurface() throws GLException {
        int[] attribList = {
                EGL14.EGL_WIDTH, width,
                EGL14.EGL_HEIGHT, height,
                EGL14.EGL_NONE
        };
        EGLSurface surface = EGL14.eglCreatePbufferSurface(getContext().getEGLDisplay(),
                getContext().getEGLConfig(), attribList, 0);
        if (surface == EGL14.EGL_NO_SURFACE) {
            throw new GLException("eglCreatePbufferSurface failed", "GL_SURFACE_ERROR");
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
