package com.mosect.app.easygl2;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

import com.mosect.lib.easygl2.GLContext;
import com.mosect.lib.easygl2.GLShader;
import com.mosect.lib.easygl2.GLSurface;
import com.mosect.lib.easygl2.GLTexture;
import com.mosect.lib.easygl2.g2d.GLDrawer2D;
import com.mosect.lib.easygl2.g2d.GLShader2D;
import com.mosect.lib.easygl2.g2d.GLTexture2D;
import com.mosect.lib.easygl2.GLTextureWindow;
import com.mosect.lib.easygl2.g2d.Matrix2D;

import java.util.LinkedList;

public class GLThread {

    private static final String TAG = "App/GLThread";

    private int state = 0;
    private final byte[] lock = new byte[0];
    private final LinkedList<Runnable> actions = new LinkedList<>();

    private GLContext currentContext;
    private GLTexture currentTexture;
    private GLShader currentShader;
    private GLSurface currentSurface;
    private String currentScaleType = "FIT_XY";
    private GLDrawer2D currentDrawer;

    private Callback callback;

    public void start() {
        synchronized (lock) {
            if (state == 0) {
                state = 1;
                new Thread(this::loop).start();
            }
        }
    }

    public void setScaleType(String scaleType) {
        runAction(() -> {
            currentScaleType = scaleType;
            updateDrawer();
        });
    }

    private void updateDrawer() {
        if (null != currentDrawer && null != currentSurface) {
            Matrix matrix = new Matrix();
            GLTexture2D texture2D = (GLTexture2D) currentTexture;
            RectF contentRect = new RectF(0, 0, texture2D.getWidth(), texture2D.getHeight());
            RectF viewportRect = new RectF(0, 0, currentSurface.getWidth(), currentSurface.getHeight());
            switch (currentScaleType) {
                case "CENTER_INSIDE":
                    Matrix2D.centerInside(contentRect, viewportRect, matrix);
                    break;
                case "CENTER_CROP":
                    Matrix2D.centerCrop(contentRect, viewportRect, matrix);
                    break;
                case "FIT_XY":
                    Matrix2D.fixXY(contentRect, viewportRect, matrix);
                    break;
            }
            currentDrawer.setMatrix(matrix);
            currentDrawer.setCameraRect(currentSurface.getWidth(), currentSurface.getHeight());
        }
    }

    public void setSurface(GLSurface surface) {
        runAction(() -> {
            if (null != currentSurface) {
                currentSurface.close();
            }
            currentSurface = surface;
            if (null != currentSurface) {
                currentSurface.init(currentContext);
            }
            updateDrawer();
        });
    }

    public void setContent(GLTexture texture, GLShader shader) {
        runAction(() -> {
            if (null != currentTexture) {
                currentTexture.close();
            }
            if (null != currentShader) {
                if (currentShader != shader) {
                    currentShader.close();
                }
            }
            if (null != texture && null != shader) {
                texture.init(currentContext);
                shader.init(currentContext);
                currentDrawer = new GLDrawer2D((GLTexture2D) texture, (GLShader2D) shader);
                currentShader = shader;
                currentTexture = texture;
                updateDrawer();
                Callback callback = this.callback;
                if (null != callback) {
                    callback.onGLTextureInitialed(texture);
                }
            } else {
                currentDrawer = null;
                currentShader = null;
                currentTexture = null;
            }
        });
    }

    private void runAction(Runnable action) {
        synchronized (lock) {
            if (state != 2) {
                actions.addLast(action);
            }
        }
    }

    public void release() {
        synchronized (lock) {
            if (state != 2) {
                state = 2;
            }
        }
    }

    private void loop() {
        Log.d(TAG, "loop: start");
        try {
            currentContext = new GLContext(64, 64);
            currentContext.init();
            while (true) {
                currentContext.makeCurrentWithException();
                synchronized (lock) {
                    if (state != 1) break;
                    while (actions.size() > 0) actions.removeFirst().run();
                }
                if (null != currentSurface) {
                    if (currentSurface.makeCurrent()) {
                        currentSurface.clearToBlack();
                        if (currentTexture instanceof GLTextureWindow) {
                            ((GLTextureWindow) currentTexture).updateTexImage();
                        }
                        if (null != currentDrawer) {
                            currentDrawer.draw();
                        }
                        if (currentSurface.commit()) {
                            Callback callback = this.callback;
                            if (null != callback) {
                                callback.onGLSurfaceCommitted(currentSurface);
                            }
                        } else {
                            Log.w(TAG, "loop: commit failed");
                        }
                    } else {
                        Log.w(TAG, "loop: makeCurrent failed");
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "loop: ", e);
            Callback callback = this.callback;
            if (null != callback) {
                callback.onGLError(e);
            }
        } finally {
            synchronized (lock) {
                if (null != currentShader) currentShader.close();
                if (null != currentTexture) currentTexture.close();
                if (null != currentSurface) currentSurface.close();
                currentContext.close();
            }
        }
        Log.d(TAG, "loop: end");
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {

        void onGLTextureInitialed(GLTexture texture);

        void onGLSurfaceCommitted(GLSurface surface);

        void onGLError(Exception exp);
    }
}
