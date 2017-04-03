package com.example.pascal.Mannitok;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangeEvent extends AppCompatActivity implements View.OnClickListener {
    String courseID, courseName;
    EditText enventTitle,enventType,enventDate ,enventTime ,enventPlace ,enventCapacity ,enventDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_event);

        Intent courseDetails = getIntent();
        courseID=courseDetails.getStringExtra("courseID");
        courseName=courseDetails.getStringExtra("courseName");

        //Recuperation d'elements pour afficher info du cours pour lequel on cree un evenement
        TextView courseCodeTV= (TextView) findViewById(R.id.coursCode);
        TextView courseNameTV= (TextView) findViewById(R.id.coursNom);

        //set code et nom du cours pour lequel on va creer un evenemet
        courseCodeTV.setText(courseID);
        courseNameTV.setText(courseName);

        Button createEventB= (Button) findViewById(R.id.changeEventB);
        createEventB.setOnClickListener(this);

        enventTitle =(EditText) findViewById(R.id.eventTitle);
        enventType =(EditText) findViewById(R.id.coursNom);
        enventDate =(EditText) findViewById(R.id.eventDate);
        enventTime =(EditText) findViewById(R.id.eventTime);
        enventPlace =(EditText) findViewById(R.id.eventPlace);
        enventCapacity =(EditText) findViewById(R.id.eventCapacity);
        enventDescription =(EditText) findViewById(R.id.eventDescription);

        //faire le set de tous les champs du layout etant donne qu'on a le ID de l'evenement il faut faire un requete et remplir

        /*TO-DO
        enventTitle.setText();
        enventType.setText();
        enventDate.setText();
        enventTime.setText();
        enventPlace.setText();
        enventCapacity.setText();
        enventDescription.setText();
        */
    }

    @Override
    public void onClick(View v) {
        //prendre les valeurs qui on ete updated et envoyer la requete API pour mettre a jour l'info
            //TO-DO

        //envoyer la requete pour creer l'evenement et retourner a la page d'accueil
        MainActivity.accueil.finish();
        DetailsActivity.details.finish();
        Intent accueil = new Intent(this,MainActivity.class);
        //accueil.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(accueil);
        finish();
    }
}
