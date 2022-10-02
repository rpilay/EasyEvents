package ec.com.easyevents.adapter;

import android.app.Activity;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import ec.com.easyevents.Prefs;
import ec.com.easyevents.R;
import ec.com.easyevents.entity.DjEntity;

public class DjAdapter extends FirestoreRecyclerAdapter<DjEntity,DjAdapter.ViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;
    FragmentManager fm;
    String idDj;
   // Map<String, Object> map;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public DjAdapter(@NonNull FirestoreRecyclerOptions<DjEntity> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull DjEntity model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        idDj = documentSnapshot.getId(); // captura el id del dj

        holder.nombreDj.setText(model.getNombre());
        holder.apellidoDj.setText(model.getApellido());

        model.getEventos().get("nocturno");
        holder.tvEventoNocturno.setText("$ "+ String.valueOf(model.getEventos().get("nocturno")));
        holder.tvEventoBoda.setText("$ "+ String.valueOf(model.getEventos().get("boda")));
        holder.tvEventoQuince.setText("$ "+ String.valueOf(model.getEventos().get("quince")));



        Picasso.with(activity.getApplicationContext())
                .load(model.getFotoDj())
                .resize(250, 250)
                .into(holder.fotoDj);

        holder.addCarritoDj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Prefs.setDefaultsPreference("idDj",idDj,v.getContext());


                Toast.makeText(activity.getApplicationContext(), "Se ha agregado Dj al carrito de Compras", Toast.LENGTH_SHORT).show();


            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_dj,parent,false);
        return new ViewHolder(v);
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        TextView nombreDj, apellidoDj, tvEventoNocturno, tvEventoBoda, tvEventoQuince;
        ImageView fotoDj, addCarritoDj;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreDj = itemView.findViewById(R.id.tv_nombre_dj);
            apellidoDj = itemView.findViewById(R.id.tv_apellido_dj);
            fotoDj = itemView.findViewById(R.id.iv_foto_dj);
            addCarritoDj = itemView.findViewById(R.id.add_carritoDj);
            tvEventoNocturno = itemView.findViewById(R.id.tv_eventoNocturno);
            tvEventoBoda = itemView.findViewById(R.id.tv_eventoBoda);
            tvEventoQuince = itemView.findViewById(R.id.tv_eventoQuince);
        }
    }
}
