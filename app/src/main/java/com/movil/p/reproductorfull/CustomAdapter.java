package com.movil.p.reproductorfull;

import android.content.Context;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by miguel on 7/11/16.
 */

public class CustomAdapter extends ArrayAdapter<Cancion> {

    public CustomAdapter(Context context, List<Cancion> objects) {
        super(context, R.layout.item_custom, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View view = super.getView(position, convertView, parent);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_custom, null);
        TextView nombre = (TextView) view.findViewById(R.id.nombre);
        TextView artista = (TextView) view.findViewById(R.id.artist);
        ImageView photo = (ImageView) view.findViewById(R.id.caratula);

        Cancion cancion = getItem(position);
        nombre.setText(cancion.getNombre());
        artista.setText(cancion.getInterprete());
        photo.setImageDrawable(AppCompatResources.getDrawable(getContext(),cancion.getCaratula()));

        return view;
    }
}
