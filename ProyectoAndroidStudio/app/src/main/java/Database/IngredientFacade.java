package Database;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import Model.Ingrediente;

public class IngredientFacade {

    private DBManager dbManager;

    public IngredientFacade(DBManager dbManager){
        this.dbManager = dbManager;
    }

    //readIngrediente: Lee el ingrediente del cursor que se le ha pasado
    public static Ingrediente readIngrediente(Cursor cursor){
        Ingrediente r = new Ingrediente(null,null,null);

        r.setNombre(cursor.getString(cursor.getColumnIndex(DBManager.RELACION_COLUMN_NAME)));
        r.setMedida(cursor.getString(cursor.getColumnIndex(DBManager.RELACION_COLUMN_MEDIDA)));
        r.setCantidad(cursor.getDouble(cursor.getColumnIndex(DBManager.RELACION_COLUMN_CANTIDAD)));

        return r;
    }

    //getIngredient: Devuelve un cursor con todos los ingredientes
    public Cursor getIngredient(){
        Cursor toret = null;
        toret = dbManager.getReadableDatabase().rawQuery("SELECT * FROM " + DBManager.RELACION_TABLE_NAME, null);
        return toret;
    }

    //createIngredient: Se le añade un ingrediente a la BD
    public void createIngredient(Ingrediente r, int id){
        SQLiteDatabase db = dbManager.getWritableDatabase();

        try{
            db.beginTransaction();
            db.execSQL(
                    "INSERT INTO " + DBManager.RELACION_TABLE_NAME +
                            "(" + DBManager.RELACION_COLUMN_ID_RECIPE + "," +
                                DBManager.RELACION_COLUMN_NAME + "," +
                                DBManager.RELACION_COLUMN_MEDIDA + "," +
                                DBManager.RELACION_COLUMN_CANTIDAD +
                             ") VALUES(?,?,?,?)",
                    new Object[]{id,r.getNombre().toLowerCase(), r.getMedida(), r.getCantidad()}
            );
            db.setTransactionSuccessful();
        }catch (SQLException exc){
            Log.e(RecipeFacade.class.getName(), "create ingredient", exc);
        }finally {
            db.endTransaction();
        }
    }

    //removeIngredient: Se le borra un ingrediente a la BD
    public void removeIngredient(Ingrediente r){
        SQLiteDatabase db = dbManager.getWritableDatabase();

        try{
            db.beginTransaction();
            db.execSQL(
                    "DELETE FROM " + DBManager.RELACION_TABLE_NAME + " WHERE "
                            + DBManager.RELACION_COLUMN_NAME + "=?", new Object[]{r.getNombre()}
            );
            db.setTransactionSuccessful();
        }catch (SQLException exc){
            Log.e(RecipeFacade.class.getName(), "remove ingrediente", exc);
        }finally {
            db.endTransaction();
        }
    }

    //updateIngredient: Se actualiza un ingrediente a la BD
    public void updateIngredient(Ingrediente r){
        SQLiteDatabase db = dbManager.getWritableDatabase();

        try{
            db.beginTransaction();
            db.execSQL(
                    "UPDATE "
                            + DBManager.RELACION_TABLE_NAME + " SET "
                            + DBManager.RELACION_COLUMN_NAME + "=?, "
                            + DBManager.RELACION_COLUMN_CANTIDAD + "=?, "
                            + DBManager.RELACION_COLUMN_MEDIDA + "=? "
                            + "WHERE " + DBManager.RELACION_COLUMN_NAME + "=?",
                    new Object[]{r.getNombre().toLowerCase(), r.getMedida(), r.getCantidad()}
            );
            db.setTransactionSuccessful();
        }catch (SQLException exc){
            Log.e(RecipeFacade.class.getName(), "update ingredient", exc);
        }
    }

    //getIngredientsByName: Se le pasa un nombre y se devuelve el ingrediente
    public Ingrediente getIngredientsByName(String name){
        Cursor cursor =
                dbManager.getReadableDatabase().rawQuery("SELECT * FROM " + DBManager.RELACION_TABLE_NAME
                        + " WHERE " + DBManager.RELACION_COLUMN_NAME + "=?", new String[]{name+""});
        cursor.moveToFirst();
        return readIngrediente(cursor);
    }



}
