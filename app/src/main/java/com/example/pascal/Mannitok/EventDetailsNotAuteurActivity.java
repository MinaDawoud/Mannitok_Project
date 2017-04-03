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
import com.example.pascal.Mannitok.Requetes.GetEventRequest;
import com.example.pascal.Mannitok.Requetes.JoindreEventRequest;
import com.example.pascal.Mannitok.Requetes.ListerParticipantsRequest;
import com.example.pascal.Mannitok.Requetes.QuitterEventRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class EventDetailsNotAuteurActivity extends AppCompatActivity {

    public static Activity eventDetailsNotAuteur;
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
    Button joinB;

    boolean alreadyInEvent;
    boolean full;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventDetailsNotAuteur = this;
        setContentView(R.layout.details_layout);

        alreadyInEvent = false;
        full = false;

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


        joinB = (Button) findViewById(R.id.joinB);
        joinB.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(alreadyInEvent) {
                    //si déjà dans l'évènement, on quitte
                    Response.Listener<String> responseListenerQuitter = new Response.Listener<String>(){

                        @Override
                        public void onResponse(String response) {
                            try {
                                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                //Si success = false, alors on ne fait rien (donc on reste à la création de compte)
                                //Si success = true, alors on passe tout de suite à la page d'accueil.
                                if(success){
                                    Toast.makeText(EventDetailsNotAuteurActivity.this, "Évènement quitté avec succès", Toast.LENGTH_SHORT).show();

                                    Intent accueil = new Intent(EventDetailsNotAuteurActivity.this, MainActivity.class);

                                    //accueil.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(accueil);
                                    finish();

                                } else {
                                    //passe au LoginActivity
                                    Toast.makeText(EventDetailsNotAuteurActivity.this, "Erreur, evenement ne peut être quitté", Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    //On crée la requête
                    QuitterEventRequest quitterEventRequest = new QuitterEventRequest(eventID, responseListenerQuitter);
                    queue = Volley.newRequestQueue(EventDetailsNotAuteurActivity.this);
                    queue.add(quitterEventRequest);

                }else if (full){

                    //si évènement plein
                    Toast.makeText(EventDetailsNotAuteurActivity.this, "Vous êtes déjà inscrit à l'évènement.", Toast.LENGTH_SHORT).show();
                    }
                 else {
                    //sinon on s'inscrit, puis on retourne à l'accueil
                   // Toast.makeText(EventDetailsNotAuteurActivity.this, "pas encore dans l'évènement", Toast.LENGTH_LONG).show();


                    Response.Listener<String> responseListenerJoindre = new Response.Listener<String>(){

                        @Override
                        public void onResponse(String response) {
                            try {
                                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                //Si success = false, alors on ne fait rien (donc on reste à la création de compte)
                                //Si success = true, alors on passe tout de suite à la page d'accueil.
                                if(success){
                                    Toast.makeText(EventDetailsNotAuteurActivity.this, "Évènement rejoint avec succès", Toast.LENGTH_SHORT).show();

                                    Intent accueil = new Intent(EventDetailsNotAuteurActivity.this, MainActivity.class);

                                    //accueil.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(accueil);
                                    finish();

                                } else {
                                    //passe au LoginActivity
                                    Toast.makeText(EventDetailsNotAuteurActivity.this, "Erreur, evenement ne peut être rejoint", Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    //On crée la requête
                    JoindreEventRequest joindreEventRequest = new JoindreEventRequest(eventID, responseListenerJoindre);
                    queue = Volley.newRequestQueue(EventDetailsNotAuteurActivity.this);
                    queue.add(joindreEventRequest);
                }
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

                            if(Integer.parseInt(row.getString("nbr_participants")) >= Integer.parseInt(row.getString("capacite"))){
                                full = true;
                            }

                        } else {
                            Toast.makeText(EventDetailsNotAuteurActivity.this, "marche pas", Toast.LENGTH_SHORT).show();
                        }
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        GetEventRequest getEventRequest= new GetEventRequest(eventID, responseListenerGetEvent);
        queue = Volley.newRequestQueue(EventDetailsNotAuteurActivity.this);
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

                            if(UserInformation.getUsername().equals(row.getString("pseudo"))){
                                alreadyInEvent = true;
                                joinB.setText("Quitter");

                            }

                            participantsListe.add(map);


                            android.widget.ListAdapter adapter;

                            adapter = new SimpleAdapter(EventDetailsNotAuteurActivity.this, participantsListe, R.layout.participants_layout,
                                    new String[]{"pseudo"},
                                    new int[]{R.id.nomParticipant});

                            persons.setAdapter(adapter);
                        } else {
                            Toast.makeText(EventDetailsNotAuteurActivity.this, "marche pas", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //On crée la requête
        ListerParticipantsRequest listerParticipantsRequest = new ListerParticipantsRequest(eventID, participantsReponseListnener);
        queue = Volley.newRequestQueue(EventDetailsNotAuteurActivity.this);
        queue.add(listerParticipantsRequest);


    }
}
