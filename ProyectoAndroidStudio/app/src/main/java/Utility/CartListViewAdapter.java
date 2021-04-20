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

import Model.Carrito;
import Model.Ingrediente;
import Model.Receta;
import Model.Recetario;


public class CartListViewAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;

    private Context contexto;
    private Carrito c;


    public CartListViewAdapter(Context conexto, Carrito c)
    {
        this.contexto = conexto;
        this.c= c;

        inflater = (LayoutInflater)conexto.getSystemService(conexto.LAYOUT_INFLATER_SERVICE);
    }

    //Asigna a los componentes los datos a mostrar
    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        final View vista = inflater.inflate(R.layout.fragment_cart_listview_layout, null);

        Ingrediente actual = c.get(i);
        TextView quantity = (TextView) vista.findViewById(R.id.Quantity);
        TextView meassure = (TextView) vista.findViewById(R.id.MeassureUnit);
        TextView name = (TextView) vista.findViewById(R.id.IngredientName);

        quantity.setText(actual.getCantidad().toString());
        meassure.setText(actual.getMedida());
        name.setText(actual.getNombre());

        return vista;
    }

    public boolean removeIngrediente(Ingrediente ingrediente){

        return this.c.RemoveIngrediente(ingrediente);

    }
    public void resetCarrito(){
        c.resetCarrito();
    }

    public Carrito getC() {
        return c;
    }

    @Override
    public int getCount() {
        return c.getSize();
    }

    @Override
    public Ingrediente getItem(int position) {
        return c.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}

