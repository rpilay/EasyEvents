package ec.com.easyevents.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.com.easyevents.Prefs;
import ec.com.easyevents.R;
import ec.com.easyevents.entity.UsuarioEntity;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etNombre, etMail, etPassword;
    Button btnRegistrarPersona;

    //Variables globales para firebase
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    String TAG = getClass().getSimpleName();
    String nombre;
    String mail;
    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle("Registrar");

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_registro);
        etNombre = findViewById(R.id.et_nombre);
        etMail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegistrarPersona = findViewById(R.id.btn_registrar_persona);
        btnRegistrarPersona.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_registrar_persona:
                nombre = etNombre.getText().toString().trim();
                mail = etMail.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                FirebaseUser user = mAuth.getCurrentUser();
                if (nombre.isEmpty() || mail.isEmpty() || password.isEmpty())
                    Toast.makeText(this, "Complete todo los datos", Toast.LENGTH_SHORT).show();
                else {
                    if (user!=null)
                        buscarUsuario(user);
                    else {
                        registarConFirebase(mail, password, nombre);


                    }
                }
                break;

            // ...
        }
    }

    private void registarConFirebase(String mail, String password, String nombre) {
        mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCustomToken:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    String id = user.getUid();

                    //bd firebase
                    //String idCliente = mAuth.getCurrentUser().getUid();
                    DocumentReference _id = mFirestore.collection("cliente").document();
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", _id);
                    map.put("nombre", nombre);
                    map.put("mail", mail);
                    map.put("password", password);
                    map.put("idAuth", id);
                    map.put("ci", "");
                    map.put("direccion", "");
                    map.put("telefono", "");
                    map.put("imagenUrl", "");


                    mFirestore.collection("cliente")
                            .document(_id.getId())
                            .set(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext(), "Usuario Creado Exitosamente", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error al Crear Usuario", Toast.LENGTH_SHORT).show();
                                }
                            });

                    //fin bd
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                    Toast.makeText(RegistroActivity.this, "Registro Falló.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                e.printStackTrace();
            }
        });

    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            onBackPressed();
        }
    }

    private boolean buscarUsuario(FirebaseUser user) {
        boolean isEncontrado = false;

        Query query = mFirestore.collection("cliente").whereEqualTo("idAuth", user.getUid());
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                List<UsuarioEntity> lstUsuario = value.toObjects(UsuarioEntity.class);
                if (lstUsuario.size()!=0) {
                    updateUI(user);
                } else {
                    nombre = etNombre.getText().toString().trim();
                    mail = etMail.getText().toString().trim();
                    password = etPassword.getText().toString().trim();
                    //registarConFirebase(user);
                    registarConFirebase(mail, password, nombre);
                }
            }
        });

        return isEncontrado;
    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}