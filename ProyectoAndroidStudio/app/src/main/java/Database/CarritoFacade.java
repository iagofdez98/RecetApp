package Database;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import Model.Ingrediente;


//Clase CarritoFacade: Clase correspondiente a las operaciones en la BD de la tabla Carrito
public class CarritoFacade {

    private DBManager dbManager;

    public CarritoFacade(DBManager dbManager){
        this.dbManager = dbManager;
    }

    //readIngredienteCarrito: Lee el ingrediente pasado por el cursor
    public static Ingrediente readIngredienteCarrito(Cursor cursor){
        Ingrediente r = new Ingrediente(null,null,null);

        r.setNombre(cursor.getString(cursor.getColumnIndex(DBManager.CARRITO_COLUMN_NAME)));
        r.setMedida(cursor.getString(cursor.getColumnIndex(DBManager.CARRITO_COLUMN_MEDIDA)));
        r.setCantidad(cursor.getDouble(cursor.getColumnIndex(DBManager.CARRITO_COLUMN_CANTIDAD)));

        return r;
    }

    //getCarrito: Devuelve un cursor con todos los ingredientes del carrito
    public Cursor getCarrito(){
        Cursor toret = null;
        toret = dbManager.getReadableDatabase().rawQuery("SELECT * FROM " + DBManager.CARRITO_TABLE_NAME, null);
        return toret;
    }

    //createIngredientCarrito: Se pasa un ingrediente y se añade a la tabla de la BD
    //En caso de que el ingrediente ya estuviese en la BD, añade la cantidad a la ya estipulada
    public void createIngredientCarrito(Ingrediente r){
        SQLiteDatabase db = dbManager.getWritableDatabase();
        Cursor cursor=getIngredienteCarrito(r.getNombre());

        boolean insert=true;
        if(cursor.moveToFirst()){
            do{
                if(r.getMedida().equals(cursor.getString(cursor.getColumnIndex("medida_carrito")))) {
                    insert=false;
                }

            cursor.moveToNext();
            }while(!cursor.isAfterLast() && insert);
        }

        if(insert) {

            try {
                db.beginTransaction();
                db.execSQL(
                        "INSERT INTO " + DBManager.CARRITO_TABLE_NAME +
                                "(" + DBManager.CARRITO_COLUMN_NAME + "," +
                                DBManager.CARRITO_COLUMN_MEDIDA + "," +
                                DBManager.CARRITO_COLUMN_CANTIDAD + ") VALUES(?,?,?)",
                        new Object[]{r.getNombre().toLowerCase(), r.getMedida(), r.getCantidad()}
                );
                db.setTransactionSuccessful();
            } catch (SQLException exc) {
                Log.e(RecipeFacade.class.getName(), "create carrito", exc);
            } finally {
                db.endTransaction();
            }
        }else{

            cursor.moveToPrevious();
            double cantidad = cursor.getDouble(cursor.getColumnIndex("cantidad_carrito"));

            try {
                db.beginTransaction();
                db.execSQL(
                        "UPDATE "
                                + DBManager.CARRITO_TABLE_NAME + " SET "
                                + DBManager.CARRITO_COLUMN_NAME + "=?, "
                                + DBManager.CARRITO_COLUMN_MEDIDA + "=?, "
                                + DBManager.CARRITO_COLUMN_CANTIDAD + "=? "
                                + "WHERE " + DBManager.CARRITO_COLUMN_NAME + "=?" + " AND " + DBManager.CARRITO_COLUMN_MEDIDA + "=?",
                        new Object[]{r.getNombre().toLowerCase(), r.getMedida(), r.getCantidad()+cantidad,r.getNombre(),r.getMedida()});
                db.setTransactionSuccessful();
            } catch (SQLException exc) {
                Log.e(RecipeFacade.class.getName(), "create carrito", exc);
            } finally {
                db.endTransaction();
            }
        }


    }

    //removeIngredientCarrito: Se le pasa el ingrediente a borrar de la BD
    public void removeIngredientCarrito(Ingrediente r){
        SQLiteDatabase db = dbManager.getWritableDatabase();

        try{
            db.beginTransaction();
            db.execSQL(
                    "DELETE FROM " + DBManager.CARRITO_TABLE_NAME + " WHERE "
                            + DBManager.CARRITO_COLUMN_NAME + "=?" + " AND " + DBManager.CARRITO_COLUMN_MEDIDA + "=?", new Object[]{r.getNombre(),r.getMedida()}
            );
            db.setTransactionSuccessful();
        }catch (SQLException exc){
            Log.e(RecipeFacade.class.getName(), "remove ingrediente carrito", exc);
        }finally {
            db.endTransaction();
        }
    }

    //removeCarrito: Borra todos los ingredientes del carrito.
    public void removeCarrito(){
        SQLiteDatabase db = dbManager.getWritableDatabase();

        try{
            db.beginTransaction();
            db.execSQL(
                    "DELETE FROM " + DBManager.CARRITO_TABLE_NAME , new Object[]{}
            );
            db.setTransactionSuccessful();
        }catch (SQLException exc){
            Log.e(RecipeFacade.class.getName(), "remove carrito", exc);
        }finally {
            db.endTransaction();
        }
    }

    //getIngredienteCarrito: Pasandose el nombre (la clave primaria en este caso) devuelve el ingrediente
    public Cursor getIngredienteCarrito(String name){
        Cursor cursor =
                dbManager.getReadableDatabase().rawQuery("SELECT * FROM " + DBManager.CARRITO_TABLE_NAME
                        + " WHERE " + DBManager.CARRITO_COLUMN_NAME + "=?", new String[]{name+""});
        cursor.moveToFirst();

        return cursor;
    }
}
