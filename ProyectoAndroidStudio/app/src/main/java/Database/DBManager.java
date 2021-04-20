package Database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import Model.Ingrediente;

public class DBManager extends SQLiteOpenHelper {

    private static DBManager db;

    //Nombres y versiones de las tablas
    private static String RECIPE_DATABASE_NAME = "recipe_db";
    private static int RECIPE_DATABASE_VERSION = 1;


    //Nombres de tablas
    public static final String RECIPE_TABLE_NAME = "recipes";
    public static final String RELACION_TABLE_NAME = "recipe_ing";
    public static final String CARRITO_TABLE_NAME = "carrito";


    //Tabla Recipe
    public static final String RECIPE_COLUMN_ID = "_id";
    public static final String RECIPE_COLUMN_NAME = "name";
    public static final String RECIPE_COLUMN_TYPE = "type";
    public static final String RECIPE_COLUMN_NUMCOM = "num_dinners";
    public static final String RECIPE_COLUMN_PREPARATION = "preparation";
    public static final String RECIPE_COLUMN_PHOTO = "photo";


    //Tabla relacional
    public static final String RELACION_COLUMN_ID = "id_ingrediente";
    public static final String RELACION_COLUMN_ID_RECIPE = "recipe_id";
    public static final String RELACION_COLUMN_NAME = "ingredient_name";
    public static final String RELACION_COLUMN_MEDIDA = "medida";
    public static final String RELACION_COLUMN_CANTIDAD = "cantidad";

    //Tabla carrito
    public static final String CARRITO_COLUMN_ID = "id_carrito";
    public static final String CARRITO_COLUMN_NAME = "name_carrito";
    public static final String CARRITO_COLUMN_CANTIDAD = "cantidad_carrito";
    public static final String CARRITO_COLUMN_MEDIDA = "medida_carrito";



    private DBManager(@Nullable Context context){
        super(context, RECIPE_DATABASE_NAME, null, RECIPE_DATABASE_VERSION);

    }

    public static DBManager getInstance(Context context){
        if(db==null){
            db=new DBManager(context);
        }

        return db;
    }

    //onCreate: Metodo para crear las tablas
    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.beginTransaction();

            db.execSQL("CREATE TABLE IF NOT EXISTS " + RECIPE_TABLE_NAME + "(" +
                            RECIPE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            RECIPE_COLUMN_NAME +  " TEXT NOT NULL," +
                            RECIPE_COLUMN_TYPE + " TEXT NOT NULL," +
                            RECIPE_COLUMN_NUMCOM + " INTEGER NOT NULL," +
                            RECIPE_COLUMN_PREPARATION + " TEXT NOT NULL, " +
                            RECIPE_COLUMN_PHOTO + " TEXT " + ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + RELACION_TABLE_NAME + "(" +
                            RELACION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            RELACION_COLUMN_ID_RECIPE + " TEXT NOT NULL, " +
                            RELACION_COLUMN_NAME + " TEXT NOT NULL, " +
                            RELACION_COLUMN_MEDIDA + " TEXT NOT NULL, " +
                            RELACION_COLUMN_CANTIDAD + " DOUBLE NOT NULL, " +
                            " FOREIGN KEY (recipe_id) REFERENCES RECIPE_TABLE_NAME(id) " +
                            ")");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + CARRITO_TABLE_NAME + "(" +
                    CARRITO_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CARRITO_COLUMN_NAME + " TEXT NOT NULL, " +
                    CARRITO_COLUMN_CANTIDAD + " DOUBLE NOT NULL, " +
                    CARRITO_COLUMN_MEDIDA + " TEXT NOT NULL " + ")");

            db.setTransactionSuccessful();
        }catch(SQLException exc){
            Log.e(DBManager.class.getName(), "onCreate", exc);
        }finally {
            db.endTransaction();
        }
    }

    //Metodo para actualizar las tablas
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBManager.class.getName(), "Upgrading db version to" + oldVersion + "to" + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + RECIPE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RELACION_TABLE_NAME);

        onCreate(db);

    }
}
