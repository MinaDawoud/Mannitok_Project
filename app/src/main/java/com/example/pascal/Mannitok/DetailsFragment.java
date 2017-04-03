package com.example.pascal.Mannitok;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Alejandro on 4/14/2016.
 */
public class DetailsFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    LayoutInflater listeInflater;
    String eventID;
    int typeDetail;
    boolean own;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Recuperation de l'info du item pour lequel on affiche le details
        Bundle details = getArguments();
        eventID = details.getString("eventID"); //On reçoit le TAB sur lequel on a fait click
        typeDetail= details.getInt("typeDetail");//pour savoir quelle layout pour le fragment utiliser 0=desc evenement 1,2=liste evenements d'un cours
        own=details.getBoolean("ownEvent");//true s'il s'agit d'un evenement cree par l'utilisateur


        View v = null;
        ListView liste = null;
        Log.i("New DetailsFragment","eventID: "+eventID+" typeDetail: "+typeDetail);

        if(typeDetail==0){//demande de details a partir de la liste de items de "MES EVENEMENTS", donc une liste de evenements

            if(own){//true s'il s'agit d'un evenement cree par l'utilisateur
                v = inflater.inflate(R.layout.details_modify_layout, container, false);
                Button changeButton = (Button)v.findViewById(R.id.modifyB);
                changeButton.setOnClickListener(this);
            }
            else
                v = inflater.inflate(R.layout.details_layout, container, false);

            TextView courseCode = (TextView) v.findViewById(R.id.coursCode);
            Button shareButton = (Button)v.findViewById(R.id.shareB);
            Button joinButton = (Button)v.findViewById(R.id.joinB);

            shareButton.setOnClickListener(this);
            joinButton.setOnClickListener(this);

            courseCode.setText(eventID);
        }
        else if(typeDetail==1 || typeDetail==2){//demande de details a partir de la liste de items de "RECHERCHE", donc une liste de cours.
            v = inflater.inflate(R.layout.cours_details_list_layout,container,false);
            ListAdapter adapter = new ListAdapter(getActivity(),typeDetail);

            //Recuperation des elements du layout a remplir
            TextView courseName= (TextView) v.findViewById(R.id.coursCode);
            ListView eventList = (ListView) v.findViewById(R.id.courseEventL);
            Button addEventB = (Button) v.findViewById(R.id.addCourseEventB);

            //Reglage des elementes du layout
            courseName.setText(eventID);
            eventList.setAdapter(adapter);
            addEventB.setOnClickListener(this);
            eventList.setOnItemClickListener(DetailsFragment.this);
        }
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent details = new Intent(getActivity(),DetailsActivity.class);
        //envoie de toute l'information qu'on veut afficher
        details.putExtra("eventID", position);
        details.putExtra("typeDetail", 0);
        if(position%2==0)//si l'evenement sur lequel on click a ete cree par l'utilisateur
            details.putExtra("ownEvent",true);
        DetailsActivity.details.finish();
        startActivity(details);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.addCourseEventB){
            //lancer l'activité creer evenement en passant en parametre le id du cours pour lequel on veut ajouter un evenement
            Intent createEvent = new Intent(getActivity(),AddEvent.class);
            createEvent.putExtra("courseID",eventID);
            createEvent.putExtra("courseName","Systemes d'exploitation");
            startActivity(createEvent);
        }
        else if(v.getId()==R.id.modifyB){
            //lancer l'activité change evenement en passant en parametre le id de l'evenement qu'on veut modifier
            Intent changeEvent = new Intent(getActivity(),ChangeEvent.class);
            changeEvent.putExtra("courseID",eventID);
            changeEvent.putExtra("courseName","Systemes d'exploitation");
            startActivity(changeEvent);
        }
        else if(v.getId()==R.id.shareB){
            Toast.makeText(getActivity(),"Partager",Toast.LENGTH_SHORT).show();
        }
        else if(v.getId()==R.id.joinB){
            Toast.makeText(getActivity(),"Joindre",Toast.LENGTH_SHORT).show();
        }
    }

    public class ListAdapter extends BaseAdapter{

        LayoutInflater inflater;
        int typeDetail;

        public ListAdapter(Context activity, int typeDetail) {
            inflater = LayoutInflater.from(activity);
            this.typeDetail=typeDetail;
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
                if(typeDetail==0) {//selon tab ou choixID remplir les elements de la liste du fragment
                    item = inflater.inflate(R.layout.event_layout, parent, false);
                    //Recuperation des elements du layout a remplir
                    /*
                    TextView cours= (TextView)item.findViewById(R.id.eventName);
                    TextView titreCours= (TextView)item.findViewById(R.id.courseName);
                    TextView dans= (TextView)item.findViewById(R.id.courseDate);
                    TextView titre= (TextView)item.findViewById(R.id.eventName);
                    TextView capacite= (TextView)item.findViewById(R.id.eventCapacity);
                    TextView type= (TextView)item.findViewById(R.id.courseName);

                    //Remplisage des elements du layout
                    cours.setText("IFT- " + position);
                    titreCours.setText("Titre Cours " + position);
                    dans.setText(position+":50");
                    titre.setText("Event Title " + position);
                    capacite.setText(position+"/30");
                    type.setText("Type " + position);*/
                }
                else if(typeDetail==1) {
                    //item = inflater.inflate(R.layout.search_layout, parent, false);
                    item = inflater.inflate(R.layout.search_layout, parent, false);
                    TextView texto= (TextView)item.findViewById(R.id.coursCode);
                    texto.setText("Search "+position);
                }
                else {
                    //item = inflater.inflate(R.layout.favorite_layout, parent, false);
                    item = inflater.inflate(R.layout.favorite_layout, parent, false);
                    TextView texto= (TextView)item.findViewById(R.id.coursCode);
                    texto.setText("Favorite "+position);
                }
            }
            return item;
        }

    }

}
