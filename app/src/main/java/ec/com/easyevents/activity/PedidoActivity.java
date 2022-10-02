package ec.com.easyevents.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ec.com.easyevents.Prefs;
import ec.com.easyevents.R;
import ec.com.easyevents.dialog.DateDialog;
import ec.com.easyevents.dialog.TimeDialog;
import ec.com.easyevents.entity.DjEntity;
import ec.com.easyevents.entity.UsuarioEntity;

public class PedidoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        View.OnClickListener {
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    Query query;
    EditText etFecha, etHora, etInvitados;
    int numInvitados = 0;

    float precioDj, precioPlato = 0, subTotal = 0, total = 0, IVA = (float) 0.12, precioPedido = 0, impuestoDj=0;
    TextView tvNombrePedido, tvTelefonoPedido, tvDireccionPedido, tvCiClientePedido;
    TextView tvPrecioMenuPedido, tvPrecioDjPedido, tvMenuPedido, tvDjPedido;
    String idCliente, idMenu, idDj, idChef, fecha, hora;
    TextView tvTipoEvento,tvSubtotal,tvTotal,tvIva;

    Button btnPagar, btnAnular;

    Map<String, Object> map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        map = new HashMap<>();


        etFecha = findViewById(R.id.et_fecha);
        etFecha.setOnClickListener(this);

        etHora = findViewById(R.id.et_hora);
        etHora.setOnClickListener(this);

        tvNombrePedido = findViewById(R.id.tv_nombre_pedido);
        tvTelefonoPedido = findViewById(R.id.tv_telefono_pedido);
        tvDireccionPedido = findViewById(R.id.tv_direccion_pedido);
        tvCiClientePedido = findViewById(R.id.tv_ci_cliente);
        tvMenuPedido = findViewById(R.id.tv_menu_pedido);
        tvDjPedido = findViewById(R.id.tv_dj_pedido);
        tvPrecioMenuPedido = findViewById(R.id.tv_precio_menu_pedido);
        tvPrecioDjPedido = findViewById(R.id.tv_precio_dj_pedido);

        tvTipoEvento = findViewById(R.id.tv_tipo_evento);
        tvTipoEvento.setOnClickListener(this);

        etInvitados = findViewById(R.id.et_invitados);
        etInvitados.setOnClickListener(this);

        tvSubtotal=findViewById(R.id.tv_subtotal);
        tvTotal=findViewById(R.id.tv_total);
        tvIva=findViewById(R.id.tv_iva);

        btnPagar=findViewById(R.id.btn_pagar);
        btnAnular=findViewById(R.id.btn_anular);


        btnPagar.setOnClickListener(this);
        btnAnular.setOnClickListener(this);


