package com.example.loginfirebasemail77;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginfirebasemail77.modelos.reconocimientoFire;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class registroReconocimiento extends AppCompatActivity {

    ImageView ivPicture;
    TextView txtResult;
    Button bntChoosePicture,btnOpenCamara;
    private static final String TAG = "MiTag";
    private static  final int STORAGE_PERMISSION_CODE=113;
    private static  final int CAMERA_PERMISSION_CODE=223;
    private static  final int READ_STORAGE_PERMISSION_CODE=144;
    private static  final int WRITE_STORAGE_PERMISSION_CODE=144;
    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> galleryLauncher;


    List<reconocimientoFire> listReconocimiento;
    ListView listaView;
    ArrayAdapter<reconocimientoFire> arrayAdapterreconocimientoFire;
    InputImage inputImage;
    ImageLabeler labeler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_reconocimiento);
        ivPicture=findViewById(R.id.ivPicture2);
        bntChoosePicture=findViewById(R.id.idGaleria);
        listaView = findViewById(R.id.listaPaciente);
        labeler= ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);



        bntChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(registroReconocimiento.this, "Buscar img",Toast.LENGTH_SHORT).show();
                Intent storIntent= new Intent();
                storIntent.setType("image/*");
                storIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryLauncher.launch(storIntent);
            }
        });
        galleryLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        Intent data=result.getData();
                        try{
                            inputImage=InputImage.fromFilePath(registroReconocimiento.this, data.getData());
                            ivPicture.setImageURI(data.getData());
                            processImage();
                        }catch (Exception e)
                        {
                            Log.d(TAG, "onActivityResult: "+e.getMessage());
                        }
                    }
                }
        );

    }

    public void abriGaleria(View view)
    {
        Toast.makeText(registroReconocimiento.this, "Buscar img",Toast.LENGTH_SHORT).show();
        Intent storIntent= new Intent();
        storIntent.setType("image/*");
        storIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryLauncher.launch(storIntent);

    }
    private void processImage() {
        labeler.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(@NonNull @NotNull List<ImageLabel> imageLabels) {
                        String result="";
                        listReconocimiento=new ArrayList<>();
                        listReconocimiento.clear();
                        for (ImageLabel label: imageLabels)
                        {
                            listReconocimiento.add(new reconocimientoFire((label.getConfidence()+""),label.getText()));
                            // txtResult.setText(result);
                        }
                        arrayAdapterreconocimientoFire = new ArrayAdapter<reconocimientoFire>(registroReconocimiento.this, android.R.layout.simple_list_item_1, listReconocimiento);
                        listaView.setAdapter(arrayAdapterreconocimientoFire);

                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG,"onFailure"+e.getMessage());
            }
        });
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL("https://res.cloudinary.com/durxpegdm/image/upload/v1627940101/3d-flame-279_xt18fx.png");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            Log.d(TAG,"error"+e.getMessage());
            return null;
        }
    }


    @Override
    public  void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults )
    {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if(requestCode== CAMERA_PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            { Toast.makeText(registroReconocimiento.this,"Acepto los permisos", Toast.LENGTH_SHORT).show();
            }else
            { Toast.makeText(registroReconocimiento.this,"Denego los permisos", Toast.LENGTH_SHORT).show();}
        }
        else if(requestCode==STORAGE_PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(registroReconocimiento.this,"Acepto los permisos", Toast.LENGTH_SHORT).show();}
            else
            {Toast.makeText(registroReconocimiento.this,"Permisos denegados", Toast.LENGTH_SHORT).show();}
        }
        else  if(requestCode==READ_STORAGE_PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(registroReconocimiento.this,"Acepto los permisos", Toast.LENGTH_SHORT).show();}
            else
            {Toast.makeText(registroReconocimiento.this,"Permisos denegados", Toast.LENGTH_SHORT).show();}
        }
        else  if(requestCode==WRITE_STORAGE_PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(registroReconocimiento.this,"Acepto los permisos", Toast.LENGTH_SHORT).show();}
            else
            {Toast.makeText(registroReconocimiento.this,"Permisos denegados", Toast.LENGTH_SHORT).show();}
        }
    }

}