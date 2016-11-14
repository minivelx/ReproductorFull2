package com.movil.p.reproductorfull;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.movil.p.reproductorfull.ServicioMusica.mp;

public class MyReceiver extends BroadcastReceiver {

    //Acciones
    private static final String ACTION_PLAY = "com.movil.p.reproductorfull.action.PLAY";
    private static final String ACTION_PAUSE = "com.movil.p.reproductorfull.action.PAUSE";
    private static final String ACTION_STOP = "com.movil.p.reproductorfull.action.STOP";
    private static final String ACTION_NEXT = "com.movil.p.reproductorfull.action.NEXT";
    private static final String ACTION_BACK = "com.movil.p.reproductorfull.action.BACK";
    private static final String ACTION_MAIN = "com.movil.p.reproductorfull.action.MAIN";
    private static final int PLAY = 0;
    private static final int PAUSE = 1;
    private static final int STOP = 2;
    private static final int NEXT = 3;
    private static final int BACK = 4;
    Intent myintent;
    int cursor = 0;

    public MyReceiver() {


    }

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()){

            case ACTION_PLAY:
                ServicioMusica.interfaz(PLAY);
                break;

            case ACTION_PAUSE:
                ServicioMusica.interfaz(PAUSE);
                break;

            case ACTION_NEXT:
                ServicioMusica.interfaz(NEXT);
                break;

            case ACTION_BACK:
                ServicioMusica.interfaz(BACK);
                break;
        }

    }
}
