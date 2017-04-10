package com.example.pascal.Mannitok;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.pascal.Mannitok.Requetes.ListerAllEventsRequest;
import com.example.pascal.Mannitok.Requetes.ListerArticlesRequest;
import com.example.pascal.Mannitok.Requetes.ListerCoursRechercheRequest;
import com.example.pascal.Mannitok.Requetes.ListerEntreprisesRequest;
import com.example.pascal.Mannitok.Requetes.ListerFavorisRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class UserContentFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    LayoutInflater listeInflater;
    String searchCourseID;//ID du cours pour lequel on cherche des evenements
    AutoCompleteTextView search;//champ pour lire le cours pour lequel on va chercher des evenements
    View contentBox;//layout du fragment a retourner
    int choixID;//le TAB pour lequel il faut retourner le fragment a afficher
    int compteurEvent = 0;
    int compteurFavori = 0;
    Context c;

    ListView liste;

    RequestQueue queue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        Bundle choix = getArguments();
        choixID = choix.getInt("choix"); //On reçoit le TAB sur lequel on a fait click


        //on va chercher le contexte
        c = getActivity();

        /*
         * Cas des évènements (tab position 0)
         */
        if(choixID==0) {
            contentBox = inflater.inflate(R.layout.event_liste_layout, container, false);
            liste = (ListView) contentBox.findViewById(R.id.eventL);
           // ListAdapter adapter = new ListAdapter(getActivity(),choixID);
            //liste.setAdapter(adapter);

            final ArrayList<HashMap<String, String>> eventsListe = new ArrayList<HashMap<String, String>>();

            //On remplie le ListView (liste) avec les éléments de la réponse du json

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        //Toast pour voir le json envoyé
                        //Toast.makeText(c, response, Toast.LENGTH_SHORT).show();
                        JSONObject jsonResponse = new JSONObject(response);

                        TextView titreCours= (TextView)contentBox.findViewById(R.id.titre_cours);
                        TextView dans= (TextView)contentBox.findViewById(R.id.dans);
                        TextView titre= (TextView)contentBox.findViewById(R.id.titre);
                        TextView capacite= (TextView)contentBox.findViewById(R.id.capacite);
                        TextView type= (TextView)contentBox.findViewById(R.id.type);

                        //jsonResponse = new JSONObject(contents.trim());
                        Iterator<?> keys = jsonResponse.keys();

                        while( keys.hasNext() ) {
                            compteurEvent++;
                            String key = (String)keys.next();
                            if ( jsonResponse.get(key) instanceof JSONObject ) {
                                JSONObject row = (JSONObject) jsonResponse.get(key);

                                HashMap<String, String> map = new HashMap<String, String>();
                               map.put("titre", row.getString("titre"));
                                map.put("date", row.getString("date"));
                                map.put("type", row.getString("type"));
                                //map.put("capacite", row.getString("capacite"));
                                String capacitepart = row.getInt("nbr_participants") + "/" + row.getString("capacite");
                                map.put("capacite", capacitepart);
                                map.put("titre_cours", row.getString("titre_cours"));
                                map.put("auteur", row.getString("auteur"));

                                //si c'est l'auteur, on met un C rouge, sinon rien
                                if(row.getString("auteur").equals("true")){
                                    map.put("c", "Créé");
                                } else {
                                    map.put("c", "");
                                }
                                map.put("code", row.getString("code"));
                                map.put("eventID", row.getString("eventID"));


                                eventsListe.add(map);
                                liste = (ListView) contentBox.findViewById(R.id.eventL);

                                android.widget.ListAdapter adapter = new SimpleAdapter(c, eventsListe, R.layout.event_layout,
                                        new String[]{"date","titre", "type", "capacite", "titre_cours", "c"},
                                        new int[]{R.id.dans,R.id.titre, R.id.type, R.id.capacite, R.id.titre_cours, R.id.c_cree});

                                liste.setAdapter(adapter);

                                liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        if(eventsListe.get(+position).get("auteur").equals("true")){

                                            Intent coursEventIntent = new Intent(getActivity() ,EventDetailsAuteurActivity.class);
                                            coursEventIntent.putExtra("code", eventsListe.get(+position).get("code"));
                                            coursEventIntent.putExtra("titre_cours", eventsListe.get(+position).get("titre_cours"));
                                            coursEventIntent.putExtra("eventID", eventsListe.get(+position).get("eventID"));
                                            startActivity(coursEventIntent);

                                        } else {
                                            Intent coursEventIntent = new Intent(getActivity() ,EventDetailsNotAuteurActivity.class);
                                            coursEventIntent.putExtra("code", eventsListe.get(+position).get("code"));
                                            coursEventIntent.putExtra("titre_cours", eventsListe.get(+position).get("titre_cours"));
                                            coursEventIntent.putExtra("eventID", eventsListe.get(+position).get("eventID"));
                                            startActivity(coursEventIntent);
                                        }
                                    }
                                });

                            } else {
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //si compteur est à 0, c'est qu'on n'avait aucun évènement, donc on va afficher un petit
                    //texte à la place pour indiquer aux gens comment rajouter des évènements.
                    if(compteurEvent==0){
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("message", "Vous n'avez pas encore joint ou créé d'évènement. Vous pouvez le faire en allant sur la page d'un cours à partir de l'onglet 'Recherche'.");
                        eventsListe.add(map);
                        liste = (ListView) contentBox.findViewById(R.id.eventL);

                        android.widget.ListAdapter adapter = new SimpleAdapter(c, eventsListe, R.layout.no_element_layout,
                                new String[]{"message"},
                                new int[]{R.id.leTextView});

                        liste.setAdapter(adapter);
                    }

                }


            };
            //On crée la requête
            ListerAllEventsRequest listerAllEventsRequest = new ListerAllEventsRequest(responseListener);
            queue = Volley.newRequestQueue(c);
            queue.add(listerAllEventsRequest);

        }
