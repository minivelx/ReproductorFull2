package com.movil.p.reproductorfull;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

public class ServicioMusica extends Service{

    //posicion de la canción actual
    private int cursor = 0;
    //Acciones
    private static final String ACTION_PLAY = "com.movil.p.reproductorfull.action.PLAY";
    private static final String ACTION_PAUSE = "com.movil.p.reproductorfull.action.PAUSE";
    private static final String ACTION_STOP = "com.movil.p.reproductorfull.action.STOP";
    private static final String ACTION_NEXT = "com.movil.p.reproductorfull.action.NEXT";
    private static final String ACTION_BACK = "com.movil.p.reproductorfull.action.BACK";
    private static final int REQUEST_CODE_PLAY = 0;
    private static final int REQUEST_CODE_PAUSE = 1;
    private static final int REQUEST_CODE_STOP = 2;
    private static final int REQUEST_CODE_NEXT = 3;
    private static final int REQUEST_CODE_BACK = 4;

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
        super.onCreate();

    }

    private void foreground(){
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notificacion_audio);
        Intent intent;
        PendingIntent pendingIntent;

        intent = new Intent(getApplicationContext(), MyReceiver.class);
        intent.setAction(ACTION_PLAY);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                REQUEST_CODE_PLAY, intent, 0);

        remoteViews.setOnClickPendingIntent(R.id.boton_play,
                pendingIntent);

        intent = new Intent(getApplicationContext(), MyReceiver.class);
        intent.setAction(ACTION_PAUSE);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                REQUEST_CODE_PAUSE, intent, 0);

        remoteViews.setOnClickPendingIntent(R.id.boton_pause,
                pendingIntent);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.icono_play)
                .setOngoing(true)
                .setContentTitle("Reproductor de musica")
                .setWhen(System.currentTimeMillis())
                .setContent(remoteViews)

                .build();
        startForeground(100, notification);
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
            if ( workerThread == null || !workerThread.isAlive() ) {
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
            foreground();
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
            stopForeground(true);
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

    //broadcast
    static public void interfaz(int action){

        switch (action){

            case REQUEST_CODE_PLAY :
                mp.start();
                break;

            case REQUEST_CODE_PAUSE :
                mp.pause();
                break;

        }

    }

}
