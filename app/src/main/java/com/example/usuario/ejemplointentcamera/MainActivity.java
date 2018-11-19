package com.example.usuario.ejemplointentcamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    /*Mirar :
    -https://developer.android.com/guide/components/intents-common?hl=es-419
    -https://developer.android.com/training/permissions/requesting?hl=es-419
    */

    static final int VENGO_DE_LA_CAMARA = 1;
    static final int VENGO_DE_LA_CAMARA_CON_FICHERO = 2;
    static final int PEDI_PERMISOS_DE_ESCRITURA = 2;
Button captura2;
VideoView video;
String rutaFotoActual;
MediaController control;
//rutas del video
Intent haceFoto;
String ruta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        captura2 = findViewById(R.id.buttonCaptura2);
        video = findViewById(R.id.musica);
        control = new MediaController(this);

        captura2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pedirPermisoParaEscribirYHacerFoto();


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if ((requestCode == VENGO_DE_LA_CAMARA) && (resultCode == RESULT_OK)){
            // no ocurre nada si cancelo el video

        }else if ((requestCode == VENGO_DE_LA_CAMARA_CON_FICHERO) && (resultCode == RESULT_OK)){
            //He hecho la foto y la he guardado, así que la foto estará en  rutaFotoActual
            video.setMediaController(control);
            Uri myuri = Uri.parse(ruta);
            video.requestFocus();
            video.setVideoURI(myuri);
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    control.show();
                }
            });
        }

        }
    public void capturarFoto( ) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, false);
        File ficheroFoto = null;
        try {
            ficheroFoto = crearFicheroImagen();
            ruta = ficheroFoto.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(ficheroFoto));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, VENGO_DE_LA_CAMARA_CON_FICHERO);
        }else{
            Toast.makeText(this, "No tengo programa o cámara", Toast.LENGTH_SHORT).show();
        }
    }
    File crearFicheroImagen() throws IOException {
        String fechaYHora = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreFichero = "Ejemplo_"+fechaYHora;
        File carpetaParaFotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        File imagen = File.createTempFile(nombreFichero, ".mp4", carpetaParaFotos);
        rutaFotoActual = imagen.getAbsolutePath();
        return imagen;
    }


    void pedirPermisoParaEscribirYHacerFoto(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Aquí puedo explicar para qué quiero el permiso

            } else {

                // No explicamos nada y pedimos el permiso

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PEDI_PERMISOS_DE_ESCRITURA);

                // El resultado de la petición se recupera en onRequestPermissionsResult
            }
        }else{//Tengo los permisos
            capturarFoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PEDI_PERMISOS_DE_ESCRITURA: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Tengo los permisos: hago la foto:

                    this.capturarFoto();

                } else {

                    //No tengo permisos: Le digo que no se puede hacer nada
                    Toast.makeText(this, "Sin permisos de escritura no puedo guardar la imagen en alta resolución.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

           //Pondría aquí más "case" si tuviera que pedir más permisos.
        }
    }
}
