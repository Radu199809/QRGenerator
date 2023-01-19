package com.example.qrgenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class scanQRActivity extends AppCompatActivity {

    //declare variables
    private SurfaceView qrScannerView;
    private TextView qrResultTextView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private androidx.appcompat.widget.AppCompatButton backBTN;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private String barcodeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qractivity);

        //initialize variables
        qrScannerView = findViewById(R.id.qr_scanner_view);
        qrResultTextView = findViewById(R.id.scanResult);
        backBTN = findViewById(R.id.backBtn);
        initializeDetectorsAndSources();

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void initializeDetectorsAndSources() {

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setAutoFocusEnabled(true)
                .build();

        qrScannerView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(scanQRActivity.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(qrScannerView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(scanQRActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    qrResultTextView.post(new Runnable() {
                        public void run() {
                            if (barcodes.valueAt(0).valueFormat == Barcode.QR_CODE) {
                                barcodeData = barcodes.valueAt(0).displayValue;
                                qrResultTextView.setText(barcodeData);
                            } else {
                                qrResultTextView.setText("Invalid QR code");
                            }
                        }
                    });
                }
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        getSupportActionBar().hide();
        cameraSource.release();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        getSupportActionBar().hide();
//        initialiseDetectorsAndSources();
//    }



}