        this.setTitle("Pedido");
        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);


        //capturar el id del cliente


        llenarCamposPedido();




    }

    private void calcularValores() {

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);

        subTotal=precioPedido+precioDj;
        total = subTotal+impuestoDj;

        tvSubtotal.setText(String.valueOf(subTotal));
        tvIva.setText(df.format(impuestoDj));
        tvTotal.setText(String.valueOf(total));



    }

    private void llenarCamposPedido() {

        idCliente = Prefs.getDefaultsPreference("idCliente", this);
        idMenu = Prefs.getDefaultsPreference("idMenu", this);
        idDj = Prefs.getDefaultsPreference("idDj", this);
        idChef = Prefs.getDefaultsPreference("idChef", this);

        mFirestore.collection("cliente")
                .whereEqualTo("idAuth", idCliente)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if (Objects.equals(document.getString("nombre"), "") || Objects.equals(document.getString("direccion"), "") || Objects.equals(document.getString("telefono"), "")) {
                                    Toast.makeText(getApplicationContext(), "Debe Completar todos los datos en su Perfil ", Toast.LENGTH_SHORT).show();
                                } else {
                                    tvNombrePedido.setText(document.getString("nombre"));
                                    tvTelefonoPedido.setText(document.getString("telefono"));
                                    tvDireccionPedido.setText(document.getString("direccion"));
                                    tvCiClientePedido.setText(document.getString("ci"));


                                    map.put("idCliente", document.getId());
                                    map.put("nombreCliente", document.getString("nombre"));
                                    map.put("telefonoCliente", document.getString("telefono"));
                                    map.put("direccionCliente", document.getString("direccion"));
                                    map.put("ciCliente", document.getString("ci"));

                                }
                            }
                        }
                    }

                });





    }

    private void llenarDetalle() {
        numInvitados = Integer.parseInt(etInvitados.getText().toString());

        if (numInvitados < 15) {
            numInvitados = 15;
            Toast.makeText(this, "El Número de Invitados debe ser mayor o igual a 15", Toast.LENGTH_SHORT).show();
            etInvitados.setText(String.valueOf(numInvitados));

        }

        if (!Objects.equals(idMenu, "")) {

            DocumentReference documentReference = mFirestore.collection("chefCocinero").document(idChef).collection("menu").document(idMenu);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            tvMenuPedido.setText(document.getString("plato"));
                            precioPlato = Objects.requireNonNull(document.getDouble("precio")).floatValue();
                            precioPedido = precioPlato * numInvitados;

                            tvPrecioMenuPedido.setText(String.valueOf(precioPedido));

                            map.put("idChef", idChef);
                            map.put("idMenu", idMenu);
                            map.put("plato", document.getString("plato"));
                            map.put("precioPlato", precioPlato);
                            map.put("precioPedido", precioPedido);
                            map.put("invitados", numInvitados);
                            calcularValores();


                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });
        }



    }

    private void llenarDetalleDj() {
        if (!Objects.equals(idDj, "")) {

            DocumentReference documentReference = mFirestore.collection("dj").document(idDj);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            tvDjPedido.setText("Dj " + document.getString("nombre"));

                            DjEntity djEntity = document.toObject(DjEntity.class);

                            HashMap<String, Object> evento = djEntity.getEventos();

                            if (Prefs.getDefaultsPreference("evento", getApplicationContext()) == "boda") {
                                precioDj = Float.parseFloat(evento.get("boda").toString());
                            } else if (Prefs.getDefaultsPreference("evento", getApplicationContext()) == "nocturno") {
                                precioDj = Float.parseFloat(evento.get("nocturno").toString());
                            } else {
                                precioDj = Float.parseFloat(evento.get("quince").toString());
                            }

                            // String boda = evento.get("boda").toString();

                            tvPrecioDjPedido.setText(String.valueOf(precioDj));

                            impuestoDj= (IVA*precioDj);


                            map.put("idDj", idDj);
                            map.put("impuesto",impuestoDj);
                            map.put("precioDj",precioDj);
                            calcularValores();


                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        //captura la informacion de la fecha y la muestra en pantalla
        Calendar c = Calendar.getInstance();
        c.set(year, month, dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy");
        etFecha.setText(format.format(c.getTime()));
        fecha = format.format(c.getTime());
        map.put("fechaEvento", fecha);

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //captura la informacion de la hora y la muestra en pantalla
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm a");
        etHora.setText(format.format(c.getTime()));
        hora = format.format(c.getTime());
        map.put("horaEvento", hora);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_fecha:
                new DateDialog().show(getFragmentManager(), "Fecha");
                break;
            case R.id.et_hora:
                new TimeDialog().show(getFragmentManager(), "Hora");
                break;
            case R.id.et_invitados:
                llenarDetalle();
                break;
            case R.id.tv_tipo_evento:
                createSingleListDialog();
                break;
            case R.id.btn_anular:
                borrarPedido();
                finish();
                break;
            case R.id.btn_pagar:
                pagarPedido();
                break;

        }
    }

    private void borrarPedido() {

        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.clear();
        editor.apply();
    }

    private void pagarPedido() {

        if (tvDireccionPedido.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(),"Verifique los datos del Perfil. ",Toast.LENGTH_SHORT).show();
        }else
        if (etFecha.getText().toString().equals("") || etHora.getText().toString().equals("")){

            Toast.makeText(getApplicationContext(),"Debe Seleccionar Fecha y Hora del Evento ",Toast.LENGTH_SHORT).show();

        }else  if (tvTipoEvento.getText() =="" && !Objects.equals(idDj, "")){
            Toast.makeText(getApplicationContext(),"Debe Seleccionar el Tipo de Evento ",Toast.LENGTH_SHORT).show();
        }else if (numInvitados==0 && !Objects.equals(idMenu, "")){
            Toast.makeText(getApplicationContext(),"Debe Seleccionar la Cantidad de Invitados ",Toast.LENGTH_SHORT).show();
        }
        else {
            mFirestore.collection("pedidosCLientes")
                    .document()
                    .set(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Su pedido ha sido creado y pagado correctamente", Toast.LENGTH_SHORT).show();
                            borrarPedido();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error al crear su pedido", Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }


    public AlertDialog createSingleListDialog() {


        final CharSequence[] items = new CharSequence[3];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        items[0] = "Cumpleaños y otras Festividades";
        items[1] = "Matrimonio";
        items[2] = "Fiesta de 15 años";

        builder.setTitle("Eventos")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        Toast.makeText(
                                        getApplicationContext(),
                                        "Seleccionaste:" + items[item],
                                        Toast.LENGTH_SHORT)
                                .show();
                        tvTipoEvento.setText(items[item]);
                        if (items[item] == "Cumpleaños y otras Festividades") {
                            map.put("evento", "nocturno");
                        } else if (items[item] == "Matrimonio") {
                            map.put("evento", "boda");
                        } else {
                            map.put("evento", "quince");

                        }
                        Prefs.setDefaultsPreference("evento", map.get("evento").toString(), getApplicationContext());
                        llenarDetalleDj();
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        return alertDialog;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}