package com.example.recetapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Database.DBManager;
import Database.RecipeFacade;
import Model.Receta;
import Model.Recetario;
import Utility.ListViewAdapter;

public class PrincipalActivity extends AppCompatActivity {
    private ListView lv;
    private ListViewAdapter adapter;
    private Recetario recetas;
    protected void onCreate(Bundle savedInstanceState , ViewGroup container) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        recetas=rellenarListView("Carnes");
        adapter=new ListViewAdapter(this,recetas);

        lv=(ListView) findViewById(R.id.lvLista);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PrincipalActivity.this, RecipeDetail.class);
                intent.putExtra("variable_receta", adapter.getItem(position).getId());
                startActivity(intent);
            }
        });

        registerForContextMenu(lv);



        lv.setAdapter(adapter);
        final FloatingActionButton btn = (FloatingActionButton) container.findViewById(R.id.floatingActionButton);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, AddRecipeFormActivity.class);

                startActivity(intent);
            }
        });

    }


    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.contextual_menu_listview, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.DeleteRecipe:
                deleteRecipeDialog(item);
                return true;
            case R.id.ModifyRecipe:
                //deleteNote(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void deleteRecipeDialog(MenuItem item){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
        RecipeFacade recipe=new RecipeFacade(DBManager.getInstance(this));
        Receta r=recipe.getRecipesById(adapter.getItem(position).getId());
        recipe.removeRecipe(r);
        adapter.removeReceta(r);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onResume() {
        super.onResume();

        Recetario recetas=rellenarListView("Carnes");

        adapter=new ListViewAdapter(this,recetas);

        lv.setAdapter(adapter);
    }




    private Recetario rellenarListView(String selected){

        RecipeFacade recipeFacade=new RecipeFacade(DBManager.getInstance(this));
        Cursor cursorRecetas;
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