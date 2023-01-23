package com.mosect.lib.easygl2.g2d;

import java.nio.Buffer;

/**
 * 2D shader
 */
public interface GLShader2D {

    void draw(GLTexture2D texture2D, float[] cameraMatrix, Buffer vertices, Buffer textureCoords);
}
