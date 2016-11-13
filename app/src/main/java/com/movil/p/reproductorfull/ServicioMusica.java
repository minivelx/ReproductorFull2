package com.movil.p.reproductorfull;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ServicioMusica extends Service{

    //posicion de la canción actual
    private int cursor = 0;
    //Acciones
    private static final String ACTION_PLAY = "com.movil.p.reproductorfull.action.PLAY";
    private static final String ACTION_PAUSE = "com.movil.p.reproductorfull.action.PAUSE";
    private static final String ACTION_STOP = "com.movil.p.reproductorfull.action.STOP";
    private static final String ACTION_NEXT = "com.movil.p.reproductorfull.action.NEXT";
    private static final String ACTION_BACK = "com.movil.p.reproductorfull.action.BACK";
    //Media player
    static MediaPlayer mp = null;
    //Lista canciones
    int [] lista = {R.raw.audio, R.raw.audio2, R.raw.audio3};
    //Hilo
    private Thread workerThread = null;
    //control
    boolean shouldPause = false;

    @Override
    public void onCreate() {
        //startForeground();
    }

    //******************MAIN******************************
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("onStartCommand","Recibiendo Intent");

        //Btn start pulsado
        if (intent.getAction().equals(ACTION_PLAY)) {
            cursor = intent.getIntExtra("cursor",0);
            Log.i("Cursor seteado",String.valueOf(cursor));
            //solo se ejcutara la primer vez que le den al play
            if (workerThread == null /*|| !workerThread.isAlive() */) {
                Log.i("onThead","creando hilo");
                workerThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        start();
                    }
                });
                workerThread.start();
            //desde la segunda vez en adelante...
            }else{
                start();
            }
        }

        //Btn pause pulsado
        if (intent.getAction().equals(ACTION_PAUSE)) {

            if(mp != null) {
                Log.i("Pausa","pausando");
                mp.pause();
            }
        }

        //Btn stop pulsado
        if (intent.getAction().equals(ACTION_STOP)) {

            if(mp != null) {
                Log.i("Stop","parado");
                mp.stop();
                mp.release();
                mp=null;
                mp = MediaPlayer.create(this, lista[cursor]);
            }
        }

        //Btn cambiar de cancion pulsado
        if (intent.getAction().equals(ACTION_NEXT) || intent.getAction().equals(ACTION_BACK)) {

            if(mp != null) {
                Log.i("siguiente","next");
                cursor = intent.getIntExtra("cursor",0);
                Log.i("Cursor seteado",String.valueOf(cursor));
                next();
            }
        }
        return START_STICKY;
    }
    //*****************************************************

    @Override
    public void onDestroy() {


    }

    @Override
    public IBinder onBind(Intent intencion) {
        return null;
    }

    static public MediaPlayer getMediaPlayer() {
        return mp;
    }

    //Control del start
    private void start(){
        Log.i("OnCreate","start()");
        startSong();
        shouldPause = false;
    }

    private void startSong(){
        if(mp == null){
            Log.i("OnCreate","cargando audio");
            mp = MediaPlayer.create(this, lista[cursor]);
            mp.start();
        }
        if(!mp.isPlaying()){
            //mMediaPlayer.setLooping(true);
            mp.start();
        }
    }

    //Control del pause
    private void pause(){
        shouldPause = true;
        if(shouldPause) {
            mp.pause();
        }

    }

    // Control del Next
    private void next(){

        if(mp.isPlaying()){
            mp.stop();
            mp.release();
            mp=null;
        }

        mp = MediaPlayer.create(this, lista[cursor]);
        mp.start();

    }

}
