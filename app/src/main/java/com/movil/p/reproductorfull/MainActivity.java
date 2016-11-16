package com.movil.p.reproductorfull;


import android.os.Handler;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //ID de la cancion inicial
    private static int cursor = 0;
    int [] caratulas = {R.drawable.skrillex, R.drawable.guns, R.drawable.linkin};
    //numero de canciones en la lista
    private int num = 3;
    //Acciones
    private static final String ACTION_PLAY = "com.movil.p.reproductorfull.action.PLAY";
    private static final String ACTION_PAUSE = "com.movil.p.reproductorfull.action.PAUSE";
    private static final String ACTION_STOP = "com.movil.p.reproductorfull.action.STOP";
    private static final String ACTION_NEXT = "com.movil.p.reproductorfull.action.NEXT";
    private static final String ACTION_BACK = "com.movil.p.reproductorfull.action.BACK";
    //Botones
    ImageButton btnPause, btnStart, btnStop, btnnext, btnBack, btnrepeat;
    //Barra de progreso
    SeekBar seekbar;
    private Handler myHandler = new Handler();
    private double startTime = 0;
    public static String timer = "2:34";
    //visor de tiempo
    TextView tx1,nombre,artista;
    //repetir lista
    boolean loop = false;
    //Intent
    static Intent intent;
    static int oneTimeOnly = 0;
    //vista
    ImageView img;
    List<Cancion> personas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vincularBotones();
        intent = new Intent(MainActivity.this,ServicioMusica.class);

        List<Cancion> personas = initSongsList();
        ListView lista = (ListView) findViewById(R.id.lista);
        CustomAdapter adapter = new CustomAdapter(this,personas);
        lista.setAdapter(adapter);

        tx1 = (TextView)findViewById(R.id.timer);
        nombre = (TextView)findViewById(R.id.nombre);
        artista = (TextView)findViewById(R.id.artista);
        img = (ImageView) findViewById(R.id.fondo);

        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setMax(100);//valor inicial predefinido
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (oneTimeOnly == 0 && ServicioMusica.getMediaPlayer()!=null) {
                    seekbar.setMax((int) ServicioMusica.getMediaPlayer().getDuration());
                    Log.i("duracion:",String.valueOf(ServicioMusica.getMediaPlayer().getDuration()/1000));
                    oneTimeOnly = 1;
                }

                if(ServicioMusica.getMediaPlayer() != null) {

                    if (fromUser) {
                        ServicioMusica.getMediaPlayer().seekTo(progress);
                        seekbar.setProgress(progress);
                    }
                    //se revisa si la canción finalizo
                    if (progress >= ServicioMusica.getMediaPlayer().getDuration() - 200) {
                        Log.i("Progressbar", "canción terminada");
                        intent.setAction(ACTION_STOP);
                        startService(intent);
                        //si el boton de repetir lista esta pulsado pasamos al sgte cancion
                        if (cursor == 2 && loop) {
                            next(true);
                        } else {
                            if (cursor != 2)
                                next(true);
                        }
                        setearVista();
                    }
                }
            }
        });
        setearVista();
    }

    private void vincularBotones() {
        btnStart = (ImageButton) findViewById(R.id.btnStart);
        btnStop = (ImageButton) findViewById(R.id.btnStop);
        btnPause = (ImageButton) findViewById(R.id.btnPause);
        btnnext = (ImageButton) findViewById(R.id.btnNext);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnrepeat = (ImageButton) findViewById(R.id.btnRepetir);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnnext.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnrepeat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.btnStart:
                intent.setAction(ACTION_PLAY);
                intent.putExtra("cursor",cursor);
                startService(intent);
                seekbar.setProgress((int)startTime);
                myHandler.postDelayed(UpdateSongTime,100);
                break;

            case R.id.btnPause:
                intent.setAction(ACTION_PAUSE);
                startService(intent);
                break;

            case R.id.btnStop:
                intent.setAction(ACTION_STOP);
                startService(intent);
                break;

            case R.id.btnNext:
                next(true);
                break;

            case R.id.btnBack:
                next(false);
                break;

            case R.id.btnRepetir:
                //se alterna entre On y Off
                loop = !loop;
                if(loop){
                    //btnrepeat.setText("ON");
                }else{
                    //btnrepeat.setText("OFF");
                }
                break;
        }
    }

    private void next(boolean value) {

        if(value)
            intent.setAction(ACTION_NEXT);
        else
            intent.setAction(ACTION_BACK);
        actualizarCursor(value);
        //intent.putExtra("cursor",cursor);
        startService(intent);
        //cambiar la vista y actualizar la portada de la cancion
        oneTimeOnly=0;
    }

    private void actualizarCursor(boolean up) {

        if(up){
            cursor++;
        }else {
            cursor--;
        }
        //se configura que el movimiento ciclico del cursor
        if(cursor>num-1){
            cursor=0;
        }
        if(cursor<0){
            cursor = num -1;
        }
        setearVista();
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {


            startTime = ServicioMusica.getMediaPlayer().getCurrentPosition();
            tx1.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            timer = String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)));

            if(ServicioMusica.getMediaPlayer().isPlaying()) {
                btnPause.setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.INVISIBLE);
            }else{
                btnPause.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.VISIBLE);
            }

            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    public static void setOneTimeOnly(int oneTimeOnly) {
        MainActivity.oneTimeOnly = oneTimeOnly;
    }

    private List<Cancion> initSongsList() {

        personas.add(new Cancion(caratulas[0],"Bangarabab", "Skrillex"));
        personas.add(new Cancion(caratulas[1], "Welcome to Jungle", "Guns & Roses"));
        personas.add(new Cancion(caratulas[2], "Papercut", "Linkin Park"));
        return personas;
    }

    public void setearVista(){
        nombre.setText(personas.get(cursor).getNombre());
        artista.setText(personas.get(cursor).getInterprete());
        nombre.setText(personas.get(cursor).getNombre());
        img.setImageResource(caratulas[cursor]);

    }
}
