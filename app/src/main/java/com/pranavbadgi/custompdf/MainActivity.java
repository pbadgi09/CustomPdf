package com.pranavbadgi.custompdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.pranavbadgi.custompdf.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Activity activity = this;

    private static final int STORAGE_CODES = 1000;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    String doctor_name, clinic_name, reg_number,qualification, mobile_number, patient_name, patient_gender, patient_age,
    medicine_name,medicine_quantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        initialize();
        listener();



    }

    private void initialize() {
    }

    private void listener() {

        binding.generatePdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //file object for sharing with whatsapp\
                File pdfInfo = null;


                //runtime permission for marshmellow and above
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        //permission wasn't granted request it.
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(PERMISSIONS_STORAGE, STORAGE_CODES);

                    } else {
                        //permission already granted, call savepdf()
                        verifyStoragePermissions(activity);
                        pdfInfo = savePdf();
                    }
                } else {
                    //system os is not marshmellow and not req to check permission
                    pdfInfo = savePdf();
                }

                if(pdfInfo != null){

                    String toNumber = "91"+mobile_number; // contains spaces.
                    toNumber = toNumber.replace("+", "").replace(" ", "");

                    Intent sendIntent = new Intent("android.intent.action.MAIN");

                    //sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdfInfo));
                    sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.whatsapp");
                    sendIntent.setType("application/pdf");

                    //sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey here is your pdf");
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    activity.startActivity(sendIntent);
                }
            }
        });


        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.nextButton.setVisibility(View.GONE);

                binding.doctorNameET.setVisibility(View.GONE);
                binding.clinicNameET.setVisibility(View.GONE);
                binding.regNumberET.setVisibility(View.GONE);
                binding.qualificationET.setVisibility(View.GONE);
                binding.mobileNumberET.setVisibility(View.GONE);

                binding.generatePdfButton.setVisibility(View.VISIBLE);
                binding.patientNameET.setVisibility(View.VISIBLE);
                binding.patientGenderET.setVisibility(View.VISIBLE);
                binding.patientAgeET.setVisibility(View.VISIBLE);
                binding.medicineNameET.setVisibility(View.VISIBLE);
                binding.medicineQuantityET.setVisibility(View.VISIBLE);



            }
        });



    }



    private File savePdf() {
        doctor_name = binding.doctorNameET.getText().toString();
        clinic_name = binding.clinicNameET.getText().toString();
        reg_number = binding.regNumberET.getText().toString();
        qualification = binding.qualificationET.getText().toString();
        mobile_number = binding.mobileNumberET.getText().toString();
        patient_name = binding.patientNameET.getText().toString();
        patient_gender = binding.patientGenderET.getText().toString();
        patient_age = binding.patientAgeET.getText().toString();
        medicine_name = binding.medicineNameET.getText().toString();
        medicine_quantity = binding.medicineQuantityET.getText().toString();




        PdfDocument myPdfDocument = new PdfDocument();
        Paint myPaint = new Paint();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(300,700,1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();

        //create custom pdf
        myPaint.setTextAlign(Paint.Align.CENTER);
        myPaint.setTextSize(12f);
        canvas.drawText(doctor_name,myPageInfo.getPageWidth()/2,30,myPaint);

        myPaint.setTextSize(7f);
        canvas.drawText(qualification+","+mobile_number+","+reg_number,myPageInfo.getPageWidth()/2,50,myPaint);

        canvas.drawLine(10,55,290,55,myPaint);


        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(9f);
        canvas.drawText("\nPatient: "+patient_name,10,70,myPaint);

        myPaint.setTextAlign(Paint.Align.CENTER);
        myPaint.setTextSize(7f);
        canvas.drawText(patient_name+" \nAge: "+patient_age+"\n Gender: "+patient_gender,myPageInfo.getPageWidth()/2,100,myPaint);

        canvas.drawLine(10,110,290,110,myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(12f);
        canvas.drawText("\nMedicines",40,130,myPaint);

        myPaint.setTextAlign(Paint.Align.CENTER);
        myPaint.setTextSize(9f);
        canvas.drawText("\nMedicines Name:"+medicine_name,myPageInfo.getPageWidth()/2,150,myPaint);

        myPaint.setTextAlign(Paint.Align.CENTER);
        myPaint.setTextSize(9f);
        canvas.drawText("\nMedicine Quantity: "+medicine_quantity,myPageInfo.getPageWidth()/2,170,myPaint);

        canvas.drawLine(10,200,290,200,myPaint);

        myPaint.setTextAlign(Paint.Align.CENTER);
        myPaint.setTextSize(5f);
        canvas.drawText("Generated by Clinic97",myPageInfo.getPageWidth()/2,210,myPaint);

        myPdfDocument.finishPage(myPage);



        File file = new File(Environment.getExternalStorageDirectory(),"/"+patient_name+".pdf");
        try{
            verifyStoragePermissions(activity);
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        myPdfDocument.close();


        return file;

    }






    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_CODES: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted from pop up
                    //savePdf();
                    Toast.makeText(this, "Permission Granted.", Toast.LENGTH_SHORT).show();
                }
                else {
                    //permission denied
                    Toast.makeText(this, "Permission Denied, Cannot save Info.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    STORAGE_CODES
            );
        }
    }
//end of onCreate()

}
