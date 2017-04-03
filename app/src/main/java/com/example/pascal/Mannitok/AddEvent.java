package com.example.pascal.Mannitok;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AddEvent extends AppCompatActivity implements View.OnClickListener {
    String courseID, courseName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

                Intent courseDetails = getIntent();
        courseID=courseDetails.getStringExtra("courseID");
        courseName=courseDetails.getStringExtra("courseName");

        //Recuperation d'elements pour afficher info du cours pour lequel on cree un evenement
        TextView courseCodeTV= (TextView) findViewById(R.id.coursCode);
        TextView courseNameTV= (TextView) findViewById(R.id.coursNom);

        //set code et nom du cours pour lequel on va creer un evenemet
        courseCodeTV.setText(courseID);
        courseNameTV.setText(courseName);

        Button createEventB= (Button) findViewById(R.id.createEventB);
        createEventB.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //envoyer la requete pour creer l'evenement et retourner a la page d'accueil
        MainActivity.accueil.finish();
        DetailsActivity.details.finish();
        Intent accueil = new Intent(this,MainActivity.class);

        //accueil.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(accueil);
        finish();
    }

}
