package ec.com.easyevents.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.com.easyevents.MainActivity;
import ec.com.easyevents.Prefs;
import ec.com.easyevents.R;
import ec.com.easyevents.entity.UsuarioEntity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
// autenticacion con google https://github.com/firebase/snippets-android/blob/f29858162c455292d3d18c1cc31d6776b299acbd/auth/app/src/main/java/com/google/firebase/quickstart/auth/GoogleSignInActivity.java#L67-L68
// autenticacion con mail https://github.com/firebase/snippets-android/blob/8fbcdaef064ed94d8ee9efc662c00991ff397254/auth/app/src/main/java/com/google/firebase/quickstart/auth/EmailPasswordActivity.java#L49-L57

    TextView google;
    TextView tvregistar;
    Button btnlogin;
    EditText etMailLogin, etPasswordLogin;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    private GoogleSignInClient mGoogleSignInClient;
    String TAG = getClass().getSimpleName();
    int RC_SIGN_IN = 9001;

    // ...
//    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
//    private boolean showOneTapUI = true;
    // ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle("Login");


        setContentView(R.layout.activity_login);
        google = findViewById(R.id.tv_google);
        google.setOnClickListener(this);

        tvregistar = findViewById(R.id.tv_registar);
        tvregistar.setOnClickListener(this);

        btnlogin = findViewById(R.id.btn_login);
        btnlogin.setOnClickListener(this);

        etMailLogin = findViewById(R.id.et_email_login);
        etPasswordLogin = findViewById(R.id.et_password_login);


        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        mFirestore = FirebaseFirestore.getInstance();


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        //--------------------------------------------------------
        if (currentUser != null) {

            updateUI(currentUser);
        }
    }

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }


    }

    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = mAuth.getCurrentUser();

                            buscarUsuario(user);




                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private boolean buscarUsuario(FirebaseUser user) {
        boolean isEncontrado = false;

        Query query = mFirestore.collection("cliente").whereEqualTo("idAuth",user.getUid());
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                List<UsuarioEntity> lstUsuario = value.toObjects(UsuarioEntity.class);
                //if(lstUsuario!=null){
                if(lstUsuario.size()!=0){
                    updateUI(user);
                }else{
                    crearUsuarioLoginGoogle(user);
                }
            }
        });

        return isEncontrado;
    }

    private void crearUsuarioLoginGoogle(FirebaseUser user) {

        //----------------------------


        String name = user.getDisplayName();
        String mail = user.getEmail();
        String id = user.getUid();
        String telefono = user.getPhoneNumber();
        Uri fotoPerfil = user.getPhotoUrl();
        DocumentReference _id = mFirestore.collection("cliente").document();
        Map<String, Object> map = new HashMap<>();
        map.put("id", _id);
        map.put("idAuth", id);
        map.put("nombre", name);
        map.put("mail", mail);
        map.put("telefono", telefono);
        map.put("imagenUrl", fotoPerfil);
        map.put("password"," ");
        map.put("direccion","");
        map.put("ci","");

        mFirestore.collection("cliente")
                .document(_id.getId())
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Usuario Creado", Toast.LENGTH_SHORT).show();

                        updateUI(user);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });


        //---------------
    }
    // [END auth_with_google]


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_google:
                signInGoogle();
                break;
            case R.id.tv_registar:
                registrar();
                break;
            case R.id.btn_login:
                //String nombre = etNombre.getText().toString().trim();
                String mail = etMailLogin.getText().toString().trim();
                String password = etPasswordLogin.getText().toString().trim();
                if (mail.isEmpty() || password.isEmpty())
                    Toast.makeText(this, "Complete todos los datos", Toast.LENGTH_SHORT).show();
                else
                    signIn(mail, password);
                break;
            // ...
        }
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            google.setVisibility(View.GONE);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            google.setVisibility(View.VISIBLE);
        }
    }


    // FIN AUTENTICACION GOOGLE


    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }


    private void registrar() {
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }

//// para agrear el boton de retroceso



}
