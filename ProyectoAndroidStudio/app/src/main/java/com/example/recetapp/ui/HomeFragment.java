package com.example.recetapp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.recetapp.AddRecipeFormActivity;
import com.example.recetapp.ModifyRecipeFormActivity;
import com.example.recetapp.R;
import com.example.recetapp.RecipeDetail;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Database.DBManager;
import Database.RecipeFacade;
import Model.Receta;
import Model.Recetario;
import Utility.ListViewAdapter;

public class HomeFragment extends BaseFragment  {

    //ListView de la vista
    private ListView lv;
    //Adaptador de la ListView
    private ListViewAdapter adapter;
    //Contenedor de recetas
    private Recetario recetas;

    //Inicializa la vista y se registran los eventos principales
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recetas=rellenarListView("Home"); //Metodo que recupera todas las recetas del tipo que se le pasa como parametro, si se envia un "Home" se devuelven todas
        adapter=new ListViewAdapter(getActivity(),recetas); //Crea un adaptador con las recetas obtenidas con anterioridad
        lv=(ListView)root.findViewById(R.id.lvLista);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), RecipeDetail.class);
                intent.putExtra("variable_receta", adapter.getItem(position).getId()); //Le envia la receta en si a detail para que pueda cargar su vista
                startActivity(intent);
            }
        });
        registerForContextMenu(lv);
        lv.setAdapter(adapter);
        final FloatingActionButton btn = (FloatingActionButton) root.findViewById(R.id.floatingActionButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddRecipeFormActivity.class); //Lanza la actividad de crear una receta nueva
                startActivity(intent);
            }
        });
        return root;
    }

    //Cuando la aplicación vuelve de una pausa se recargan los datos del ListView, ya que estos pueden haber cambiado
    @Override
    public void onResume() {
        super.onResume();
        DBManager.getInstance(getActivity());
        Recetario recetas=rellenarListView("Home");
        //Home envia para indicar que se deben cargar todas las recetas, si se enviara carnes solo se cargarian las de este tipo
        adapter=new ListViewAdapter(getActivity(),recetas);
        lv.setAdapter(adapter);
    }


    //Método que contempla las acciones a realizar segundo la opción pulsada por el usuario
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.DeleteRecipe:
                deleteRecipeDialog(item);
                return true;
            case R.id.ModifyRecipe:
                modifyRecipe(item);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    //Lanza el formulario de modificación de receta
    private void modifyRecipe(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Intent intent = new Intent(getActivity(), ModifyRecipeFormActivity.class);
        intent.putExtra("variable_receta", adapter.getItem(position).getId());
        startActivity(intent);
    }

    //Dialogo de confirmación de eliminación de una receta
    public void deleteRecipeDialog(MenuItem item){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog.Builder builder1 = builder.setMessage(R.string.DeleteAreYouSure)
                .setTitle(R.string.DeleteRecipe);
        builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteRecipe(item);
            }
        });
        builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Elimina la receta de la BD así como de la ListView
    public void deleteRecipe(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        RecipeFacade recipe=new RecipeFacade(DBManager.getInstance(getActivity()));
        Receta r=recipe.getRecipesById(adapter.getItem(position).getId());
        recipe.removeRecipe(r);
        adapter.removeReceta(r);
        adapter.notifyDataSetChanged();
    }
}