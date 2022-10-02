package ec.com.easyevents.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ec.com.easyevents.Prefs;
import ec.com.easyevents.R;
import ec.com.easyevents.adapter.ChefAdapter;
import ec.com.easyevents.adapter.MenuAdapter;
import ec.com.easyevents.entity.ChefEntity;
import ec.com.easyevents.entity.MenuEntity;

public class MenuActivity extends AppCompatActivity {
    RecyclerView rvMenu;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    Query query;
    MenuAdapter menuAdapter;
    String idChef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        this.setTitle("Menú del Chef");
        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        rvMenu = findViewById(R.id.rv_menu);
        mFirestore = FirebaseFirestore.getInstance();


        idChef = getIntent().getStringExtra("idChef");
        Prefs.setDefaultsPreference("idChef",idChef,this);




        llenarRv();

    }

    private void llenarRv() {
        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        //query = mFirestore.collection("menu").whereEqualTo("idChef", idChef);
        query = mFirestore.collection("chefCocinero").document(idChef).collection("menu");
        FirestoreRecyclerOptions<MenuEntity> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<MenuEntity>()
                .setQuery(query, MenuEntity.class).build();
        menuAdapter = new MenuAdapter(firestoreRecyclerOptions, this, getSupportFragmentManager());
        menuAdapter.notifyDataSetChanged();
        rvMenu.setAdapter(menuAdapter);
    }




    @Override
    protected void onStart() {
        super.onStart();
        menuAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        menuAdapter.stopListening();
    }

    // this event will enable the back
    // function to the button on press

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}