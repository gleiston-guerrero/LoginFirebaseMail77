package com.example.loginfirebasemail77;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.loginfirebasemail77.modelos.reconocimientoFire;
import com.example.loginfirebasemail77.modelos.ubicaciones;
import com.example.loginfirebasemail77.reproducir.TTSManager;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    reconocimientoFire selectReconocimiento;


    private Button btnSpeak;
    private EditText editText;
    TTSManager ttsManager=null;
    String text="No hay";
    String mac="";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Translator translator;
    reconocimientoFire r= new reconocimientoFire();
    Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_reconocimiento);
        ivPicture=findViewById(R.id.ivPicture2);
        bntChoosePicture=findViewById(R.id.idGaleria);
        listaView = findViewById(R.id.listaPaciente);
        btnOpenCamara=findViewById(R.id.btnOpenCamara3);

        mac=getIntent().getExtras().getString("mac");

        aSwitch=findViewById(R.id.switch3);
        labeler= ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        ttsManager = new TTSManager();
        ttsManager.init(this);

        inicializarFirebase();
        esp32cam();

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

        listaView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectReconocimiento= (reconocimientoFire) parent.getItemAtPosition(position);
                text=selectReconocimiento.getReconocimiento().toString();
                if(aSwitch.isChecked())
                {
                 prepareModel(text);
                }else
                {
                 ttsManager.initQueue(text);
                }

                return false;

            }
        });


        btnOpenCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camaraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(camaraIntent);
                Toast.makeText(registroReconocimiento.this, "No hay",Toast.LENGTH_SHORT).show();
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

    }
    public String nombreAleatodio()
    {
        int p=(int) (Math.random()*25+1); int s=(int) (Math.random()*25+1);
        int t=(int) (Math.random()*25+1);int c=(int) (Math.random()*25+1);
        int numero1=(int) (Math.random()*1012+2111);
        int numero2=(int) (Math.random()*1012+2111);

        String[] elementos={"a","b","c","d","e","f","g","h","i","j","k","l",
                "m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
        final String aleatorio=elementos[p]+elementos[s]+numero1+elementos[t]+elementos[c]+numero2+"comprimido.jpg";
        return  aleatorio;
    }
    private void esp32cam() {
        databaseReference.child("esp32cam").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot objShaptshot : snapshot.getChildren())
                {
                    String baseRe=objShaptshot.child("photo").getValue().toString().split(",")[1];
                    String base=baseRe.replace("%2F","/").replace("%2B","+");
                    System.out.println(baseRe.replace("%2F","/"));
                    byte[] decodedString = Base64.decode(base, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    inputImage=InputImage.fromBitmap(decodedByte,0);
                    ivPicture.setImageBitmap(decodedByte);
                    processImage();

                    StorageReference reference=storageReference.child(nombreAleatodio());
                    UploadTask uploadTask = reference.putBytes(decodedString);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });

                    Task<Uri> uriTask= uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then( Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw Objects.requireNonNull(task.getException());
                            }
                            return reference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(Task<Uri> task) {
                            Uri dUri=task.getResult();
                            guardarreconocimiento(dUri.toString());
                            System.out.println("Uri "+dUri.toString());
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("elementos: "+error.getMessage());
            }
        });
    }
    public void guardarreconocimiento(String url)
    {
        r.setIdReconocimiento(UUID.randomUUID().toString());
        r.setConfianza(StringConfianza());
        r.setReconocimiento(Stringreconocimiento());
        r.setMacDispositivo(mac);
        r.setUrl(url);

        databaseReference.child("reconocimiento").child(r.getIdReconocimiento()).setValue(r);
        ttsManager.initQueue("Se guardó el reconocimiento");


    }
    public String Stringreconocimiento()
    {

        String html="";
        for (int i=0; i<listReconocimiento.size(); i++) {

            html=listReconocimiento.get(i).getReconocimiento().toString()+";"+html;
        }
        return html;
    }
    public String StringConfianza()
    {
        String html="";
        for (int i=0; i<listReconocimiento.size(); i++) {

            html=listReconocimiento.get(i).getConfianza().toString()+";"+html;
        }
        return html;
    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        storageReference= FirebaseStorage.getInstance().getReference().child("Esp32");
    }

    private void traslaterlenguaje(String text) {

        translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>(){
            @Override
            public  void onSuccess(String s)
            {
                ttsManager.initQueue(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                ttsManager.initQueue("Fallo al traducir");
            }
        });

    }

    private void prepareModel(String text) {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.fromLanguageTag("en"))
                        .setTargetLanguage(TranslateLanguage.fromLanguageTag("es"))
                        .build();
        translator = Translation.getClient(options);
        translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                traslaterlenguaje(text);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttsManager.shutDown();
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

                        }
                        arrayAdapterreconocimientoFire = new ArrayAdapter<reconocimientoFire>(registroReconocimiento.this, android.R.layout.simple_list_item_1, listReconocimiento);
                        listaView.setAdapter(arrayAdapterreconocimientoFire);

                        Reprodicir();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG,"onFailure"+e.getMessage());
            }
        });

    }
    public void Reprodicir()
    {
        if(aSwitch.isChecked())
        {

        try {
         prepareModel(listReconocimiento.get(1).getReconocimiento().toString());
         Thread.sleep( 4000);
         } catch (Exception e) {
         System.out.println(e);
                }
            try {
                prepareModel(listReconocimiento.get(2).getReconocimiento().toString());
                Thread.sleep( 4000);
            } catch (Exception e) {
                System.out.println(e);
            }

        }else
        {
            for (int i=0; i<listReconocimiento.size(); i++) {
                try {
                    ttsManager.initQueue(listReconocimiento.get(i).getReconocimiento().toString());
                    Thread.sleep( 1000);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

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