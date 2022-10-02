package ec.com.easyevents.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import ec.com.easyevents.Prefs;
import ec.com.easyevents.R;
import ec.com.easyevents.dialog.SimpleDialog;
import ec.com.easyevents.entity.UsuarioEntity;

public class PerfilActivity extends AppCompatActivity implements View.OnClickListener,
        SimpleDialog.OnSimpleDialogListener {

    CircleImageView civFotoPerfil;
    EditText etNombrePerfil, etCiPerfil, etDireccion, etTelefono;
    ImageView ivAddFoto, ivBorrarFoto;
    int COD_SEL_IMAGE = 14;
    Uri imageUrl;
    String idUsuario;

    ProgressDialog progressDialog;
    String rutaAlmacenamiento = "imagenes/";
    String photo = "photo";
    StorageReference storageReference;


    Button btnActualizar;

    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    FirebaseUser user;
    String idDocumento; // captura el id del cliente

    String rutaAlmacenamientoFoto;

    List<UsuarioEntity> lstUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        civFotoPerfil = findViewById(R.id.foto_perfil);
        etNombrePerfil = findViewById(R.id.et_nombre_perfil);

        etCiPerfil = findViewById(R.id.et_ci_perfil);
        etDireccion = findViewById(R.id.et_direccion);
        etTelefono = findViewById(R.id.et_telefono);

        ivAddFoto = findViewById(R.id.iv_add_foto);
        ivAddFoto.setOnClickListener(this);
        ivBorrarFoto = findViewById(R.id.iv_borrar_foto);
        ivBorrarFoto.setOnClickListener(this);

        btnActualizar = findViewById(R.id.btn_actualizar);
        btnActualizar.setOnClickListener(this);

        this.setTitle("Perfil");


        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);


        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        idUsuario = getIntent().getStringExtra("codUsuario");
        if (idUsuario != null && idUsuario.length() > 1) {
            buscarUsuario(idUsuario);

        }

        if (user != null) {
            // Name, email address, and profile photo Url

            String email = user.getEmail();
            Toast.makeText(PerfilActivity.this, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();

        }

    }
///******


    private boolean buscarUsuario(String idUsuario) {
        boolean isEncontrado = false;

        Query query = mFirestore.collection("cliente").whereEqualTo("idAuth", idUsuario);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    return;
                }


                lstUsuario = value.toObjects(UsuarioEntity.class);
                if (lstUsuario != null) {
                    etNombrePerfil.setText(lstUsuario.get(0).getNombre());
                    etCiPerfil.setText(lstUsuario.get(0).getCi());
                    etDireccion.setText(lstUsuario.get(0).getDireccion());
                    etTelefono.setText(lstUsuario.get(0).getTelefono());

                    // preguntar cargar foto
                    String picFoto = lstUsuario.get(0).getImagenUrl();
                    if (!picFoto.equals("")) {
                        Picasso.with(PerfilActivity.this)
                                .load(picFoto)
                                .resize(200, 200)
                                .into(civFotoPerfil);
                    }


                    DocumentReference registroActualizar = lstUsuario.get(0).getId();
                    idDocumento = registroActualizar.getId();



                }
            }
        });

        return isEncontrado;
    }

    //-------------------------

    public void signOut() {
        // [START auth_sign_out]
        FirebaseAuth.getInstance().signOut();
        // [END auth_sign_out]
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_foto:
                subirFoto();
                break;
            case R.id.iv_borrar_foto:
                //borrarFoto();
                new SimpleDialog().show(getFragmentManager(), "SimpleDialog");
                break;
            case R.id.btn_actualizar:
                actualizarDatos();

                break;
        }

    }


    private void actualizarDatos() {
        //String nombrePerfil;


        String nombrePerfil = etNombrePerfil.getText().toString().trim();
        String ciPerfil = etCiPerfil.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();


        if (nombrePerfil.isEmpty() || direccion.isEmpty() || ciPerfil.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Se requieren todos los canpos", Toast.LENGTH_SHORT).show();
            return;
        }

        //bd firebase
        //String idCliente = mAuth.getCurrentUser().getUid();


        Map<String, Object> map = new HashMap<>();
        map.put("ci", ciPerfil);
        map.put("nombre", nombrePerfil);
        map.put("direccion", direccion);
        map.put("telefono", telefono);


        mFirestore.collection("cliente")
                .document(idDocumento)
                .update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Perfil Actualizado", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });

        //fin bd
    }


    //imagen
    private void subirFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/");
        startActivityForResult(intent, COD_SEL_IMAGE);

    }

    private void borrarFoto() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("imagenUrl", "");
        mFirestore.collection("cliente")
                .document(idDocumento)
                .update(map);
        rutaAlmacenamientoFoto = rutaAlmacenamiento + "" + photo + "" + mAuth.getUid() + "" + idUsuario;
        StorageReference reference = storageReference.child(rutaAlmacenamientoFoto);
        reference.delete();
        civFotoPerfil.setImageResource(R.drawable.logo_sin_fondo);
        Toast.makeText(PerfilActivity.this, "Foto Eliminada", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == COD_SEL_IMAGE) {
                imageUrl = data.getData();
                grabarImagen(imageUrl);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void grabarImagen(Uri imageUrl) {
        progressDialog.setMessage("Actualizando foto...");
        progressDialog.show();

        //String rutaAlmacenamientoFoto = rutaAlmacenamiento+""+photo+""+mAuth.getUid()+""+idUsuario;
        rutaAlmacenamientoFoto = rutaAlmacenamiento + "" + photo + "" + mAuth.getUid() + "" + idUsuario;
        StorageReference reference = storageReference.child(rutaAlmacenamientoFoto);
        Picasso.with(PerfilActivity.this)
                .load(imageUrl)
                .resize(200, 200)
                .into(civFotoPerfil);
        reference.putFile(imageUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) {
                            if (uriTask.isSuccessful()) {
                                uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri imagenDescargada = uri;
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("imagenUrl", imagenDescargada);
                                        progressDialog.dismiss();
                                        mFirestore.collection("cliente")
                                                .document(idDocumento)
                                                .update(map);
                                        Toast.makeText(PerfilActivity.this, "Foto Actualizada", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PerfilActivity.this, "Error al cargar la foto", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onPossitiveButtonClick() {

        borrarFoto();
    }

    @Override
    public void onNegativeButtonClick() {

    }
}