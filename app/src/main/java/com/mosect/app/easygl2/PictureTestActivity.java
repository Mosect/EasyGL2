package com.mosect.app.easygl2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.mosect.lib.easygl2.GLShader;
import com.mosect.lib.easygl2.g2d.GLBitmapProvider;
import com.mosect.lib.easygl2.g2d.GLShaderRect;
import com.mosect.lib.easygl2.g2d.GLTextureBitmap;

import java.io.InputStream;

public class PictureTestActivity extends TestActivity {

    private static final String TAG = "App/PictureTest";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String picture = getIntent().getStringExtra("picture");
        if (TextUtils.isEmpty(picture)) {
            Toast.makeText(this, "未选择图片", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        GLTextureBitmap textureBitmap = new GLTextureBitmap(new GLBitmapProvider() {

            @Override
            public Bitmap getBitmap(Object host) {
                Uri uri = Uri.parse(picture);
                try (InputStream ins = getContentResolver().openInputStream(uri)) {
                    Bitmap bitmap = BitmapFactory.decodeStream(ins);
                    Log.d(TAG, "getBitmap: " + bitmap);
                    return bitmap;
                } catch (Exception e) {
                    Log.e(TAG, "getBitmap: ", e);
                }
                return null;
            }

            @Override
            public void destroyBitmap(Object host, Bitmap bitmap) {
                if (null != bitmap) bitmap.recycle();
            }
        });
        GLShader shader = new GLShaderRect();
        getGLThread().setContent(textureBitmap, shader);
    }
}
