package com.movil.p.reproductorfull;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //ID de la cancion inicial
    private int cursor = 0;
    //numero de canciones en la lista
    private int num = 3;
    //Acciones
    private static final String ACTION_PLAY = "com.movil.p.reproductorfull.action.PLAY";
    private static final String ACTION_PAUSE = "com.movil.p.reproductorfull.action.PAUSE";
    private static final String ACTION_STOP = "com.movil.p.reproductorfull.action.STOP";
    private static final String ACTION_NEXT = "com.movil.p.reproductorfull.action.NEXT";
    private static final String ACTION_BACK = "com.movil.p.reproductorfull.action.BACK";
    //Botones
    Button btnPause, btnStart, btnStop, btnnext, btnBack;
    //Barra de progreso
    SeekBar seekbar;
    private Handler myHandler = new Handler();
    private double startTime = 0;
    //visor de tiempo
    TextView tx1;
    //Intent
    Intent intent;
    int oneTimeOnly = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vincularBotones();
        intent = new Intent(MainActivity.this,ServicioMusica.class);

        tx1 = (TextView)findViewById(R.id.timer);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser){
                    ServicioMusica.getMediaPlayer().seekTo(progress);
                    seekbar.setProgress(progress);
                }
            }
        });

    }

    private void vincularBotones() {
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnnext = (Button) findViewById(R.id.btnNext);
        btnBack = (Button) findViewById(R.id.btnBack);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnnext.setOnClickListener(this);
        btnBack.setOnClickListener(this);
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
                intent.setAction(ACTION_NEXT);
                actualizarCursor(true);
                intent.putExtra("cursor",cursor);
                startService(intent);
                //cambiar la vista y actualizar la portada de la cancion
                oneTimeOnly=0;
                break;

            case R.id.btnBack:
                intent.setAction(ACTION_BACK);
                actualizarCursor(false);
                intent.putExtra("cursor",cursor);
                startService(intent);
                //cambiar la vista y actualizar la portada de la cancion
                oneTimeOnly=0;
                break;
        }
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
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {

            if (oneTimeOnly == 0) {
                seekbar.setMax((int) ServicioMusica.getMediaPlayer().getDuration());
                oneTimeOnly = 1;
            }
            startTime = ServicioMusica.getMediaPlayer().getCurrentPosition();
            tx1.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };
}
