package com.example.tfg_app;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.parceler.Parcels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Foto extends AppCompatActivity {
    private ImageView img = null;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String PdfNameHolder, PdfPathHolder, PdfID;
    private ArrayList<Bitmap> imgList = new ArrayList<>();
    private String currentPhotoPath;
    public static String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);
        img = (ImageView)findViewById(R.id.imageView);
        if (ContextCompat.checkSelfPermission(Foto.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Foto.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Foto.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Backup_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Método para tomar una fotografía y crear el archivo
    public void TakePhoto(View view) throws InterruptedException {
        //Nos aseguramos que existe una activity para procesar la activity
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //Creamos el archivo que se asociará a la imagen
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.print("Ocurrió un error al tomar una fotografía");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Parcels.wrap(photoURI.toString()));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                SystemClock.sleep(6000);
            }
        }
    }

    //Metodo para convertir bitmap a pdf
    private void createPDF(){
        String path = Environment.getExternalStorageDirectory().getPath() + "/Download";
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        try{
            fileName = MainActivity.user.getString("username")+ "_" + Exam.exam_name + "_" + timeStamp + "_" + Course.name_curso + ".pdf";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final File file = new File(path, fileName);
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Generating PDF...");
        dialog.show();
        new Thread(() -> {
            Bitmap bitmap;
            PdfDocument document = new PdfDocument();
            int height = 1010;
            int width = 714;
            int reqH, reqW;
            reqW = width;
            for (int i = 0; i < imgList.size(); i++) {
                bitmap = imgList.get(i);
                reqH = width * bitmap.getWidth() / bitmap.getHeight();
                if (reqH < height) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, reqW, reqH, true);
                } else {
                    reqH = height;
                    reqW = height * bitmap.getWidth() / bitmap.getHeight();
                    bitmap = Bitmap.createScaledBitmap(bitmap, reqW, reqH, true);
                }
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(reqW, reqH, 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);
                Canvas canvas = page.getCanvas();
                canvas.drawBitmap(bitmap, 0, 0, null);
                document.finishPage(page);
            }
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                document.writeTo(fos);
                document.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //Método para mostrar la vista previa de la foto tomada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgList.add(imageBitmap);
            Toast.makeText(Foto.this, String.valueOf(imgList.size()), Toast.LENGTH_LONG).show();
            img.setImageBitmap(imageBitmap);
        }
    }
    public void Select_pdf(View view){
        createPDF();

        Intent Select_pdf = new Intent(this, Upload_File.class);
        startActivity(Select_pdf);

    }
}