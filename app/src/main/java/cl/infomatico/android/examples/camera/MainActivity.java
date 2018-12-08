package cl.infomatico.android.examples.camera;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    // AppCompatActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkCameraHardware(getBaseContext()))
            cameraPermissionsCheck();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null)
            mCamera.stopPreview();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[]
            , @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraOpen();
                }
            }
        }
    }

    // MainActivity

    private static final String TAG = MainActivity.class.getSimpleName();

    // Camera

    private static final String ERROR_STARTING_CAMERA = "Error starting camera: ";
    private Camera mCamera;

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            Log.d(TAG, ERROR_STARTING_CAMERA);
        }
        return c;
    }

    // Permissions

    private static final int PERMISSIONS_CAMERA = 1;

    private void cameraPermissionsCheck() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage(R.string.permission_camera);
                builder.setTitle(R.string.permission_required);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cameraPermissionsMake();
                    }
                });

                builder.create().show();
            } else {
                cameraPermissionsMake();
            }
        } else {
            cameraOpen();
        }
    }

    private void cameraPermissionsMake() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSIONS_CAMERA);
    }

    private void cameraOpen() {
        mCamera = getCameraInstance();

        // Preview

        CameraPreview mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }
}