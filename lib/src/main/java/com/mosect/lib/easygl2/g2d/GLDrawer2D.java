package com.mosect.lib.easygl2.g2d;

import android.graphics.Matrix;
import android.graphics.RectF;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * 2D绘制器
 */
public class GLDrawer2D {

    private final static float NEAR = 0.5f;
    private final static float FAR = 1.5f;

    private final GLTexture2D texture2D;
    private final GLShader2D shader2D;

    private final RectF cameraRect = new RectF(0, 0, 1, 1);
    private final RectF textureRect = new RectF();
    private float z = 0f;
    private Matrix matrix = null;
    private boolean changed = true;

    private final float[] cameraMatrix = new float[16];
    private final FloatBuffer vertices;
    private final FloatBuffer textureCoords;

    public GLDrawer2D(GLTexture2D texture2D, GLShader2D shader2D) {
        this.texture2D = texture2D;
        this.shader2D = shader2D;

        textureRect.set(0, 0, texture2D.getWidth(), texture2D.getHeight());
        vertices = ByteBuffer.allocateDirect(4 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoords = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoords.put(new float[]{
                0.f, 1.f,
                1.f, 1.f,
                0.f, 0.f,
                1.f, 0.f,
        });
    }

    public void draw() {
        if (changed) {
            android.opengl.Matrix.orthoM(cameraMatrix, 0, cameraRect.left, cameraRect.right,
                    cameraRect.top, cameraRect.bottom, NEAR, FAR);
            float zLen = FAR - NEAR;
            float sz = -NEAR - z * zLen;
            float[] points = {
                    textureRect.left, textureRect.top,
                    textureRect.right, textureRect.top,
                    textureRect.left, textureRect.bottom,
                    textureRect.right, textureRect.bottom
            };
            if (null != matrix) {
                matrix.mapPoints(points);
            }
            vertices.position(0);
            for (int i = 0; i < points.length; i += 2) {
                vertices.put(points[i]);
                vertices.put(points[i + 1]);
                vertices.put(sz);
            }

            testCameraMatrix();

            changed = false;
        }
        vertices.position(0);
        textureCoords.position(0);
        shader2D.draw(texture2D, cameraMatrix, vertices, textureCoords);
    }

    public void setCameraRect(float left, float top, float right, float bottom) {
        if (right < left) {
            throw new IllegalArgumentException("right < left");
        }
        if (bottom < top) {
            throw new IllegalArgumentException("bottom < top");
        }
        cameraRect.set(left, top, right, bottom);
        changed = true;
    }

    public void setCameraRect(int cameraWidth, int cameraHeight) {
        if (cameraWidth < 0)
            throw new IllegalArgumentException("Invalid cameraWidth: " + cameraWidth);
        if (cameraHeight < 0)
            throw new IllegalArgumentException("Invalid cameraHeight: " + cameraHeight);

        setCameraRect(0, 0, cameraWidth, cameraHeight);
    }

    public RectF getCameraRect(RectF out) {
        if (null == out) out = new RectF();
        out.set(cameraRect);
        return out;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        if (z < 0 || z > 1) {
            throw new IllegalArgumentException("Invalid z: " + z);
        }
        this.z = z;
        changed = true;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
        changed = true;
    }

    private void testCameraMatrix() {
        vertices.position(0);
        float[] point = new float[4];
        float[] result = new float[4];
        for (int i = 0; i < 4; i++) {
            point[0] = vertices.get();
            point[1] = vertices.get();
            point[2] = vertices.get();
            point[3] = 1f;
            android.opengl.Matrix.multiplyMV(result, 0, cameraMatrix, 0, point, 0);
            System.out.printf("%s >>> %s%n", Arrays.toString(point), Arrays.toString(result));
        }
    }
}
