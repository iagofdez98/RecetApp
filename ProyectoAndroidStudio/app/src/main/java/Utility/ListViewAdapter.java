package Utility;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recetapp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Model.Ingrediente;
import Model.Receta;
import Model.Recetario;


public class ListViewAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;

    private Context contexto;
    private Recetario r;


    public ListViewAdapter(Context conexto, Recetario r)
    {
        this.contexto = conexto;
        this.r=r;

        inflater = (LayoutInflater)conexto.getSystemService(conexto.LAYOUT_INFLATER_SERVICE);
    }

    //Asigna a los componentes los datos a mostrar
    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        final View vista = inflater.inflate(R.layout.fragment_home_listview_layout, null);

        Receta actual=r.get(i);
        ImageView photo = (ImageView) vista.findViewById(R.id.image);
        TextView name = (TextView) vista.findViewById(R.id.Titulo);
        TextView type = (TextView) vista.findViewById(R.id.Categoria);

        if(actual.getRutaFoto()!=null) {
            File f = new File(actual.getRutaFoto());
            photo.setImageURI(Uri.fromFile(f));
        }else{
            photo.setImageResource(R.drawable.base_empty_image);
        }
        name.setText(actual.getNombre());
        type.setText(actual.getTipo());


        return vista;
    }

    public boolean removeReceta(Receta receta){



        return this.r.removeRecetaById(receta.getId());

    }

    public Recetario getR() {
        return r;
    }

    @Override
    public int getCount() {
        return r.getSize();
    }

    @Override
    public Receta getItem(int position) {
        return r.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}
