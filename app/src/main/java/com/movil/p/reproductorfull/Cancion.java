package com.movil.p.reproductorfull;

/**
 * Created by miguel on 7/11/16.
 */

public class Cancion {

    private String nombre;
    private String Interprete;
    private int caratula;

    public Cancion() {
    }

    public Cancion(int caratula,String nombre, String interprete) {
        this.nombre = nombre;
        Interprete = interprete;
        this.caratula = caratula;
    }

    public String getInterprete() {
        return Interprete;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setInterprete(String interprete) {
        Interprete = interprete;
    }

    public int getCaratula() {
        return caratula;
    }
}
