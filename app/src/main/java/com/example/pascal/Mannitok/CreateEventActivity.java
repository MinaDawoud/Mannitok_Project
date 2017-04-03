package com.example.pascal.Mannitok;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.pascal.Mannitok.Requetes.CreerEventRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateEventActivity extends AppCompatActivity {
    public static Activity createEvent;
    String code;
    String titre_cours;

    EditText eventTitle;
    EditText eventType;
    EditText eventDate;
    EditText eventTime;
    EditText eventPlace;
    EditText eventCapacity;
    EditText eventDescription;

    TextView coursCode;
    TextView coursNom;

    Button createEventB;

    RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createEvent = this;
        setContentView(R.layout.activity_add_event);

        //recuperer le cours
        Intent createEvent=getIntent();
        code = createEvent.getStringExtra("code");
        titre_cours = createEvent.getStringExtra("titre");

        eventTitle = (EditText) findViewById(R.id.eventTitle);
        eventType = (EditText) findViewById(R.id.eventType2);
        eventDate = (EditText) findViewById(R.id.eventDate);
        eventTime = (EditText) findViewById(R.id.eventTime);
        eventPlace = (EditText) findViewById(R.id.eventPlace);
        eventCapacity = (EditText) findViewById(R.id.eventCapacity);
        eventDescription= (EditText) findViewById(R.id.eventDescription);

        coursCode = (TextView) findViewById(R.id.coursCode);
        coursCode.setText(code);
        coursNom = (TextView) findViewById(R.id.coursNom);
        coursNom.setText(titre_cours);

        createEventB = (Button) findViewById(R.id.createEventB);
        createEventB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = eventType.getText().toString();
                String titre = eventTitle.getText().toString();
                String description = eventDescription.getText().toString();
                String date = eventDate.getText().toString();
                String heure = eventTime.getText().toString();
                String lieu = eventPlace.getText().toString();
                String capacite = eventCapacity.getText().toString();

                //On crée le response listener qui attend la réponse de l'API
                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            JSONObject jsonResponse = new JSONObject(response);

                            //maintenant qu'on a notre réponse en json, on peut analyser la réponse
                            //va chercher le champ success de la réponse json, qui est soit true ou false
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                Toast.makeText(getApplicationContext(), "Évènement créé", Toast.LENGTH_SHORT).show();
                                //Crée un intent qui ouvre un MainActivity, et on dit à LoginActivity de faire cet intent.
                                Intent mainIntent = new Intent(CreateEventActivity.this, MainActivity.class);
                                CreateEventActivity.this.startActivity(mainIntent);
                                finish();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);
                                builder.setMessage("Veuillez saisir toutes les informations.").setNegativeButton("Réessayer", null).create().show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                CreerEventRequest creerEventRequest = new CreerEventRequest(type, titre, description, date, heure, lieu, capacite, code, responseListener);
                queue = Volley.newRequestQueue(CreateEventActivity.this);
                queue.add(creerEventRequest);

            }
        });

    }
}
