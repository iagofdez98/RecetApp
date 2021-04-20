package Database;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import Model.Ingrediente;
import Model.Receta;

public class RecipeFacade {

    private DBManager dbManager;

    public RecipeFacade(DBManager dbManager){
        this.dbManager = dbManager;
    }

    //readRecipe: Lee la receta del cursor que se le pasa
    public Receta readRecipe(Cursor cursor){
        Receta r = new Receta(null,null,null,null);

        r.setId(cursor.getInt(cursor.getColumnIndex(DBManager.RECIPE_COLUMN_ID)));
        r.setNombre(cursor.getString(cursor.getColumnIndex(DBManager.RECIPE_COLUMN_NAME)));
        r.setTipo(cursor.getString(cursor.getColumnIndex(DBManager.RECIPE_COLUMN_TYPE)));
        r.setNumComensales(cursor.getInt(cursor.getColumnIndex(DBManager.RECIPE_COLUMN_NUMCOM)));
        r.setPreparacion(cursor.getString(cursor.getColumnIndex(DBManager.RECIPE_COLUMN_PREPARATION)));
        r.setRutaFoto(cursor.getString(cursor.getColumnIndex(DBManager.RECIPE_COLUMN_PHOTO)));

        int id = r.getId();

        Cursor cursorIngredientes;
        String query=("SELECT * FROM " + DBManager.RELACION_TABLE_NAME + " INNER JOIN " + DBManager.RECIPE_TABLE_NAME
                        + " ON " + DBManager.RECIPE_COLUMN_ID + " = " + DBManager.RELACION_COLUMN_ID_RECIPE +
                        " WHERE " + DBManager.RELACION_COLUMN_ID_RECIPE + " =? ");

        cursorIngredientes=dbManager.getReadableDatabase().rawQuery(query,new String[]{id+""});

        cursorIngredientes.moveToFirst();

        do{

            r.addIngrediente(new Ingrediente(cursorIngredientes.getDouble(cursorIngredientes.getColumnIndex("cantidad")),
                    cursorIngredientes.getString(cursorIngredientes.getColumnIndex("medida")),
                    cursorIngredientes.getString(cursorIngredientes.getColumnIndex("ingredient_name"))));
        }while(cursorIngredientes.moveToNext());

        return r;
    }

    //getRecipes: Lee las recetas de la tabla
    public Cursor getRecipes(){
        Cursor toret = null;
        toret = dbManager.getReadableDatabase().rawQuery("SELECT * FROM " + DBManager.RECIPE_TABLE_NAME, null);
        return toret;
    }

    //getRecipesByType: Filtra las recetas por el tipo que se le pasa
    public Cursor getRecipesByType(String type){
        Cursor toret = null;
        String[] args = {type};
        toret = dbManager.getReadableDatabase().rawQuery("SELECT * FROM " + DBManager.RECIPE_TABLE_NAME +" WHERE " + DBManager.RECIPE_COLUMN_TYPE + "=?", new String[]{type});
        return toret;

    }

    //createRecipe: Crea la receta pasada en la BD, añade los ingredientes a la tabla de ingredientes.
    public Receta createRecipe(Receta r){
        SQLiteDatabase db = dbManager.getWritableDatabase();
        int lastId = -1;

        try{
            db.beginTransaction();
            db.execSQL(
                    "INSERT INTO " + DBManager.RECIPE_TABLE_NAME +
                        "(" + DBManager.RECIPE_COLUMN_ID + ","
                            + DBManager.RECIPE_COLUMN_NAME + ","
                            + DBManager.RECIPE_COLUMN_TYPE + ","
                            + DBManager.RECIPE_COLUMN_NUMCOM + ","
                            + DBManager.RECIPE_COLUMN_PREPARATION + ","
                            + DBManager.RECIPE_COLUMN_PHOTO +  ") VALUES(?,?,?,?,?,?)",
                    new Object[]{r.getId(), r.getNombre(), r.getTipo(), r.getNumComensales(),  r.getPreparacion(), r.getRutaFoto()}
            );

            for(Ingrediente i : r.getIngredientes()){
                db.execSQL("INSERT INTO " + DBManager.RELACION_TABLE_NAME +
                        "(" + DBManager.RELACION_COLUMN_ID_RECIPE + ","
                            + DBManager.RELACION_COLUMN_NAME + ","
                            + DBManager.RELACION_COLUMN_MEDIDA + ","
                            + DBManager.RELACION_COLUMN_CANTIDAD + ") VALUES(?,?,?,?)",
                        new Object[]{r.getId(), i.getNombre().toLowerCase(), i.getMedida(), i.getCantidad()}
                );
            }

            db.setTransactionSuccessful();
        }catch (SQLException exc){
            Log.e(RecipeFacade.class.getName(), "create recipe", exc);
        }finally {
            db.endTransaction();
        }

        String query = "SELECT _id from recipes order by _id DESC limit 1";
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            lastId = c.getInt(0);
        }

