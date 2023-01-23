package com.mosect.lib.easygl2;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.util.Log;

/**
 * opengl上下文环境
 */
public class GLContext implements AutoCloseable {

    private static final String TAG = "EsGL/GLContext";

    private final int defaultWidth;
    private final int defaultHeight;
    private int state = 0;

    private EGLDisplay eglDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLContext eglContext = EGL14.EGL_NO_CONTEXT;
    private EGLSurface eglSurface = EGL14.EGL_NO_SURFACE;
    private EGLConfig eglConfig = null;

    /**
     * 创建opengl上下文环境对象，由于必须存在一个EGLSurface，因此必须设定默认大小，此EGLSurface不用于实际用途
     *
     * @param defaultWidth  默认宽度，大于0的双数，推荐64
     * @param defaultHeight 默认高度，大于0的双数，推荐64
     */
    public GLContext(int defaultWidth, int defaultHeight) {
        if (defaultWidth <= 0) {
            throw new IllegalArgumentException("Invalid defaultWidth: " + defaultWidth);
        }
        if (defaultHeight <= 0) {
            throw new IllegalArgumentException("Invalid defaultHeight: " + defaultHeight);
        }
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
    }

    /**
     * 初始化环境
     *
     * @throws GLException opengl异常
     */
    public void init() throws GLException {
        if (state != 0) {
            throw new IllegalStateException("Initialed or destroyed");
        }
        state = 1;
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new GLException("eglGetDisplay failed", "GL_DISPLAY_ERROR");
        }
        int[] version = new int[2];
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
            Log.e(TAG, "init.display: eglInitialize failed");
            eglDisplay = EGL14.EGL_NO_DISPLAY;
            throw new GLException("eglInitialize failed", "GL_DISPLAY_ERROR");
        }
        Log.d(TAG, String.format("init.display: %s.%s", version[0], version[1]));
        try {
            eglConfig = chooseConfig();
            eglContext = createContext();
            eglSurface = createSurface();
            state = 2;
        } catch (Exception e) {
            clearGL();
            throw e;
        }
    }

    /**
     * 选择EGLConfig
     *
     * @return EGLConfig
     * @throws GLException opengl异常
     */
    protected EGLConfig chooseConfig() throws GLException {
        int[] attribList = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                EGL14.EGL_NONE,
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        boolean ok = EGL14.eglChooseConfig(eglDisplay, attribList, 0, configs,
                0, 1, numConfigs, 0);
        if (!ok) {
            throw new GLException("eglChooseConfig failed", "GL_CONFIG_ERROR");
        }
        return configs[0];
    }

    /**
     * 创建EGLContext
     *
     * @return EGLContext
     * @throws GLException opengl异常
     */
    protected EGLContext createContext() throws GLException {
        int[] attribList = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE,
        };
        EGLContext context = EGL14.eglCreateContext(eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT, attribList, 0);
        GLException.checkEGLError("eglCreateContext");
        if (context == EGL14.EGL_NO_CONTEXT) {
            throw new GLException("eglCreateContext failed", "GL_CONTEXT_ERROR");
        }
        return context;
    }

    /**
     * 创建EGLSurface，仅用于初始化环境
     *
     * @return EGLSurface
     * @throws GLException opengl异常
     */
    protected EGLSurface createSurface() throws GLException {
        int[] attribList = {
                EGL14.EGL_WIDTH, defaultWidth,
                EGL14.EGL_HEIGHT, defaultHeight,
                EGL14.EGL_NONE,
        };
        EGLSurface surface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, attribList, 0);
        GLException.checkEGLError("eglCreatePbufferSurface");
        if (surface == EGL14.EGL_NO_SURFACE) {
            throw new GLException("eglCreatePbufferSurface failed", "GL_SURFACE_ERROR");
        }
        return surface;
    }

    /**
     * 切换成默认EGLSurface
     *
     * @return true，切换成功；false，切换失败
     */
    public boolean makeCurrent() {
        if (state != 2) {
            throw new IllegalStateException("Not initialed");
        }
        return EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
    }

    /**
     * 切换成默认EGLSurface
     *
     * @throws GLException 切换失败，抛出opengl异常
     */
    public void makeCurrentWithException() throws GLException {
        if (!makeCurrent()) {
            throw new GLException("makeCurrent failed", "GL_CONTEXT_ERROR");
        }
    }

    /**
     * 释放opengl资源
     *
     * @throws GLException opengl异常
     */
    @Override
    public void close() throws GLException {
        if (state != 3) {
            state = 3;
            clearGL();
        }
    }

    /**
     * 清除opengl资源对象
     */
    private void clearGL() {
        if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            if (eglSurface != EGL14.EGL_NO_SURFACE) {
                EGL14.eglDestroySurface(eglDisplay, eglSurface);
                eglSurface = EGL14.EGL_NO_SURFACE;
            }
            if (eglContext != EGL14.EGL_NO_CONTEXT) {
                EGL14.eglDestroyContext(eglDisplay, eglContext);
                eglContext = EGL14.EGL_NO_CONTEXT;
            }
            EGL14.eglTerminate(eglDisplay);
            eglDisplay = EGL14.EGL_NO_DISPLAY;
        }
    }

    public int getDefaultWidth() {
        return defaultWidth;
    }

    public int getDefaultHeight() {
        return defaultHeight;
    }

    public EGLDisplay getEGLDisplay() {
        return eglDisplay;
    }

    public EGLContext getEGLContext() {
        return eglContext;
    }

    public EGLConfig getEGLConfig() {
        return eglConfig;
    }
}
