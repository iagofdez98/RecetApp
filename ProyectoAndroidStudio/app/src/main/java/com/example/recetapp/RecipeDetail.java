package com.example.recetapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Database.CarritoFacade;
import Database.DBManager;
import Model.Ingrediente;
import Model.Receta;
import Database.RecipeFacade;


public class RecipeDetail extends AppCompatActivity {
    //Variable que guarda los campos de la receta a mostrar
    private Receta r=new Receta("","",0,"");

    //Inicializa la vista
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Integer recuperado = getIntent().getIntExtra("variable_receta", -1);



        RecipeFacade rec = new RecipeFacade(DBManager.getInstance(this));

        r = rec.getRecipesById(recuperado);
        

        getSupportActionBar().setTitle(r.getNombre());


        viewRecipe();



    }


    //Crea el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }
    //Dictamina la acción a lanzar al pulsar en una opción del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.compartir:
                shareRecipe();
                break;

            case R.id.eliminarReceta:
                deleteRecipeDialog(item);
                break;

            case  R.id.editarReceta:
                modifyRecipe(item);
                break;
            case  R.id.listaCompra:
                addCart();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    //Añade al carrito todos los ingredientes de la receta
    private void addCart() {
        CarritoFacade carritoFacade=new CarritoFacade(DBManager.getInstance(this));
        for(Ingrediente in: r.getIngredientes()){
            carritoFacade.createIngredientCarrito(in);
        }


        Toast.makeText(RecipeDetail.this,"Ingredientes añadidos a la cesta de la compra",Toast.LENGTH_LONG).show();
    }

    //Lanza el formulario de modificación de la receta
    private void modifyRecipe(MenuItem item) {

        Intent intent = new Intent(RecipeDetail.this, ModifyRecipeFormActivity.class);
        intent.putExtra("variable_receta", r.getId());
        startActivity(intent);


    }
    //Muesta un diálogo de confirmación de borrado de la receta
    public void deleteRecipeDialog(MenuItem item){
        AlertDialog.Builder builder = new AlertDialog.Builder(RecipeDetail.this);

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

    //Borrra la receta
    public void deleteRecipe(MenuItem item){
        RecipeFacade recipe=new RecipeFacade(DBManager.getInstance(RecipeDetail.this));
        recipe.removeRecipe(r);
        Intent intent = new Intent(RecipeDetail.this, MainActivity.class);
        intent.putExtra("variable_receta",(Integer) r.getId());
        startActivity(intent);




    }

    private Receta addReceta(){
        RecipeFacade recipe = new RecipeFacade(DBManager.getInstance(this));
        ((TextView)(findViewById(R.id.Elaboracion))).setText(r.getPreparacion());





        return recipe.createRecipe(r);
    }

    //Lanza un intent para enviar el archivo json de la receta a compartir
    private void shareRecipe(){
        File toShare=new File("");
        try{
            toShare=createFileToShare();
        }catch (IOException exc){
            System.err.println(exc.getMessage());
        }

        if(toShare.getAbsoluteFile().exists()) {

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            Uri toShareUri = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider",
                    toShare);
            ;

            sendIntent.setType("application/json");
            sendIntent.putExtra(Intent.EXTRA_STREAM,toShareUri);
            sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            startActivity(Intent.createChooser(sendIntent, null));

        }
    }

    //Crea el archivo json para la receta
    private File createFileToShare() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JSON_" + timeStamp + "_";




        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File recipe = File.createTempFile(
                imageFileName,  /* prefix */
                ".json",         /* suffix */
                storageDir      /* directory */
        );


        r.toJSON(recipe);
        return recipe;

    }



    //Asigna a los widgets la información de la receta
    private void viewRecipe() {
        ((TextView) findViewById(R.id.Elaboracion)).setText(r.getPreparacion());
        ((TextView) findViewById(R.id.numComensales)).setText(r.getNumComensales().toString());
        ((TextView) findViewById(R.id.tipo)).setText(r.getTipo());
        ((TextView) findViewById(R.id.ingredientesList)).setText(ingredientesToString(r.getIngredientes()));
        if(!(r.getRutaFoto()==null)) {
            File f = new File(r.getRutaFoto());
            ImageView im = (ImageView) findViewById(R.id.imageView);
            im.setImageURI(Uri.fromFile(f));

        }
    }


    public String  ingredientesToString(List<Ingrediente> ingredientes) {
        Iterator< Ingrediente> it= ingredientes.iterator();
        StringBuilder toret = new StringBuilder();
        System.out.println(it.hasNext());
        Ingrediente temporal;
        while(it.hasNext()){
            temporal=it.next();
            toret.append(temporal.getCantidad() + " " + temporal.getMedida() + " " + temporal.getNombre() + "\n" );
        }
        return toret.toString();

    }

}