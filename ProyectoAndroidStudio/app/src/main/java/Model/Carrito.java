package Model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class Carrito implements Iterable<Ingrediente>  {
    //Lista de ingredientes que conforman el carrito
    List<Ingrediente> carrito;

    public Carrito(){
        carrito=new ArrayList<>();
    }

    @NonNull
    @Override
    public Iterator<Ingrediente> iterator() {
        return carrito.iterator();
    }

    //Añade un ingrediente al carrito
    public void addIngrediente(Ingrediente in){
        carrito.add(in);
    }

    //Elimina un ingrediente del carrito
    public boolean RemoveIngrediente(Ingrediente in){
        return carrito.remove(in);
    }

    //Elimina un ingrediente del carrito
    public boolean removeIngredientePorNombre(String in){
        Ingrediente toDelete=new Ingrediente((Double) 0.0,"","");
        for (Ingrediente aux:carrito){
            if(aux.getNombre().toUpperCase().equals(in.toUpperCase())){
                toDelete=aux;
            }
        }
            return carrito.remove(toDelete);
    }

    public Ingrediente getIngredienteByName(String name,String medida){
        Ingrediente toRet=new Ingrediente((Double) 0.0,"","");
        for (Ingrediente aux:carrito){
            if(aux.getNombre().toUpperCase().equals(name.toUpperCase()) && aux.getMedida().equals(medida)){
                toRet=aux;
            }
        }
       return toRet;
    }

    public void resetCarrito(){
        this.carrito=new ArrayList<>();
    }

    public int getSize(){
        return carrito.size();
    }

    public Ingrediente get(int position){
        return carrito.get(position);
    }


}
