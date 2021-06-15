package com.example.tfg_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class Upload_File extends AppCompatActivity {

    private Button SelectButton, UploadButton;
    private EditText PdfNameEditText ;
    private Uri uri;

    public static final String PDF_UPLOAD_HTTP_URL = "http://192.168.1.40//moodle/mod/exam/android/file_upload.php";

    public int PDF_REQ_CODE = 1;
    public String PdfNameHolder, PdfPathHolder, PdfID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        AllowRunTimePermission();

        Toast.makeText(Upload_File.this, "Archivo procesandose y subiendose a Caronte", Toast.LENGTH_LONG).show();

        File pdf = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/" + Foto.fileName);
        this.uri = Uri.fromFile(pdf);
        //this.uri = Uri.parse("content://com.mi.android.globalFileexplorer.myprovider/external_files/Download/exam.pdf");
        //getApplicationContext().grantUriPermission(getApplicationContext().getPackageName(), this.uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PdfUploadFunction();

        Intent upload_done = new Intent(this, Course.class);
        startActivity(upload_done);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PDF_REQ_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            this.uri = data.getData();
            System.out.println("\n" + "\n" + "Path de la URI actual: " + this.uri.getPath() + "\n" + "\n" + "URI del file " + this.uri + "\n");
            //String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
            SelectButton.setText("Has seleccionado un archivo");
        }
    }

    private void PdfUploadFunction() {

        //Generar automaticamente el nombre del archivo NIU+Nombre examen+Fecha+NombreAssignatura
        //PdfNameHolder = PdfNameEditText.getText().toString().trim();
        PdfNameHolder = Foto.fileName;

        PdfPathHolder = FilePath.getPath(this, this.uri);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        if (PdfPathHolder == null) {
            Toast.makeText(this, "Please move your PDF file to internal storage & try again.", Toast.LENGTH_LONG).show();
        } else {
            try {
                //PdfID = UUID.randomUUID().toString();
                new MultipartUploadRequest(this, PDF_UPLOAD_HTTP_URL)
                        .addFileToUpload(PdfPathHolder, "pdf")
                        .setMethod("POST")
                        .addParameter("name", PdfNameHolder)
                        .addParameter("userID", MainActivity.user.getString("id"))
                        .addParameter("fechaEntrega", timeStamp)
                        .addParameter("examID", Exam.id_exam)
                        //.setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(5)
                        .startUpload();

                Toast.makeText(Upload_File.this, "Archivo subido con éxito", Toast.LENGTH_LONG).show();
            } catch (Exception exception) {
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void AllowRunTimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Upload_File.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(Upload_File.this,"READ_EXTERNAL_STORAGE permission Access Dialog", Toast.LENGTH_LONG).show();
        } else{
            ActivityCompat.requestPermissions(Upload_File.this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, @NonNull String[] per, @NonNull int[] Result) {

        if (RC == 1) {
            if (Result.length > 0 && Result[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Upload_File.this, "Permiso otorgado", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Upload_File.this, "Permiso denegado", Toast.LENGTH_LONG).show();
            }
        }
    }
}