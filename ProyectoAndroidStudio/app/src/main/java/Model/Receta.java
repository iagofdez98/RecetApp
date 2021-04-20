package Model;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Receta implements Serializable {

    //Id receta
    private Integer id;
    //Nombre receta
    private String nombre;
    //Tipo receta("postre","pasta")
    private String tipo;
    //Numero de comensales de la receta
    private Integer numComensales;
    //Lista de ingredientes de la receta
    private List<Ingrediente> ingredientes;
    //Instrucciones de preparación de la receta
    private String preparacion;
    //Ruta de la foto de la receta
    private String rutaFoto;

    public Receta(String nombre, String tipo, Integer numComensales, String preparacion) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.numComensales = numComensales;
        this.ingredientes = new ArrayList<>();
        this.preparacion = preparacion;
    }

    public Receta(String nombre, String tipo, Integer numComensales, String preparacion, String rutaFoto) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.numComensales = numComensales;
        this.ingredientes = new ArrayList<>();
        this.preparacion = preparacion;
        this.rutaFoto = rutaFoto;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getNumComensales() {
        return numComensales;
    }

    public void setNumComensales(Integer numComensales) {
        this.numComensales = numComensales;
    }

    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<Ingrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public void addIngrediente(Ingrediente ingrediente) {
        this.ingredientes.add(ingrediente);
    }

    public boolean removeIngrediente(Ingrediente ingrediente) {
        return this.ingredientes.remove(ingrediente);
    }

    public String getPreparacion() {
        return preparacion;
    }

    public void setPreparacion(String preparacion) {
        this.preparacion = preparacion;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    //Convierte a JSON el objeto receta
    public void toJSON(File fileURL) throws IOException{
        Gson gson=new Gson();
        String json= gson.toJson(this);

        FileWriter fileWriter = new FileWriter(fileURL);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(json);
        bufferedWriter.close();
    }




    //Recupera el obeto Receta desde JSON
    public void fromJSON(Context context, Uri uri) throws IOException {
        ParcelFileDescriptor pdf = context.getContentResolver().openFileDescriptor(uri, "r");

        assert pdf != null;
        assert pdf.getStatSize() <= Integer.MAX_VALUE;
        byte[] data = new byte[(int) pdf.getStatSize()];

        FileDescriptor fd = pdf.getFileDescriptor();
        FileInputStream fileStream = new FileInputStream(fd);

        final Gson gson = new Gson();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
        Receta receta= gson.fromJson(reader, Receta.class);

        this.setNombre(receta.getNombre());
        this.setTipo(receta.getTipo());
        this.setNumComensales(receta.getNumComensales());
        this.setIngredientes(receta.getIngredientes());
        this.setPreparacion(receta.getPreparacion());


    }


}