/*********************************************************************************************************/
        //ONGLET DE LA RECHERCHE DES ARTICLES
        else if(choixID==1) {
            contentBox = inflater.inflate(R.layout.search_list_layout, container, false);

            //Obtenir la liste a remplir
            liste = (ListView) contentBox.findViewById(R.id.searchL);
           // ListAdapter adapter = new ListAdapter(getActivity(),choixID);
           // liste.setAdapter(adapter);

            final ArrayList<HashMap<String, String>> articlesListe = new ArrayList<HashMap<String, String>>();

            //Le autocomplete
            //Creer liste complete des articles disponibles, à remplir avec l'info de la base de donnees
            final ArrayList<String> articles=new ArrayList<String>();

            //On remplie le ListView (liste) avec les éléments de la réponse du json

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        //Toast pour voir le json envoyé
                        //Toast.makeText(c, response, Toast.LENGTH_LONG).show();
                        JSONObject jsonResponse = new JSONObject(response);

                        Iterator<?> keys = jsonResponse.keys();

                        while( keys.hasNext() ) {

                            String key = (String)keys.next();
                            if ( jsonResponse.get(key) instanceof JSONObject ) {
                                JSONObject row = (JSONObject) jsonResponse.get(key);

                                //chargement pour le autocomplete
                                articles.add(row.getString("articleID"));

                                articles.add(row.getString("titre"));
                                //chargement du listview
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("articleID", row.getString("articleID"));
                                map.put("titre", row.getString("titre"));
                                map.put("categorie", row.getString("categorie"));
                                map.put("prix", row.getString("prix") + "$");

                                articlesListe.add(map);
                                liste = (ListView) contentBox.findViewById(R.id.searchL);

                                android.widget.ListAdapter adapter;

                                boolean estFavori = UserInformation.isFavori(row.getString("articleID"));
                                if(estFavori){
                                    adapter = new SimpleAdapter(c, articlesListe, R.layout.articles_search_layout_faved,
                                            new String[]{"articleID", "titre", "prix"},
                                            new int[]{R.id.articleID,R.id.articleNom, R.id.prix});
                                } else {
                                    adapter = new SimpleAdapter(c, articlesListe, R.layout.articles_search_layout,
                                            new String[]{"articleID", "titre", "prix"},
                                            new int[]{R.id.articleID,R.id.articleNom, R.id.prix});
                                }


                                liste.setAdapter(adapter);

                                liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    cliquerCours(articlesListe, position);
                                    }
                                });


                            } else {
                                Toast.makeText(c, "marche pas", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            //On crée la requête
            ListerArticlesRequest listerArticlesRequest= new ListerArticlesRequest(responseListener);
            queue = Volley.newRequestQueue(c);
            queue.add(listerArticlesRequest);



            //Set le champ autocomplete
            Button searchB = (Button) contentBox.findViewById(R.id.searchB);
            searchB.setOnClickListener(this);
            search= (AutoCompleteTextView) contentBox.findViewById(R.id.autoCompleteTextView);
            ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,articles);
            search.setAdapter(autoCompleteAdapter);
        }

        /*********************************************************************************************************/

        //ONGLET DE LA RECHERCHE DES ENTREPRISES
        else if(choixID==2) {
            contentBox = inflater.inflate(R.layout.search_list_layout, container, false);

            //Obtenir la liste a remplir
            liste = (ListView) contentBox.findViewById(R.id.searchL);
            // ListAdapter adapter = new ListAdapter(getActivity(),choixID);
            // liste.setAdapter(adapter);

            final ArrayList<HashMap<String, String>> entreprisesListe = new ArrayList<HashMap<String, String>>();

            //Le autocomplete
            //Creer liste complete des entreprises disponibles, à remplir avec l'info de la base de donnees
            final ArrayList<String> entreprises=new ArrayList<String>();

            //On remplie le ListView (liste) avec les éléments de la réponse du json

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        //Toast pour voir le json envoyé
                        //Toast.makeText(c, response, Toast.LENGTH_LONG).show();
                        JSONObject jsonResponse = new JSONObject(response);

                        Iterator<?> keys = jsonResponse.keys();

                        while( keys.hasNext() ) {

                            String key = (String)keys.next();
                            if ( jsonResponse.get(key) instanceof JSONObject ) {
                                JSONObject row = (JSONObject) jsonResponse.get(key);

                                //chargement pour le autocomplete
                                entreprises.add(row.getString("entrepriseID"));

                                entreprises.add(row.getString("titre"));
                                //chargement du listview
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("entrepriseID", row.getString("entrepriseID"));
                                map.put("titre", row.getString("titre"));
                                map.put("description", row.getString("description"));
                                map.put("ville", row.getString("ville"));

                                entreprisesListe.add(map);
                                liste = (ListView) contentBox.findViewById(R.id.searchL);

                                android.widget.ListAdapter adapter;

                                boolean estFavori = UserInformation.isFavori(row.getString("entrepriseID"));
                                if(estFavori){
                                    adapter = new SimpleAdapter(c, entreprisesListe, R.layout.entreprises_search_layout_faved,
                                            new String[]{"entrepriseID", "titre", "ville"},
                                            new int[]{R.id.entrepriseID,R.id.entrepriseNom, R.id.ville});
                                } else {
                                    adapter = new SimpleAdapter(c, entreprisesListe, R.layout.entreprises_search_layout,
                                            new String[]{"entrepriseID", "titre", "ville"},
                                            new int[]{R.id.entrepriseID,R.id.entrepriseNom, R.id.ville});
                                }


                                liste.setAdapter(adapter);

                                liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        cliquerCours(entreprisesListe, position);
                                    }
                                });


                            } else {
                                Toast.makeText(c, "marche pas", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            //On crée la requête
            ListerEntreprisesRequest listerEntreprisesRequest= new ListerEntreprisesRequest(responseListener);
            queue = Volley.newRequestQueue(c);
            queue.add(listerEntreprisesRequest);



            //Set le champ autocomplete
            Button searchB = (Button) contentBox.findViewById(R.id.searchB);
            searchB.setOnClickListener(this);
            search= (AutoCompleteTextView) contentBox.findViewById(R.id.autoCompleteTextView);
            ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,entreprises);
            search.setAdapter(autoCompleteAdapter);
        }

        /*********************************************************************************************************/

        /*
        POUR L'ONGLET FAVORIS
         */
        else {
            contentBox = inflater.inflate(R.layout.favorite_list_layout, container, false);
            liste = (ListView) contentBox.findViewById(R.id.favoriteL);
            //ListAdapter adapter = new ListAdapter(getActivity(),choixID);
            //liste.setAdapter(adapter);

            final ArrayList<HashMap<String, String>> favorisListe = new ArrayList<HashMap<String, String>>();

            //On remplie le ListView (liste) avec les éléments de la réponse du json

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        //Toast pour voir le json envoyé
                        //Toast.makeText(c, response, Toast.LENGTH_LONG).show();
                        JSONObject jsonResponse = new JSONObject(response);

                        Iterator<?> keys = jsonResponse.keys();

                        while( keys.hasNext() ) {
                            compteurFavori++;
                            String key = (String)keys.next();
                            if ( jsonResponse.get(key) instanceof JSONObject ) {
                                JSONObject row = (JSONObject) jsonResponse.get(key);

                                //chargement du listview
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("code", row.getString("code"));
                                map.put("titre", row.getString("titre"));
                                String evenement;
                                if(row.getString("nbrEvent").equals("0") || row.getString("nbrEvent").equals("1")){
                                    evenement = " évènement";
                                } else {
                                    evenement = " évènements";
                                }
                                map.put("nbrEvent", row.getString("nbrEvent") + evenement);

                                favorisListe.add(map);
                                liste = (ListView) contentBox.findViewById(R.id.favoriteL);

                                boolean estFavori = UserInformation.isFavori(row.getString("code"));
                                android.widget.ListAdapter adapter;
                                if(estFavori){
                                    adapter = new SimpleAdapter(c, favorisListe, R.layout.favorite_layout_faved,
                                            new String[]{"code", "titre", "nbrEvent"},
                                            new int[]{R.id.coursNom,R.id.coursCode, R.id.nbEvents});
                                } else {
                                    adapter = new SimpleAdapter(c, favorisListe, R.layout.favorite_layout,
                                            new String[]{"code", "titre", "nbrEvent"},
                                            new int[]{R.id.coursNom,R.id.coursCode, R.id.nbEvents});
                                }


                                liste.setAdapter(adapter);

                                liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        cliquerCours(favorisListe, position);
                                    }
                                });
                            } else {
                                Toast.makeText(c, "marche pas", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //si compteur est à 0, c'est qu'on n'avait aucun favori, donc on va afficher un petit
                    //texte à la place pour indiquer aux gens comment rajouter des évènements.
                    if(compteurFavori==0){
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("message", "Vous n'avez pas encore de favoris. Vous pouvez en rajouter en allant sur la page d'un cours à partir de l'onglet 'Recherche'.");
                        favorisListe.add(map);
                        liste = (ListView) contentBox.findViewById(R.id.favoriteL);

                        android.widget.ListAdapter adapter = new SimpleAdapter(c, favorisListe, R.layout.no_element_layout,
                                new String[]{"message"},
                                new int[]{R.id.leTextView});

                        liste.setAdapter(adapter);
                    }
                }
            };
            //On crée la requête
            ListerFavorisRequest listerFavorisRequest = new ListerFavorisRequest(responseListener);
            queue = Volley.newRequestQueue(c);
            queue.add(listerFavorisRequest);


        }



        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //liste.setOnItemClickListener(ContentFragment.this);
        return contentBox;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
        Toast.makeText(getActivity(),"Click on Event",Toast.LENGTH_SHORT).show();
        Intent details = new Intent(getActivity(),DetailsActivity.class);
        //envoie de cles qui definissent l'info a afficher par DetailsActivity
        details.putExtra("eventID",searchCourseID);//cours ou evenement pour lequel on veut afficher les details
        details.putExtra("typeDetail",choixID);//identifie s'il faut montrer une liste d'evenements pour un cours ou une description d'un evenement
        startActivity(details);
        */
    }

    @Override
    public void onClick(View v) {
        /*
        LE BOUTON CHERCHER DE L'ONGLET RECHERCHE
         */
            if(v.getId()==R.id.searchB){

                //on va chercher le contexte
                c = getActivity();
                //hide keyboard on serach botton click
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                int choixID=1;
                //set le nom du cours pour lequel on va chercher des evenements
                searchCourseID=search.getText().toString();
                search.setText(null);
                liste = (ListView) contentBox.findViewById(R.id.searchL);
                //ListAdapter adapter = new ListAdapter(getActivity(),choixID);
                //liste.setAdapter(adapter);

                final ArrayList<HashMap<String, String>> coursListe = new ArrayList<HashMap<String, String>>();

                //On remplie le ListView (liste) avec les éléments de la réponse du json

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Toast pour voir le json envoyé
                            //Toast.makeText(c, response, Toast.LENGTH_LONG).show();
                            JSONObject jsonResponse = new JSONObject(response);

                            Iterator<?> keys = jsonResponse.keys();

                            while( keys.hasNext() ) {
                                String key = (String)keys.next();
                                if ( jsonResponse.get(key) instanceof JSONObject ) {
                                    JSONObject row = (JSONObject) jsonResponse.get(key);

                                    //chargement du listview
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("code", row.getString("code"));
                                    map.put("titre", row.getString("titre"));
                                    map.put("categorie", row.getString("categorie"));
                                    String evenement;
                                    if(row.getString("nbrEvent").equals("0") || row.getString("nbrEvent").equals("1")){
                                        evenement = " évènement";
                                    } else {
                                        evenement = " évènements";
                                    }
                                    map.put("nbrEvent", row.getString("nbrEvent") + evenement);

                                    coursListe.add(map);
                                    liste = (ListView) contentBox.findViewById(R.id.searchL);

                                    android.widget.ListAdapter adapter;

                                    boolean estFavori = UserInformation.isFavori(row.getString("code"));
                                    if(estFavori){
                                        adapter = new SimpleAdapter(c, coursListe, R.layout.search_layout_faved,
                                                new String[]{"code", "titre", "nbrEvent"},
                                                new int[]{R.id.courseCode,R.id.coursNom, R.id.nbEvents});
                                    } else {
                                        adapter = new SimpleAdapter(c, coursListe, R.layout.search_layout,
                                                new String[]{"code", "titre", "nbrEvent"},
                                                new int[]{R.id.courseCode,R.id.coursNom, R.id.nbEvents});
                                    }


                                    liste.setAdapter(adapter);

                                    liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            cliquerCours(coursListe, position);
                                        }
                                    });




                                } else {
                                    Toast.makeText(c, "marche pas", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //On crée la requête
                ListerCoursRechercheRequest listerCoursRechercheRequest= new ListerCoursRechercheRequest(searchCourseID, responseListener);
                queue = Volley.newRequestQueue(c);
                queue.add(listerCoursRechercheRequest);

            }
    }

    /*
    FONCTION POUR GÉNÉRER LA LISTE DES ÉVÈNEMENTS RELIÉS À UN COURS LORSQU'ON CLIQUE DESSUS DANS L'ONGLET RECHERCHE OU FAVORI
     */

    public void cliquerCours(ArrayList<HashMap<String, String>> listeParams, int position){
        //Toast.makeText(c, "Cours cliqué est : " + listeParams.get(+position).get("titre"), Toast.LENGTH_SHORT).show();


        Intent coursEventIntent = new Intent(getActivity(),CoursEventActivity.class);
        coursEventIntent.putExtra("code", listeParams.get(+position).get("code"));
        coursEventIntent.putExtra("titre", listeParams.get(+position).get("titre"));
        startActivity(coursEventIntent);
    }

    /*
    je n'utilise pas cette classe
     */
    public class ListAdapter extends BaseAdapter{

        LayoutInflater inflater;
        int choixID;
        HashMap<String, String> map;

        public ListAdapter(Context activity, int choixID, HashMap<String, String> map) {
            inflater = LayoutInflater.from(activity);
            this.choixID=choixID;
            this.map = map;
        }

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item= convertView;
            if(convertView == null) {
                /*
                if(choixID==0) {//selon tab ou choixID remplir les elements de la liste du fragment
                    /*
                    item = inflater.inflate(R.layout.event_layout, parent, false);
                    TextView titreCours= (TextView)item.findViewById(R.id.titre_cours);
                    TextView dans= (TextView)item.findViewById(R.id.dans);
                    TextView titre= (TextView)item.findViewById(R.id.titre);
                    TextView capacite= (TextView)item.findViewById(R.id.capacite);
                    TextView type= (TextView)item.findViewById(R.id.type);
                    titreCours.setText("Titre Cours " + position);
                    dans.setText(position+":50");
                    titre.setText("Event Title " + position);
                    capacite.setText(position+"/30");
                    type.setText("Type " + position);
                    */
                }
                else if(choixID==1) {
                    /*
                    item = inflater.inflate(R.layout.search_layout, parent, false);
                    //Recuperation des TextView pour le layout de la liste qui affiche le resultat d'une recherche
                    TextView courseCode= (TextView)item.findViewById(R.id.courseCode);
                    TextView courseName= (TextView)item.findViewById(R.id.courseName);
                    TextView nbEvents= (TextView)item.findViewById(R.id.nbEvents);
                    CheckBox favorite=(CheckBox) item.findViewById(R.id.favorite);

                    //Reglase des TextView
                    if(searchCourseID==null)
                        courseCode.setText("Cours IFT-XXXX "+position);
                    else
                        courseCode.setText(searchCourseID+" "+position);
                    courseName.setText("Systemes d'exploitation");
                    nbEvents.setText("3 events");
                    if(position%3==0)
                        favorite.setChecked(true);
                        */
                }
                else {
                    item = inflater.inflate(R.layout.favorite_layout, parent, false);

                    TextView code = (TextView)item.findViewById(R.id.coursNom);
                    code.setText(map.get("code"));

                    TextView titre = (TextView)item.findViewById(R.id.coursCode);
                    titre.setText(map.get("titre"));

                    TextView nbrEvent = (TextView)item.findViewById(R.id.nbEvents);
                    nbrEvent.setText(map.get("nbrEvent"));

                    //TextView texto= (TextView)item.findViewById(R.id.textView);
                    //texto.setText("Cours Favoris "+position);
                }



            return item;
        }
    }
}
