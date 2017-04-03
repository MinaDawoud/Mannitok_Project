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
import com.example.pascal.Mannitok.Requetes.EditEventRequest;
import com.example.pascal.Mannitok.Requetes.GetEventRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class ModifyEventActivity extends AppCompatActivity {
    public static Activity modifyEvent;
    String code;
    String titre_cours;
    String eventID;
    Button changeEventB;

    TextView coursCode;
    TextView coursNom;


    EditText eventTitle;
    EditText eventType;
    EditText eventDate;
    EditText eventTime;
    EditText eventPlace;
    EditText eventCapacity;
    EditText eventDescription;



    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modifyEvent = this;
        setContentView(R.layout.activity_change_event);


        //recuperer le cours
        Intent modifyEventActivity=getIntent();
        code = modifyEventActivity.getStringExtra("code");
        titre_cours = modifyEventActivity.getStringExtra("titre_cours");
        eventID = modifyEventActivity.getStringExtra("eventID");

        coursCode = (TextView) findViewById(R.id.coursCode);
        coursCode.setText(code);
        coursNom = (TextView) findViewById(R.id.coursNom);
        coursNom.setText(titre_cours);

        eventTitle = (EditText) findViewById(R.id.eventTitle);
        eventType = (EditText) findViewById(R.id.eventType);
        eventDate = (EditText) findViewById(R.id.eventDate);
        eventTime = (EditText) findViewById(R.id.eventTime);
        eventPlace = (EditText) findViewById(R.id.eventPlace);
        eventCapacity = (EditText) findViewById(R.id.eventCapacity);
        eventDescription = (EditText) findViewById(R.id.eventDescription);



        //On récupère les informations pour peupler les champs
        //On crée le response listener qui attend la réponse de l'API
        Response.Listener<String> responseListenerGetEvent = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    JSONObject jsonResponse = new JSONObject(response);

                    Iterator<?> keys = jsonResponse.keys();

                    while( keys.hasNext() ) {
                        String key = (String) keys.next();
                        if (jsonResponse.get(key) instanceof JSONObject) {
                            JSONObject row = (JSONObject) jsonResponse.get(key);

                            eventDate.setText(row.getString("date"));
                            eventTime.setText(row.getString("heure"));
                            eventTitle.setText(row.getString("titre"));
                            eventType.setText(row.getString("type"));
                            eventDescription.setText(row.getString("description"));
                            eventPlace.setText(row.getString("lieu"));
                            eventCapacity.setText(row.getString("capacite"));

                        } else {
                            Toast.makeText(ModifyEventActivity.this, "marche pas", Toast.LENGTH_SHORT).show();
                        }
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        GetEventRequest getEventRequest= new GetEventRequest(eventID, responseListenerGetEvent);
        queue = Volley.newRequestQueue(ModifyEventActivity.this);
        queue.add(getEventRequest);




        changeEventB = (Button) findViewById(R.id.changeEventB);
        changeEventB.setOnClickListener(new View.OnClickListener() {
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
                                Toast.makeText(getApplicationContext(), "Évènement modifié avec succès", Toast.LENGTH_SHORT).show();
                                //Crée un intent qui ouvre un MainActivity
                                Intent mainIntent = new Intent(ModifyEventActivity.this, MainActivity.class);
                                ModifyEventActivity.this.startActivity(mainIntent);
                                finish();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ModifyEventActivity.this);
                                builder.setMessage("Veuillez saisir toutes les informations.").setNegativeButton("Réessayer", null).create().show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                EditEventRequest editEventRequest= new EditEventRequest(titre, type, description, date, heure, lieu, capacite, eventID, responseListener);
                queue = Volley.newRequestQueue(ModifyEventActivity.this);
                queue.add(editEventRequest);

            }
        });
    }
}
