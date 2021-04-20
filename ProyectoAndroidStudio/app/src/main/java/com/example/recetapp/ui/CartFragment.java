package com.example.recetapp.ui;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.recetapp.R;

import Database.CarritoFacade;
import Database.DBManager;
import Model.Carrito;
import Model.Ingrediente;
import Utility.CartListViewAdapter;

public class CartFragment extends Fragment {

    //Contenedor de ingredientes
    private Carrito carrito;
    //Listview del carrito
    private ListView lv;
    //Adaptador del carrito;
    private CartListViewAdapter adapter;

    //Se inicializa la vista y se enlaza con el adaptador
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        carrito= rellenarListView();
        adapter=new CartListViewAdapter(getActivity(),carrito);
        lv=(ListView)root.findViewById(R.id.lvLista);
        registerForContextMenu(lv);
        return root;
    }

    //Se inicializa el menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    //Se genera el menu a partir del layout
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_cart, menu);
    }

    //Metodo que dictamina que acción realizar al pulsar una opción en el menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.eliminarCarrito:
                deleteDialogCarrito(item);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //Muesta un diálogo de confirmación para el borrado del carrito
    private void deleteDialogCarrito(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog.Builder builder1 = builder.setMessage(R.string.DeleteAreYouSureCart)
                .setTitle(R.string.DeleteCart);
        builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeCart();
            }
        });
        builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Elimina todo el contenido del carrito

    public void removeCart(){
        CarritoFacade carritoFacade=new CarritoFacade(DBManager.getInstance(getActivity()));
        carritoFacade.removeCarrito();
        adapter.resetCarrito();
        adapter.notifyDataSetChanged();
    }


    //Crea el menú contextual a partir del layout establecido
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.contextual_menu_cart, menu);
    }

    //Cierra la base datos una vez entra en pausa el carrito
    @Override
    public void onPause() {
        super.onPause();
        DBManager.getInstance(getActivity()).close();
    }

    //Recarga el carrito en el listview una vez se reinicia la vista
    @Override
    public void onResume() {
        super.onResume();
        carrito=rellenarListView();
        adapter=new CartListViewAdapter(getActivity(),carrito);
        lv.setAdapter(adapter);
    }

    //Dictamina la acción a realizar al pulsar una opción del menú contextual
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.DeleteRecipe:
                deleteItemDialog(item);
                return true;
            case R.id.ModifyRecipe:
            default:
                return super.onContextItemSelected(item);
        }
    }

    //Dialogo de confirmación de borrado de un Ingrediente
    public void deleteItemDialog(MenuItem item){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog.Builder builder1 = builder.setMessage(R.string.DeleteAreYouSureIngredient)
                .setTitle(R.string.DeleteIngredient);
        builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem(item);
            }
        });
        builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Elimina un item del carrito
    public void deleteItem(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        CarritoFacade carritoBD=new CarritoFacade(DBManager.getInstance(getActivity()));
        Ingrediente ingrediente=carrito.getIngredienteByName(adapter.getItem(position).getNombre(),adapter.getItem(position).getMedida());
        carrito.removeIngredientePorNombre(ingrediente.getNombre());
        carritoBD.removeIngredientCarrito(ingrediente);
        adapter.notifyDataSetChanged();
    }

    //Rellena el carrito (estructura de datos) para mostrarse en el listview
    public Carrito rellenarListView(){
        CarritoFacade carritoBD=new CarritoFacade(DBManager.getInstance(getActivity()));
        Cursor carritoCursor=carritoBD.getCarrito();
        Carrito aux=new Carrito();
        if(carritoCursor.moveToFirst()){
            do{
                aux.addIngrediente(new Ingrediente(carritoCursor.getDouble(carritoCursor.getColumnIndex("cantidad_carrito")),carritoCursor.getString(carritoCursor.getColumnIndex("medida_carrito")), carritoCursor.getString(carritoCursor.getColumnIndex("name_carrito"))));
            }while(carritoCursor.moveToNext());
        }
        return aux;
    }
}