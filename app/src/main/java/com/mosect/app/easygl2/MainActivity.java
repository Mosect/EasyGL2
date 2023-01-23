package com.mosect.app.easygl2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private Runnable cameraRunnable = null;
    private ActivityResultLauncher<String> cameraLauncher;
    private ActivityResultLauncher<Intent> pictureLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                cameraRunnable.run();
            } else {
                Toast.makeText(this, "获取摄像头权限失败，请授予权限后重试", Toast.LENGTH_SHORT).show();
            }
            cameraRunnable = null;
        });
        pictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && null != result.getData()) {
                Uri uri = result.getData().getData();
                Intent intent = new Intent(this, PictureTestActivity.class);
                intent.putExtra("picture", uri.toString());
                startActivity(intent);
            }
        });

        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_frontCamera).setOnClickListener(v -> jumpCameraTest("front"));
        findViewById(R.id.btn_backCamera).setOnClickListener(v -> jumpCameraTest("back"));
        findViewById(R.id.btn_picture).setOnClickListener(v -> jumpPictureTest());
    }

    private void jumpCameraTest(String facing) {
        Runnable runnable = () -> {
            Intent intent = new Intent(this, CameraTestActivity.class);
            intent.putExtra("facing", facing);
            startActivity(intent);
        };
        int status = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (status == PackageManager.PERMISSION_GRANTED) {
            runnable.run();
        } else {
            cameraRunnable = runnable;
            cameraLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void jumpPictureTest() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pictureLauncher.launch(intent);
    }
}
