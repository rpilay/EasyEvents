package ec.com.easyevents.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ec.com.easyevents.R;
import ec.com.easyevents.adapter.ChefAdapter;
import ec.com.easyevents.adapter.DjAdapter;
import ec.com.easyevents.entity.ChefEntity;
import ec.com.easyevents.entity.DjEntity;

public class DjActivity extends AppCompatActivity {
    RecyclerView rvDj;
    FirebaseFirestore djFirestore;
    Query query;
    DjAdapter djAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dj);


        this.setTitle("Dj");
        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        rvDj = findViewById(R.id.rv_dj);
        djFirestore = FirebaseFirestore.getInstance();

        llenarRv();
    }

    ///--------------------------------------------------
    @SuppressLint("NotifyDataSetChanged")
    private void llenarRv() {
        rvDj.setLayoutManager(new LinearLayoutManager(this));

        query = djFirestore.collection("dj");
        FirestoreRecyclerOptions<DjEntity> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<DjEntity>()
                .setQuery(query, DjEntity.class).build();


        djAdapter = new DjAdapter(firestoreRecyclerOptions, this, getSupportFragmentManager());
        djAdapter.notifyDataSetChanged();
        rvDj.setAdapter(djAdapter);

    }
    @Override
    protected void onStart() {
        super.onStart();
        djAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        djAdapter.stopListening();
    }

    // this event will enable the back
    // function to the button on press

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}