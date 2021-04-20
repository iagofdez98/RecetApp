package com.example.recetapp.ui;

import android.database.Cursor;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.recetapp.R;

import java.io.Serializable;

import Database.DBManager;
import Database.RecipeFacade;
import Model.Receta;
import Model.Recetario;
import Utility.ListViewAdapter;

public class BaseFragment extends Fragment implements Serializable {
    private ListViewAdapter adapter;
    DBManager test;

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.contextual_menu_listview, menu);
    }




    public Recetario rellenarListView(String selected){

        RecipeFacade recipeFacade=new RecipeFacade(DBManager.getInstance(getActivity()));
        Cursor cursorRecetas;
        if(selected=="Home")
            cursorRecetas=recipeFacade.getRecipes();
        else
            cursorRecetas=recipeFacade.getRecipesByType(selected);
        Recetario recetas=new Recetario();

        if(cursorRecetas.getCount()>0) {
            cursorRecetas.moveToFirst();

            do {
                Receta r=new Receta(cursorRecetas.getString(cursorRecetas.getColumnIndex("name")),cursorRecetas.getString(cursorRecetas.getColumnIndex("type")),
                        cursorRecetas.getInt(cursorRecetas.getColumnIndex("type")),cursorRecetas.getString(cursorRecetas.getColumnIndex("preparation")));


                int id=cursorRecetas.getInt(cursorRecetas.getColumnIndex("_id"));
                String photo=cursorRecetas.getString(cursorRecetas.getColumnIndex("photo"));

                r.setId(id);

                if(photo!=null){

                    r.setRutaFoto(photo);
                }
                recetas.addReceta(r);

            } while (cursorRecetas.moveToNext());
        }
        return recetas;


    }

}