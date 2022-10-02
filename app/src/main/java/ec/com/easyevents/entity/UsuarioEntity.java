package ec.com.easyevents.entity;

import com.google.firebase.firestore.DocumentReference;

public class UsuarioEntity {

    private DocumentReference id;
    private String nombre;
    private String idAuth;
    private String telefono;
    private String direccion;
    private String imagenUrl;
    private String ci;
    // no hay mail ni password por venir info desde el firebase autentication

    //agregar un constructor vacio
    public UsuarioEntity() {


    }

    //agregar un constructor lleno
    public UsuarioEntity(DocumentReference id, String nombre, String idAuth, String telefono, String direccion, String imagenUrl, String ci) {
        this.id = id;
        this.nombre = nombre;
        this.idAuth = idAuth;
        this.telefono = telefono;
        this.direccion = direccion;
        this.imagenUrl = imagenUrl;
        this.ci = ci;
    }


    //crear un getter and setter
    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public DocumentReference getId() {
        return id;
    }

    public void setId(DocumentReference id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdAuth() {
        return idAuth;
    }

    public void setIdAuth(String idAuth) {
        this.idAuth = idAuth;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
