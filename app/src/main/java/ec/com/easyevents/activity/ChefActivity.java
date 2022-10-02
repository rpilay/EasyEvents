package ec.com.easyevents.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ec.com.easyevents.R;
import ec.com.easyevents.adapter.ChefAdapter;
import ec.com.easyevents.entity.ChefEntity;

public class ChefActivity extends AppCompatActivity {
    RecyclerView rvchef;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    Query query;
    ChefAdapter chefAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef);


        this.setTitle("Chef");
        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        rvchef = findViewById(R.id.rv_chef);
        mFirestore = FirebaseFirestore.getInstance();

        llenarRv();




    }
    ///--------------------------------------------------
    @SuppressLint("NotifyDataSetChanged")
    private void llenarRv() {
        //rvchef.setLayoutManager(new LinearLayoutManager(this));
        rvchef.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        query = mFirestore.collection("chefCocinero");
        FirestoreRecyclerOptions<ChefEntity> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<ChefEntity>()
                .setQuery(query, ChefEntity.class).build();


        chefAdapter = new ChefAdapter(firestoreRecyclerOptions, this, getSupportFragmentManager());
        chefAdapter.notifyDataSetChanged();
        rvchef.setAdapter(chefAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        chefAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        chefAdapter.stopListening();
    }

    // this event will enable the back
    // function to the button on press

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // AGREGAR ESTO EN LOS RV Y CAMBIAR LINEA DEL setLayoutManager LINEA #56
    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
    //... constructor2

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }


        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("TAG", "meet a IOOBE in RecyclerView");
            }
        }
    }
}