package com.movil.p.reproductorfull;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.concurrent.TimeUnit;

import static java.lang.System.in;

public class ServicioMusica extends Service{

    //posicion de la canción actual
    private static int cursor = 0;
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
    private Handler myHandler = new Handler();
    private double startTime = 0;
    //Lista canciones
    int [] lista = {R.raw.audio, R.raw.audio2, R.raw.audio3};
    //Hilo
    private Thread workerThread = null;
    //control
    boolean shouldPause = false;
    //notificacion
    RemoteViews contentView;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    private void foreground(){
        /*
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notificacion_audio);
        // notification's icon
        remoteViews.setImageViewResource(R.id.notif_icon, R.drawable.icono_play);
        // notification's title
        //remoteViews.setTextViewText(R.id.notif_title, getResources().getString(R.string.app_name));



        Intent intent;
        PendingIntent pendingIntent;

        //Accion btn play
        intent = new Intent(getApplicationContext(), MyReceiver.class);
        intent.setAction(ACTION_PLAY);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                REQUEST_CODE_PLAY, intent, 0);

        remoteViews.setOnClickPendingIntent(R.id.boton_play, pendingIntent);

        //Accion btn pause
        intent = new Intent(getApplicationContext(), MyReceiver.class);
        intent.setAction(ACTION_PAUSE);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                REQUEST_CODE_PAUSE, intent, 0);

        remoteViews.setOnClickPendingIntent(R.id.boton_pause, pendingIntent);

        //Accion btn next


        //Accion btn back


        //Lanzar notificacion
        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.icono_play)
                .setOngoing(true)
                .setContentTitle("Reproductor de musica")
                .setWhen(System.currentTimeMillis())
                .setContent(remoteViews)

                .build();
        startForeground(100, notification);*/

        Intent intent;
        PendingIntent pendingIntent;

        int icon = R.mipmap.ic_launcher;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, "Custom Notification", when);

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        contentView = new RemoteViews(getPackageName(), R.layout.notificacion_audio);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, "Reproductor MP3");
        //contentView.setTextViewText(R.id.text, "Linkin Park - Numb");
        contentView.setTextColor(R.id.title, Color.parseColor("#000000"));
        //contentView.setTextColor(R.id.text, Color.parseColor("#000000"));

        //Accion btn play
        intent = new Intent(getApplicationContext(), ServicioMusica.class);
        intent.setAction(ACTION_PLAY);
        pendingIntent = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_PLAY, intent, 0);
        contentView.setOnClickPendingIntent(R.id.boton_play, pendingIntent);

        //Accion btn pause
        intent = new Intent(getApplicationContext(), ServicioMusica.class);
        intent.setAction(ACTION_PAUSE);
        pendingIntent = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_PAUSE, intent, 0);
        contentView.setOnClickPendingIntent(R.id.boton_pause, pendingIntent);

        //Accion btn next
        intent = new Intent(getApplicationContext(), ServicioMusica.class);
        intent.setAction(ACTION_NEXT);
        intent.putExtra("cursor",cursor+1);
        pendingIntent = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_NEXT, intent, 0);
        contentView.setOnClickPendingIntent(R.id.boton_next, pendingIntent);

        //Accion btn back
        intent = new Intent(getApplicationContext(), ServicioMusica.class);
        intent.setAction(ACTION_BACK);
        intent.putExtra("cursor",cursor-1);
        pendingIntent = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_BACK, intent, 0);
        contentView.setOnClickPendingIntent(R.id.boton_back, pendingIntent);

        //myHandler.postDelayed(UpdateSongTime,100);
        contentView.setTextViewText(R.id.tx2, MainActivity.timer);
        contentView.setTextColor(R.id.tx2, Color.parseColor("#000000"));
        //-----------------------------------------------------------------------------------------
        notification.contentView = contentView;

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;

        notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        //notification.defaults |= Notification.DEFAULT_SOUND; // Sound

        //mNotificationManager.notify(100, notification);
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
            //se configura que el movimiento ciclico del cursor
            if(cursor>2){
                cursor=0;
            }
            if(cursor<0){
                cursor = 2;
            }
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
            myHandler.postDelayed(UpdateSongTime,1000);
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

        //Btn next pulsado
        if (intent.getAction().equals(ACTION_NEXT) ) {

            MainActivity.setOneTimeOnly(0);
            if(mp != null) {
                Log.i("siguiente","next");
                cursor++;
                if(cursor>2){
                    cursor=0;
                }
                Log.i("Cursor seteado",String.valueOf(cursor));
                next();
            }
        }

        //Btn back pulsado
        if (intent.getAction().equals(ACTION_BACK)) {
            MainActivity.setOneTimeOnly(0);
            if(mp != null) {
                Log.i("siguiente","next");
                cursor--;
                if(cursor<0){
                    cursor = 2;
                }
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
/*
            case REQUEST_CODE_NEXT :
                next(true);
                break;

            case REQUEST_CODE_BACK :
                next(false);
                break;*/

        }

    }

    private Runnable UpdateSongTime = new Runnable() {

        public void run() {
            if(mp.isPlaying()) {
                Log.i("not","detenido");
                foreground();
            }
            myHandler.postDelayed(this, 1000);
        }
    };
}
