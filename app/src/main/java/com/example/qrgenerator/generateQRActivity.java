package com.example.qrgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;



import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Objects;


public class generateQRActivity extends AppCompatActivity {
    //declare variables for elements in activity_generate_qractivity
    private TextView qrTV;
    private ImageView qrIV;
    private TextInputEditText dataET;
    private androidx.appcompat.widget.AppCompatButton generateQRBtn;
    private BitMatrix bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qractivity);

        //initialize variables
        qrTV = findViewById(R.id.QRTextView);
        qrIV = findViewById(R.id.QRImgView);
        dataET = findViewById(R.id.InputData);
        generateQRBtn = findViewById(R.id.generateBtn);
        androidx.appcompat.widget.AppCompatButton backBTN = findViewById(R.id.backBtn);


        generateQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = Objects.requireNonNull(dataET.getText()).toString();
                MultiFormatWriter mWriter = new MultiFormatWriter();
                if(input.isEmpty()){
                    Toast.makeText(generateQRActivity.this, "Please enter text to generate QR", Toast.LENGTH_SHORT).show();
                }else{

                    try {
                        bitmap = mWriter.encode(input, BarcodeFormat.QR_CODE, 250, 250);
                        BarcodeEncoder mEncoder = new BarcodeEncoder();
                        Bitmap mBitmap = mEncoder.createBitmap(bitmap);//creating bitmap of code
                        qrIV.setScaleType(ImageView.ScaleType.FIT_XY);
                        qrIV.setImageBitmap(mBitmap);//Setting generated QR code to imageView
                        qrTV.setVisibility(View.INVISIBLE);

                        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(dataET.getApplicationWindowToken(), 0);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }


                }

            }
        });

        qrIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Save bitmap to MediaStore
                 */

                //get bitmap from ImageVIew
                //not always valid, depends on your drawable

                Bitmap bitmap = ((BitmapDrawable)qrIV.getDrawable()).getBitmap();
                ContentResolver cr = getContentResolver();
                String description = "My bitmap created by QR Generator";
                String savedURL = MediaStore.Images.Media.insertImage(cr, bitmap, dataET.getText().toString(), description);

                Toast.makeText(generateQRActivity.this, "Saved to gallery ", Toast.LENGTH_LONG).show();
            }
        });

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(generateQRActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });
    }
}