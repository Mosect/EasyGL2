package com.mosect.app.easygl2;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mosect.lib.easygl2.GLSurface;
import com.mosect.lib.easygl2.GLSurfaceWindow;
import com.mosect.lib.easygl2.GLTexture;

public class TestActivity extends AppCompatActivity implements SurfaceHolder.Callback, GLThread.Callback {

    private GLThread glThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glThread = new GLThread();
        glThread.setCallback(this);
        glThread.start();

        setContentView(R.layout.activity_test);

        SurfaceView svContent = findViewById(R.id.sv_content);
        svContent.getHolder().addCallback(this);

        String[] scaleTypes = getResources().getStringArray(R.array.scaleType_ids);
        Spinner spScaleType = findViewById(R.id.sp_scaleType);
        spScaleType.setSelection(0);
        spScaleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                glThread.setScaleType(scaleTypes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        glThread.setScaleType(scaleTypes[0]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        glThread.release();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        glThread.setSurface(new GLSurfaceWindow(holder.getSurface(), width, height));
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        glThread.setSurface(null);
    }

    public GLThread getGLThread() {
        return glThread;
    }

    @Override
    public void onGLTextureInitialed(GLTexture texture) {

    }

    @Override
    public void onGLSurfaceCommitted(GLSurface surface) {

    }

    @Override
    public void onGLError(Exception exp) {

    }
}
