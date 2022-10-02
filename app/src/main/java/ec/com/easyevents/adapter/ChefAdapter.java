package ec.com.easyevents.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import ec.com.easyevents.R;
import ec.com.easyevents.activity.MenuActivity;
import ec.com.easyevents.entity.ChefEntity;

public class ChefAdapter extends FirestoreRecyclerAdapter<ChefEntity,ChefAdapter.ViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;
    FragmentManager fm;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ChefAdapter(@NonNull FirestoreRecyclerOptions<ChefEntity> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChefEntity model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String idChef = documentSnapshot.getId();


        holder.nombreChef.setText(model.getNombre());
        holder.apellidoChef.setText(model.getApellido());


        Picasso.with(activity.getApplicationContext())
                .load(model.getFotoChef())
                .resize(200, 200)
                .into(holder.fotoChef);


        holder.clytMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MenuActivity.class);
                intent.putExtra("idChef", idChef);
                activity.startActivity(intent);
                //activity.finish();
            }
        });

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_chef,parent,false);
        return new ViewHolder(v);

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreChef, apellidoChef;
        ImageView fotoChef, fotoMenu;
        ConstraintLayout clytMenu;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreChef = itemView.findViewById(R.id.tv_nombreChef);
            apellidoChef = itemView.findViewById(R.id.tv_apellidoChef);

            fotoChef = itemView.findViewById(R.id.iv_foto_chef);
            clytMenu = itemView.findViewById(R.id.clyt_menu);



        }
    }

}
