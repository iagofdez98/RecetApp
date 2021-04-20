package Model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Recetario implements Iterable<Receta> {
    //Contenedor de recetas
    private List<Receta> recetario;

    public Recetario() {
        this.recetario = new ArrayList<>();
    }


    @NonNull
    @Override
    public Iterator<Receta> iterator() {
        return this.recetario.iterator();
    }

    public void addReceta(Receta receta){
        this.recetario.add(receta);
    }

    public boolean removeReceta(Receta receta){
        return this.recetario.remove(receta);
    }

    //Elimina una receta pasándole una id de la misma como referencia
    public boolean removeRecetaById(int id){
        Receta toDelete=new Receta("","",3,"");
        for(Receta r : recetario){
            if(r.getId()==id){
             toDelete=r;
            }
        }
        if(toDelete.getNombre().length()!=0){
            return this.removeReceta(toDelete);
        }
        return false;
    }



    public int getSize(){
        return this.recetario.size();
    }

    public Receta get(int i){
        return recetario.get(i);
    }

    public List<Receta> getRecetarioList(){
        return this.recetario;
    }

}
