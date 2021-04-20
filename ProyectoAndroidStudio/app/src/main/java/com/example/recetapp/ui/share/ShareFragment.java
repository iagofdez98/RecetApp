package com.example.recetapp.ui.share;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.recetapp.MainActivity;
import com.example.recetapp.R;
import com.google.gson.Gson;

import Database.DBManager;
import Database.IngredientFacade;
import Database.RecipeFacade;
import Model.Ingrediente;
import Model.Receta;

public class ShareFragment extends Fragment {

    private ShareViewModel shareViewModel;
    private final int EXTERNAL_STORAGE_CODE=1000;
    private final int READ_REQUEST_CODE=300;
    //Inicializa la acción de importar
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       shareViewModel =
                new ViewModelProvider(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);

        importFile();

        return root;
    }


    //Se lanza la acción de seleccionar archivos si hay permisos, si no se piden
    private void importFile(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, EXTERNAL_STORAGE_CODE);
        }else{
            openFileExplorer();
        }


    }
    //Se abre el explorador de archivos que permite selecionar archivos json
    private void openFileExplorer() {
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        startActivityForResult(intent,READ_REQUEST_CODE);
    }



    //Si la lectura del archivo se ha producido satisfactoriamente se importa la receta
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==READ_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            if(data != null){

                Uri uri= data.getData();
                Receta receta=new Receta("","",0,"");

                try{

                    receta.fromJSON(getActivity(),uri);

                    receta.setRutaFoto(null);


                }catch (Exception exc){
                    exc.getStackTrace();
                    Toast.makeText(getActivity(),"El archivo no es válido",Toast.LENGTH_SHORT).show();
                }

                if(receta.getNombre().length()!=0) {

                    RecipeFacade recipe = new RecipeFacade(DBManager.getInstance(getContext()));

                    receta.setId(recipe.getIdAImportar());
                    IngredientFacade ingredientes = new IngredientFacade(DBManager.getInstance(getContext()));


                    Receta aInsertar = recipe.createRecipe(receta);


                    for (Ingrediente i : receta.getIngredientes()) {
                        ingredientes.createIngredient(i, aInsertar.getId());
                    }


                }

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

            }
        }
    }

    //Se piden los permisos para acceder al explorador de archivos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==EXTERNAL_STORAGE_CODE){
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getActivity(),"Se necesita aceptar para poder importar archivos",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        }
    }


}