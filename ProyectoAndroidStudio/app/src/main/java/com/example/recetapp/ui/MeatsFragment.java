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

public class MeatsFragment extends BaseFragment {

    private ListView lv;
    private ListViewAdapter adapter;
    private Recetario recetas;

    //Explicacion de los metodos y atributos equivalente a los vistos en HomeFragment
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recetas=rellenarListView("Carnes");
        adapter=new ListViewAdapter(getActivity(),recetas);
        lv=(ListView)root.findViewById(R.id.lvLista);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), RecipeDetail.class);
                intent.putExtra("variable_receta", adapter.getItem(position).getId());
                startActivity(intent);
            }
        });
        registerForContextMenu(lv);
        lv.setAdapter(adapter);
        final FloatingActionButton btn = (FloatingActionButton) root.findViewById(R.id.floatingActionButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddRecipeFormActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

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

    public void deleteRecipe(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        RecipeFacade recipe=new RecipeFacade(DBManager.getInstance(getActivity()));
        Receta r=recipe.getRecipesById(adapter.getItem(position).getId());
        recipe.removeRecipe(r);
        adapter.removeReceta(r);
        adapter.notifyDataSetChanged();
    }

    private void modifyRecipe(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Intent intent = new Intent(getActivity(), ModifyRecipeFormActivity.class);
        intent.putExtra("variable_receta", adapter.getItem(position).getId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Recetario recetas=rellenarListView("Carnes");
        adapter=new ListViewAdapter(getActivity(),recetas);
        lv.setAdapter(adapter);
    }
}

