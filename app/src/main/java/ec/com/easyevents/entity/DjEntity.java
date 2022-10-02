package ec.com.easyevents.entity;

import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;

public class DjEntity {
    private String id;
    private String nombre;
    private String apellido;
    private String fotoDj;
    private String detalle;
    private HashMap<String, Object> eventos;

    public DjEntity() {
    }

    public DjEntity(String id, String nombre, String apellido, String fotoDj, String detalle, HashMap<String, Object> eventos) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fotoDj = fotoDj;
        this.detalle = detalle;
        this.eventos = eventos;
    }

    public HashMap<String, Object> getEventos() {
        return eventos;
    }

    public void setEventos(HashMap<String, Object> eventos) {
        this.eventos = eventos;
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

    public String getFotoDj() {
        return fotoDj;
    }

    public void setFotoDj(String fotoDj) {
        this.fotoDj = fotoDj;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
