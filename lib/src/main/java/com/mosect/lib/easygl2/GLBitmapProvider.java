package com.mosect.lib.easygl2;

import android.graphics.Bitmap;

/**
 * opengl位图提供者
 */
public interface GLBitmapProvider {

    Bitmap getBitmap(Object host);

    void destroyBitmap(Object host, Bitmap bitmap);
}
