package com.mosect.lib.easygl2.util;

import android.content.Context;

import com.mosect.lib.easygl2.GLException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class GLUtils {

    private GLUtils() {
    }

    public static String loadAssetsText(Context context, String name) throws GLException {
        try (InputStream ins = context.getAssets().open(name);
             ByteArrayOutputStream temp = new ByteArrayOutputStream()) {
            int len;
            byte[] buffer = new byte[512];
            while ((len = ins.read(buffer)) >= 0) {
                if (len > 0) temp.write(buffer, 0, len);
            }
            return temp.toString("utf-8");
        } catch (IOException e) {
            throw new GLException("Load assets text failed, path: " + name, e, "GL_ASSETS_ERROR");
        }
    }
}
