package Model;

import java.io.Serializable;

public class Ingrediente implements Serializable {
    //Cantidad Ingrediente
    private Double cantidad;
    //Medida ingrediente(g,ml)
    private String medida;
    //Nombre Ingrediente
    private String nombre;


    public Ingrediente(Double cantidad, String medida, String nombre) {
        this.cantidad = cantidad;
        this.medida = medida;
        this.nombre = nombre;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
