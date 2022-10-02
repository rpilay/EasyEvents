package ec.com.easyevents.entity;

import android.net.Uri;

import com.google.firebase.firestore.DocumentReference;

public class ChefEntity {
    private String id;
    private String nombre;
    private String apellido;
    private String fotoChef;
    private String detalle;


    public ChefEntity() {
    }

    public ChefEntity(String id, String nombre, String apellido,  String detalle, String fotoChef) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fotoChef = fotoChef;
        this.detalle = detalle;


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getFotoChef() {
        return fotoChef;
    }

    public void setFotoChef(String fotoChef) {
        this.fotoChef = fotoChef;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
