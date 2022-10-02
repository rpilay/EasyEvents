package ec.com.easyevents.entity;

import com.google.firebase.firestore.DocumentReference;

public class MenuEntity {

    private String id;
    private String plato;
    private float precio;
    private String fotoPlato;

    public MenuEntity() {
    }

    public MenuEntity(String id, String plato, float precio, String fotoPlato) {
        this.id = id;
        this.plato = plato;
        this.precio = precio;
        this.fotoPlato = fotoPlato;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlato() {
        return plato;
    }

    public void setPlato(String plato) {
        this.plato = plato;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getFotoPlato() {
        return fotoPlato;
    }

    public void setFotoPlato(String fotoPlato) {
        this.fotoPlato = fotoPlato;
    }
}
