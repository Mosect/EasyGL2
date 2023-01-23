package com.mosect.lib.easygl2;

import android.opengl.EGL14;
import android.opengl.GLES20;

public class GLException extends RuntimeException {

    /**
     * 检测EGL错误
     *
     * @param name 名称
     */
    public static void checkEGLError(String name) throws GLException {
        int error = EGL14.eglGetError();
        if (error != EGL14.EGL_SUCCESS) {
            throw new GLException(String.format("%s.eglGetError: 0x%x (%s)", name, error, error), "EGL_ERROR");
        }
    }

    /**
     * 检测GL错误
     *
     * @param name 名称
     */
    public static void checkGLError(String name) throws GLException {
        int error;
        if ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            throw new GLException(String.format("%s.glGetError: 0x%x (%s)", name, error, error), "GL_ERROR");
        }
    }

    private final String code;

    public GLException(String code) {
        this.code = code;
    }

    public GLException(String message, String code) {
        super(message);
        this.code = code;
    }

    public GLException(String message, Throwable cause, String code) {
        super(message, cause);
        this.code = code;
    }

    public GLException(Throwable cause, String code) {
        super(cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
