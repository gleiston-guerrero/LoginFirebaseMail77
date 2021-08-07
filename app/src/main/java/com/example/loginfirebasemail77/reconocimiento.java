package com.example.loginfirebasemail77;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.annotations.NotNull;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.List;

public class reconocimiento extends AppCompatActivity {
    ImageView  ivPicture;
    TextView txtResult;
    Button bntChoosePicture;
    private static final String TAG = "MiTag";
    private static  final int STORAGE_PERMISSION_CODE=113;
    private static  final int CAMERA_PERMISSION_CODE=223;
    private static  final int READ_STORAGE_PERMISSION_CODE=144;
    private static  final int WRITE_STORAGE_PERMISSION_CODE=144;
    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> galleryLauncher;

    InputImage inputImage;
    ImageLabeler labeler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reconocimiento);
       //final MaterialTextView textView = (MaterialTextView) findViewById(R.id.text_view_id);
        //textView.setText("sdsadsadadasdas");
        ivPicture=findViewById(R.id.ivPicture);
        txtResult=findViewById(R.id.txtResult);
        bntChoosePicture=findViewById(R.id.bntChoosePicture);
        labeler= ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);


        bntChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(reconocimiento.this, "Buscar img",Toast.LENGTH_SHORT).show();
                Intent storIntent= new Intent();
                storIntent.setType("image/*");
                storIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryLauncher.launch(storIntent);

           /*  String [] options={"camera", "gallery"};
                AlertDialog.Builder builder= new AlertDialog.Builder( reconocimiento.this);
                builder.setTitle("Pick a option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0)
                        {
                        Intent camaraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(camaraIntent);
                            Toast.makeText(reconocimiento.this, "No hay",Toast.LENGTH_SHORT).show();
                        }else
                        {
                            Intent storIntent= new Intent();
                            storIntent.setType("image/*");
                            storIntent.setAction(Intent.ACTION_GET_CONTENT);
                            galleryLauncher.launch(storIntent);
                        }
                    }
                });*/
            }
        });
        cameraLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                            Intent data =result.getData();
                            try
                            {
                                Bitmap photo=(Bitmap) data.getExtras().get("data");
                                ivPicture.setImageBitmap(photo);
                                inputImage=InputImage.fromBitmap(photo,0);
                                processImage();
                            }catch (Exception e)
                            {
                                Log.d(TAG, "onActivityResult: "+e.getMessage());
                            }
                    }
                }
        );

        galleryLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent data=result.getData();
                        try{

                            inputImage=InputImage.fromFilePath(reconocimiento.this, data.getData());
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

    private void processImage() {
        labeler.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(@NonNull @NotNull List<ImageLabel> imageLabels) {
                        String result="";
                        for (ImageLabel label: imageLabels)
                        {
                            result=result+"\n"+label.getText();
                            /*for (ImageLabel label : labels) {
                                String text = label.getText();
                                float confidence = label.getConfidence();
                                int index = label.getIndex();
                            }*/
                            txtResult.setText(result);
                        }

                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG,"onFailure"+e.getMessage());
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
    }
    public void checkPermission(String permission, int requestCode)
    {
        if(ContextCompat.checkSelfPermission(reconocimiento.this, permission)== PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(reconocimiento.this,new String[]{permission},requestCode);
        }
    }
    @Override
    public  void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults )
    {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if(requestCode== CAMERA_PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            { Toast.makeText(reconocimiento.this,"Acepto los permisos", Toast.LENGTH_SHORT).show();
            }else
            { Toast.makeText(reconocimiento.this,"Denego los permisos", Toast.LENGTH_SHORT).show();}
        }
        else if(requestCode==STORAGE_PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(reconocimiento.this,"Acepto los permisos", Toast.LENGTH_SHORT).show();}
            else
            {Toast.makeText(reconocimiento.this,"Permisos denegados", Toast.LENGTH_SHORT).show();}
        }
        else  if(requestCode==READ_STORAGE_PERMISSION_CODE)
    {
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(reconocimiento.this,"Acepto los permisos", Toast.LENGTH_SHORT).show();}
        else
        {Toast.makeText(reconocimiento.this,"Permisos denegados", Toast.LENGTH_SHORT).show();}
    }
        else  if(requestCode==WRITE_STORAGE_PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(reconocimiento.this,"Acepto los permisos", Toast.LENGTH_SHORT).show();}
            else
            {Toast.makeText(reconocimiento.this,"Permisos denegados", Toast.LENGTH_SHORT).show();}
        }
    }
    //--------------------------------------------Parte de los permisos---------------------------------------------------//

}