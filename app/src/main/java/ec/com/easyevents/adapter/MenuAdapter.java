package ec.com.easyevents.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import ec.com.easyevents.Prefs;
import ec.com.easyevents.R;
import ec.com.easyevents.entity.MenuEntity;

public class MenuAdapter extends FirestoreRecyclerAdapter<MenuEntity, MenuAdapter.ViewHolder> {

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;
    FragmentManager fm;
    DocumentReference query;
    String idChef ;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MenuAdapter(@NonNull FirestoreRecyclerOptions<MenuEntity> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull MenuEntity model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String idMenu = documentSnapshot.getId();


        holder.plato.setText(model.getPlato());

        // convertir numero a text.
        holder.precio.setText( String.valueOf(model.getPrecio()));


        Picasso.with(activity.getApplicationContext())
                .load(model.getFotoPlato())
                .resize(300, 300)
                .into(holder.fotoPlato);

        holder.addCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prueba con sharedpreference
               Prefs.setDefaultsPreference("idMenu",idMenu,v.getContext());
               Toast.makeText(activity.getApplicationContext(), "Se ha agregado el menú al carrito de Compras", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_menu,parent,false);
        return new MenuAdapter.ViewHolder(v);

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView plato , precio;
        ImageView fotoPlato, addCarrito;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            plato = itemView.findViewById(R.id.tv_menu);
            precio = itemView.findViewById(R.id.tv_precio);
            fotoPlato = itemView.findViewById(R.id.iv_foto_menu);
            addCarrito = itemView.findViewById(R.id.add_carrito);



        }
    }
}