        r.setId(lastId);
        return r;
    }

    //removeRecipe: Borra la receta pasada en la BD
    public void removeRecipe(Receta r){
        SQLiteDatabase db = dbManager.getWritableDatabase();

        try{
            db.beginTransaction();
            db.execSQL(
                    "DELETE FROM " + DBManager.RECIPE_TABLE_NAME + " WHERE "
                    + DBManager.RECIPE_COLUMN_ID + "=?", new Object[]{r.getId()}
            );
            db.setTransactionSuccessful();
        }catch (SQLException exc){
            Log.e(RecipeFacade.class.getName(), "remove recipe", exc);
        }finally {
            db.endTransaction();
        }
    }

    //updateRecipe: Actualiza la receta pasada en la BD
    public void updateRecipe(Receta r){
        SQLiteDatabase db = dbManager.getWritableDatabase();

        try{
            db.beginTransaction();
            db.execSQL(
                    "UPDATE "
                    + DBManager.RECIPE_TABLE_NAME + " SET "
                    + DBManager.RECIPE_COLUMN_NAME + "=?, "
                    + DBManager.RECIPE_COLUMN_TYPE + "=?,"
                    + DBManager.RECIPE_COLUMN_NUMCOM + "=?, "
                    + DBManager.RECIPE_COLUMN_PREPARATION + "=?, "
                    + DBManager.RECIPE_COLUMN_PHOTO + "=?"
                    + "WHERE " + DBManager.RECIPE_COLUMN_ID + "=?",
                    new Object[]{r.getId(), r.getNombre(), r.getTipo(),
                            r.getNumComensales(), r.getPreparacion(), r.getRutaFoto()}
            );

            for(Ingrediente i : r.getIngredientes()) {
                db.execSQL("UPDATE "
                        + DBManager.RELACION_TABLE_NAME + " SET "
                        + DBManager.RELACION_COLUMN_ID_RECIPE + "=?, "
                        + DBManager.RELACION_COLUMN_NAME + "=?, "
                        + DBManager.RELACION_COLUMN_MEDIDA + "=?, "
                        + DBManager.RELACION_COLUMN_CANTIDAD + "=? "
                        + "WHERE " + DBManager.RELACION_COLUMN_ID + "=?",
                new Object[]{r.getId(), i.getNombre().toLowerCase()}
                );
            }

            db.setTransactionSuccessful();
        }catch (SQLException exc){
            Log.e(RecipeFacade.class.getName(), "update recipe", exc);
        }
    }

    //getRecipesById: Devuelve un cursor con la receta de la id pasada
    public Receta getRecipesById(int id){
        Cursor cursor =
                dbManager.getReadableDatabase().rawQuery("SELECT * FROM " + DBManager.RECIPE_TABLE_NAME
                + " WHERE " + DBManager.RECIPE_COLUMN_ID + "=?", new String[]{id+""});
        cursor.moveToFirst();

        return readRecipe(cursor);
    }

    //getRecipesById: Devuelve la id mas alta para asignarle la siguiente id a la receta importada
    public int getIdAImportar(){

            String str="SELECT * FROM recipes ORDER BY _id DESC";
            Cursor cursor=dbManager.getReadableDatabase().rawQuery(str,null);

            if(cursor.moveToFirst()) {

                return (cursor.getInt(cursor.getColumnIndex("_id")))+1;
            }
            return 1;
    }
}
