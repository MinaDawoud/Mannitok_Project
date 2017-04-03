package com.example.pascal.Mannitok;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.pascal.Mannitok.Requetes.CreerFavorisRequest;
import com.example.pascal.Mannitok.Requetes.DeleteFavorisRequest;
import com.example.pascal.Mannitok.Requetes.ListerCoursEventsRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class CoursEventActivity extends AppCompatActivity {

    public static Activity coursEvent;
    String code;
    String titre_cours;
    int compteur = 0; //compteur pour savoir s'il y a des évènements pour ce cours

    TextView eventName;
    CheckBox addFavoriteB;
    Button addCourseEventB;

    ListView liste;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coursEvent = this; //pour finir cette activité de n'importe où

        setContentView(R.layout.cours_details_list_layout);

        //recuperer le cours
        Intent coursEvent=getIntent();
        code = coursEvent.getStringExtra("code");
        titre_cours = coursEvent.getStringExtra("titre");

        eventName = (TextView) findViewById(R.id.coursCode);
        eventName.setText(titre_cours);

        /*
        LA GESTION DU BOUTON FAVORIS, QUI FAIT L'AJOUT/LE RETRAIT DU COURS DANS LES FAVORIS
         */
        addFavoriteB = (CheckBox) findViewById(R.id.addFavoriteB);
        if(UserInformation.isFavori(code)){
            addFavoriteB.setChecked(true);
        }
        addFavoriteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addFavoriteB.isChecked()){
                    //On rajoute dans les favoris
                    Response.Listener<String> responseListenerCreer = new Response.Listener<String>(){

                        @Override
                        public void onResponse(String response) {
                            try {
                                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                //Si success = false, alors on ne fait rien (donc on reste à la création de compte)
                                //Si success = true, alors on passe tout de suite à la page d'accueil.
                                if(success){
                                    Toast.makeText(CoursEventActivity.this, "Cours " + code + " ajouté aux favoris", Toast.LENGTH_SHORT).show();

                                    Intent accueil = new Intent(CoursEventActivity.this, MainActivity.class);

                                    //accueil.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(accueil);
                                    finish();
                                } else {
                                    //passe au LoginActivity
                                    Toast.makeText(CoursEventActivity.this, "Erreur, ne peut rajouter ce cours", Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    //On crée la requête
                    CreerFavorisRequest creerFavorisRequest = new CreerFavorisRequest(code, responseListenerCreer);
                    queue = Volley.newRequestQueue(CoursEventActivity.this);
                    queue.add(creerFavorisRequest);

                    UserInformation.addFavori(code);
                } else {
                    //On enlève aux favoris

                    Response.Listener<String> responseListenerDelete = new Response.Listener<String>(){

                        @Override
                        public void onResponse(String response) {
                            try {
                                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                //Si success = false, alors on ne fait rien (donc on reste à la création de compte)
                                //Si success = true, alors on passe tout de suite à la page d'accueil.
                                if(success){
                                    Toast.makeText(CoursEventActivity.this, "Cours " + code + " enlevé des favoris", Toast.LENGTH_SHORT).show();

                                    Intent accueil = new Intent(CoursEventActivity.this, MainActivity.class);

                                    //accueil.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(accueil);
                                    finish();

                                } else {
                                    //passe au LoginActivity
                                    Toast.makeText(CoursEventActivity.this, "Erreur, cours n'est pas dans les favoris", Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    //On crée la requête
                    DeleteFavorisRequest deleteFavorisRequest = new DeleteFavorisRequest(code, responseListenerDelete);
                    queue = Volley.newRequestQueue(CoursEventActivity.this);
                    queue.add(deleteFavorisRequest);

                    UserInformation.deleteFavori(code);
                }
            }
        });


        /*
        Lorsqu'on clique sur le bouton pour créer un évènement, ça lance une nouvelle activité
         */
        addCourseEventB = (Button) findViewById(R.id.addCourseEventB);
        addCourseEventB.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {


                Intent addEventIntent = new Intent(CoursEventActivity.this,CreateEventActivity.class);
                addEventIntent.putExtra("code", code);
                addEventIntent.putExtra("titre", titre_cours);
                startActivity(addEventIntent);

            }
        });

        /*
        Requête, et remplissage de la liste d'évènements pour le cours
         */

        //Obtenir la liste a remplir
        liste = (ListView) findViewById(R.id.courseEventL);
        // ListAdapter adapter = new ListAdapter(getActivity(),choixID);
        // liste.setAdapter(adapter);

        final ArrayList<HashMap<String, String>> eventsListe = new ArrayList<HashMap<String, String>>();

        //On remplie le ListView (liste) avec les éléments de la réponse du json

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Toast pour voir le json envoyé
                    //Toast.makeText(CoursEventActivity.this, response, Toast.LENGTH_LONG).show();
                    JSONObject jsonResponse = new JSONObject(response);

                    Iterator<?> keys = jsonResponse.keys();

                    while( keys.hasNext() ) {
                        compteur++;
                        String key = (String)keys.next();
                        if ( jsonResponse.get(key) instanceof JSONObject ) {
                            JSONObject row = (JSONObject) jsonResponse.get(key);

                            //chargement du listview
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("titre", row.getString("titre"));
                            map.put("type", row.getString("type"));
                            map.put("date", row.getString("date"));
                            map.put("auteur", row.getString("auteur"));
                            map.put("eventID", row.getString("eventID"));
                            map.put("code", row.getString("code"));
                            if(row.getString("auteur").equals("true")){
                                map.put("status", "Créé");
                            } else if (row.getString("participation").equals("true")){
                                map.put("status", "Joint");
                            } else {
                                map.put("status", "");
                            }

                            String capacitepart = row.getInt("nbr_participants") + "/" + row.getString("capacite");
                            map.put("capacite", capacitepart);

                            eventsListe.add(map);
                            android.widget.ListAdapter adapter;



                            adapter = new SimpleAdapter(CoursEventActivity.this, eventsListe, R.layout.course_events_result_layout,
                                    new String[]{"titre", "type", "date", "capacite", "status"},
                                    new int[]{R.id.coursCode, R.id.coursNom, R.id.eventDate, R.id.nbEvents, R.id.status});

                            liste.setAdapter(adapter);

                            liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if(eventsListe.get(+position).get("auteur").equals("true")){
                                        //dans le cas où l'utilisateur est l'auteur de l'évènement, on veut le layout details_modify_layout
                                        //Toast.makeText(CoursEventActivity.this, "Évènement est : " + eventsListe.get(+position).get("titre") + " JE SUIS L'AUTEUR", Toast.LENGTH_SHORT).show();
                                        Intent coursEventIntent = new Intent(CoursEventActivity.this ,EventDetailsAuteurActivity.class);
                                        coursEventIntent.putExtra("code", eventsListe.get(+position).get("code"));
                                        coursEventIntent.putExtra("titre_cours", titre_cours);
                                        coursEventIntent.putExtra("eventID", eventsListe.get(+position).get("eventID"));
                                        startActivity(coursEventIntent);


                                    } else {
                                        //dans le cas où l'utilisateur n'est pas l'auteur de l'évènement, on veut le layout details_layout
                                        // Toast.makeText(CoursEventActivity.this, "Évènement est : " + eventsListe.get(+position).get("titre") + " ne suis pas l'auteur", Toast.LENGTH_SHORT).show();
                                        Intent coursEventIntent = new Intent(CoursEventActivity.this ,EventDetailsNotAuteurActivity.class);
                                        coursEventIntent.putExtra("code", eventsListe.get(+position).get("code"));
                                        coursEventIntent.putExtra("titre_cours", titre_cours);
                                        coursEventIntent.putExtra("eventID", eventsListe.get(+position).get("eventID"));
                                        startActivity(coursEventIntent);
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(CoursEventActivity.this, "marche pas", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //si compteur est à 0, c'est qu'on n'avait aucun évènement, donc on va afficher un petit
                //texte à la place pour indiquer aux gens comment rajouter des évènements.
                if(compteur==0){
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("message", "Pas encore d'évènement à venir pour ce cours. Soyez le premier à en ajouter!");
                    eventsListe.add(map);

                    android.widget.ListAdapter adapter = new SimpleAdapter(CoursEventActivity.this, eventsListe, R.layout.no_element_layout,
                            new String[]{"message"},
                            new int[]{R.id.leTextView});

                    liste.setAdapter(adapter);
                }
            }
        };
        //On crée la requête
        ListerCoursEventsRequest listerCoursEventsRequest = new ListerCoursEventsRequest(code, responseListener);
        queue = Volley.newRequestQueue(CoursEventActivity.this);
        queue.add(listerCoursEventsRequest);

    }
}
