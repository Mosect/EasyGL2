package com.mosect.app.easygl2;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.mosect.lib.easygl2.GLShader;
import com.mosect.lib.easygl2.GLTexture;
import com.mosect.lib.easygl2.g2d.GLShaderOES;
import com.mosect.lib.easygl2.GLTextureWindow;

import java.util.Collections;
import java.util.List;

public class CameraTestActivity extends TestActivity {

    private static final String TAG = "App/CameraTest";

    private Camera camera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 打开相机
        String facing = getIntent().getStringExtra("facing");
        int facingValue;
        if ("front".equals(facing)) {
            facingValue = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            facingValue = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraId = -1;
        int count = Camera.getNumberOfCameras();
        for (int i = 0; i < count; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facingValue) {
                cameraId = i;
                break;
            }
        }
        if (cameraId < 0) {
            Toast.makeText(this, "找不到相应摄像头设备", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            camera = Camera.open(cameraId);
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);
            List<int[]> fpsRangeList = parameters.getSupportedPreviewFpsRange();
            Collections.sort(fpsRangeList, (o1, o2) -> o2[1] - o1[1]);
            int[] fpsRange = fpsRangeList.get(0);
            parameters.setPreviewFpsRange(fpsRange[0], fpsRange[1]);
            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            Collections.sort(previewSizes, (o1, o2) -> {
                int v1 = Math.abs(o1.height - 1080);
                int v2 = Math.abs(o2.height - 1080);
                return v1 - v2;
            });
            Camera.Size size = previewSizes.get(0);
            parameters.setPreviewSize(size.width, size.height);

            GLShader shader = new GLShaderOES();
            GLTextureWindow texture = new GLTextureWindow(size.width, size.height, false);
            getGLThread().setContent(texture, shader);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
            Toast.makeText(this, "打开相机失败", Toast.LENGTH_SHORT).show();
            if (null != camera) {
                camera.release();
                camera = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != camera) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onGLTextureInitialed(GLTexture texture) {
        super.onGLTextureInitialed(texture);
        runOnUiThread(() -> {
            try {
                if (null != camera) {
                    GLTextureWindow textureWindow = (GLTextureWindow) texture;
                    camera.setPreviewTexture(textureWindow.getSurfaceTexture());
                    camera.startPreview();
                }
            } catch (Exception e) {
                Log.e(TAG, "onGLTextureInitialed: ", e);
            }
        });
    }
}
