package com.mosect.app.easygl2;

import android.opengl.GLES20;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mosect.lib.easygl2.GLContext;
import com.mosect.lib.easygl2.GLException;
import com.mosect.lib.easygl2.GLSurfaceWindow;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class SimpleActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "SimpleActivity";
    private DrawThread drawThread = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SurfaceView surfaceView = new SurfaceView(this);
        setContentView(surfaceView);
        surfaceView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (null != drawThread) {
            drawThread.destroy();
        }
        drawThread = new DrawThread(holder.getSurface(), width, height);
        drawThread.start();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (null != drawThread) {
            drawThread.destroy();
            drawThread = null;
        }
    }

    private class DrawThread {

        private final Surface surface;
        private final int width;
        private final int height;
        private final byte[] lock = new byte[0];
        private int state = 0;

        public DrawThread(Surface surface, int width, int height) {
            this.surface = surface;
            this.width = width;
            this.height = height;
        }

        private void loop() {
            Log.d(TAG, "loop: start");
            GLContext glContext = null;
            SimpleShader simpleShader = null;
            GLSurfaceWindow glSurfaceWindow = null;
            try {
                glContext = new GLContext(64, 64);
                glContext.init();
                glContext.makeCurrentWithException();

                simpleShader = new SimpleShader(getApplicationContext());
                simpleShader.init(glContext);

                glSurfaceWindow = new GLSurfaceWindow(surface, width, height);
                glSurfaceWindow.init(glContext);

                float[] red = {1, 0, 0, 0.5f};
                float[] green = {0, 1, 0, 1f};
                Buffer rect1 = createRect(new float[]{
                        0, 0, 0.1f,
                        1, 0, 0.1f,
                        0, 1, 0.1f,
                        1, 1, 0.1f
                });
                Buffer rect2 = createRect(new float[]{
                        -0.5f, -0.5f, 0.5f,
                        0.5f, -0.5f, 0.5f,
                        -0.5f, 0.5f, 0.5f,
                        0.5f, 0.5f, 0.5f
                });

                while (state == 1) {
                    if (glSurfaceWindow.makeCurrent()) {
                        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1f);
                        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
                        GLException.checkGLError("glClear");
                        // 开启深度测试
                        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
                        GLES20.glDepthFunc(GLES20.GL_LESS);
                        // 开启深度写入
                        GLES20.glDepthMask(true);
                        // 开启混合模式
                        GLES20.glEnable(GLES20.GL_BLEND);
                        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//                        initGL();
                        rect1.position(0);
                        simpleShader.draw(rect1, red);
                        rect2.position(0);
                        simpleShader.draw(rect2, green);

                        glSurfaceWindow.commit();
                    }
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException ignored) {
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "loop: ", e);
            } finally {
                if (null != simpleShader) simpleShader.close();
                if (null != glSurfaceWindow) glSurfaceWindow.close();
                if (null != glContext) glContext.close();
            }
            Log.d(TAG, "loop: end");
        }

        private void initGL() {
            // 启用混合模式
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLException.checkGLError("glBlendFunc");

            // 关闭裁剪
//            GLES20.glDisable(GLES20.GL_CULL_FACE);
//            GLException.checkGLError("glDisable/GL_CULL_FACE");
//            GLES20.glEnable(GLES20.GL_CCW);
//            GLException.checkGLError("glEnable/GL_CCW");

            // 启用深度测试
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLException.checkGLError("glEnable/GL_DEPTH_TEST");
        }

        public void start() {
            synchronized (lock) {
                if (state == 0) {
                    state = 1;
                    new Thread(this::loop).start();
                }
            }
        }

        public void destroy() {
            synchronized (lock) {
                if (state != 2) {
                    state = 2;
                }
            }
        }

        private FloatBuffer createRect(float[] data) {
            FloatBuffer buffer = ByteBuffer.allocateDirect(data.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            buffer.put(data);
            return buffer;
        }
    }
}
