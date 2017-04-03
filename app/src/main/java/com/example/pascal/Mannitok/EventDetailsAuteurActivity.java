package com.example.pascal.Mannitok;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.pascal.Mannitok.Requetes.DeleteEventRequest;
import com.example.pascal.Mannitok.Requetes.GetEventRequest;
import com.example.pascal.Mannitok.Requetes.ListerParticipantsRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class EventDetailsAuteurActivity extends AppCompatActivity {
    public static Activity eventDetailsAuteur;
    String code;
    String titre_cours;
    String eventID;

    TextView coursCode;
    TextView eventDate;
    TextView heure;
    TextView coursNom;
    TextView nomEvenement;
    TextView typeEvenement;
    TextView author;
    TextView description;
    TextView eventPlace;
    TextView eventCapacity;

    ListView persons;

    Button shareB;
    Button modifyB;
    Button supprimerB;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventDetailsAuteur = this;
        setContentView(R.layout.details_modify_layout);


        //recuperer le cours
        Intent eventDetailsNotAuteur=getIntent();
        code = eventDetailsNotAuteur.getStringExtra("code");
        titre_cours = eventDetailsNotAuteur.getStringExtra("titre_cours");
        eventID = eventDetailsNotAuteur.getStringExtra("eventID");

        coursCode = (TextView) findViewById(R.id.coursCode);
        eventDate = (TextView) findViewById(R.id.eventDate);
        heure = (TextView) findViewById(R.id.heure);
        coursNom = (TextView) findViewById(R.id.coursNom);
        nomEvenement = (TextView) findViewById(R.id.nomEvenement);
        typeEvenement = (TextView) findViewById(R.id.typeEvenement);
        author = (TextView) findViewById(R.id.author);
        description = (TextView) findViewById(R.id.description);
        eventPlace = (TextView) findViewById(R.id.eventPlace);
        eventCapacity = (TextView) findViewById(R.id.eventCapacity);


        modifyB = (Button) findViewById(R.id.modifyB);
        modifyB.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent coursEventIntent = new Intent(EventDetailsAuteurActivity.this ,ModifyEventActivity.class);
                coursEventIntent.putExtra("code", code);
                coursEventIntent.putExtra("titre_cours", titre_cours);
                coursEventIntent.putExtra("eventID", eventID);
                startActivity(coursEventIntent);

            }


        });

        shareB = (Button) findViewById(R.id.shareB);
        shareB.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Share Event
                String msg = "Je partage cet evenement";
                Intent shareEvent = new Intent(Intent.ACTION_SEND);
                shareEvent.putExtra(Intent.EXTRA_TEXT, msg);
                shareEvent.setType("text/plain");
                try {
                    //creer menu pour choisir l'app avec laquelle on veut partager l'evenement
                    startActivity(Intent.createChooser(shareEvent, getResources().getText(R.string.send)));
                } catch (Exception e) {
                    Log.e("SHARE ERROR", e.getMessage());
                }
            }
        });

        supprimerB = (Button) findViewById(R.id.supprimerB);
        supprimerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //On récupère les informations pour peupler les champs
                //On crée le response listener qui attend la réponse de l'API
                Response.Listener<String> responseListenerDeleteEvent = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            JSONObject jsonResponse = new JSONObject(response);

                            //maintenant qu'on a notre réponse en json, on peut analyser la réponse
                            //va chercher le champ success de la réponse json, qui est soit true ou false
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                Toast.makeText(getApplicationContext(), "Évènement supprimé", Toast.LENGTH_SHORT).show();
                                //Crée un intent qui ouvre un MainActivity
                                Intent mainIntent = new Intent(EventDetailsAuteurActivity.this, MainActivity.class);
                                EventDetailsAuteurActivity.this.startActivity(mainIntent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Erreur dans l'envoi de l'ID de l'évènement", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                DeleteEventRequest deleteEventRequest = new DeleteEventRequest(eventID, responseListenerDeleteEvent);
                queue = Volley.newRequestQueue(EventDetailsAuteurActivity.this);
                queue.add(deleteEventRequest);
            }
        });



        coursCode.setText(code);
        coursNom.setText(titre_cours);

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
                            heure.setText(row.getString("heure"));
                            nomEvenement.setText(row.getString("titre"));
                            typeEvenement.setText(row.getString("type"));
                            author.setText(row.getString("pseudo"));
                            description.setText(row.getString("description"));
                            eventPlace.setText(row.getString("lieu"));

                            String capacinombre = row.getString("nbr_participants") + "/" + row.getString("capacite");
                            eventCapacity.setText(capacinombre);

                        } else {
                            Toast.makeText(EventDetailsAuteurActivity.this, "marche pas", Toast.LENGTH_SHORT).show();
                        }
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        GetEventRequest getEventRequest= new GetEventRequest(eventID, responseListenerGetEvent);
        queue = Volley.newRequestQueue(EventDetailsAuteurActivity.this);
        queue.add(getEventRequest);


        //On récupère la liste d'utilisateurs

        persons = (ListView) findViewById(R.id.persons);

        final ArrayList<HashMap<String, String>> participantsListe = new ArrayList<HashMap<String, String>>();

        //On remplie le ListView (liste) avec les éléments de la réponse du json

        Response.Listener<String> participantsReponseListnener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Toast pour voir le json envoyé
                    //Toast.makeText(EventDetailsNotAuteurActivity.this, response, Toast.LENGTH_LONG).show();
                    JSONObject jsonResponse = new JSONObject(response);

                    Iterator<?> keys = jsonResponse.keys();

                    while( keys.hasNext() ) {
                        String key = (String)keys.next();
                        if ( jsonResponse.get(key) instanceof JSONObject ) {
                            JSONObject row = (JSONObject) jsonResponse.get(key);

                            //chargement du listview
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("pseudo", row.getString("pseudo"));


                            participantsListe.add(map);


                            android.widget.ListAdapter adapter;

                            adapter = new SimpleAdapter(EventDetailsAuteurActivity.this, participantsListe, R.layout.participants_layout,
                                    new String[]{"pseudo"},
                                    new int[]{R.id.nomParticipant});

                            persons.setAdapter(adapter);
                        } else {
                            Toast.makeText(EventDetailsAuteurActivity.this, "marche pas", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //On crée la requête
        ListerParticipantsRequest listerParticipantsRequest = new ListerParticipantsRequest(eventID, participantsReponseListnener);
        queue = Volley.newRequestQueue(EventDetailsAuteurActivity.this);
        queue.add(listerParticipantsRequest);


    }
}
