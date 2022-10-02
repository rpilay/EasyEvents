package ec.com.easyevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import ec.com.easyevents.activity.ChefActivity;
import ec.com.easyevents.activity.DjActivity;
import ec.com.easyevents.activity.LoginActivity;
import ec.com.easyevents.activity.PedidoActivity;
import ec.com.easyevents.activity.PerfilActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    FloatingActionButton carrito;
    TextView tvLogout, tvProfile;
    ConstraintLayout clytChef, clytDj;
    LinearLayout lytPerfil, lytLogout;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lytLogout = findViewById(R.id.lyt_logout);
        lytLogout.setOnClickListener(this);

        lytPerfil= findViewById(R.id.lyt_perfil);
        lytPerfil.setOnClickListener(this);

        clytChef = findViewById(R.id.clyt_chef);
        clytChef.setOnClickListener(this);

        clytDj= findViewById( R.id.clyt_dj);
        clytDj.setOnClickListener(this);

        carrito = findViewById(R.id.carrito);
        carrito.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        //String id1 = mAuth.getCurrentUser().getUid();

        //Log.e("TAG",id1);

        this.setTitle("Menú Principal");



        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);


        Prefs.setDefaultsPreference("idCliente",mAuth.getCurrentUser().getUid(), this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lyt_logout:
                signOut();
                break;
            case R.id.lyt_perfil:
                perfil();
                break;
            case R.id.clyt_chef:
                chef();
                break;
            case R.id.clyt_dj:
                dj();
                break;
            case  R.id.carrito:
                carrito();
                break;
        }

    }

    private void carrito() {
        Intent intent = new Intent(MainActivity.this, PedidoActivity.class);
        startActivity(intent);
        //finish();
    }

    private void dj() {
        Intent intent2 = new Intent(MainActivity.this, DjActivity.class);
        startActivity(intent2);
        //finish();
    }

    private void chef() {
        Intent intent = new Intent(MainActivity.this, ChefActivity.class);
        startActivity(intent);
        //finish();
    }

    private void perfil() {
        String idUsuario = mAuth.getCurrentUser().getUid();
        Prefs.setDefaultsPreference("idCliente",mAuth.getCurrentUser().getUid(), this);

        Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
        intent.putExtra("codUsuario",idUsuario);
        startActivity(intent);
        //finish();
    }


    public void signOut() {
        // [START auth_sign_out]
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        // [END auth_sign_out]
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