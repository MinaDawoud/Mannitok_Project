package com.example.pascal.Mannitok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    public final int nbEvents=1;//quantite d'evenements joint à afficher dans le pager
    TabLayout options;
    ViewPager contenuBox;
    TextView pseudo;
    String eventID;//ID du cours ou evenement pour lequel on affiche le details
    int typeDetail;//defini si on va afficher les details d'un cours ou d'un evenement
    boolean own;//true s'il s'agit d'un evenement cree par l'utilisateur

    public static Activity details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        details=this;//pour finir cette activity a partir de n'importe quelle autre activity

        //recuperer le cours ou evenement pour lequel on veut afficher les details
        Intent details=getIntent();
        eventID = details.getStringExtra("eventID");
        typeDetail = details.getIntExtra("typeDetail", 0);
        own = details.getBooleanExtra("ownEvent",false);
        Log.i("NEW Details Activity", "eventID: " + eventID + " typeDetail: " + typeDetail + " own=" + own);

        //Recuperation de Views
        setContentView(R.layout.activity_details);
        contenuBox = (ViewPager) findViewById(R.id.contenuBox);

        //Adapter pour generer le fragments de l'activite
        SetContent adapter=new SetContent(getSupportFragmentManager());

        //Reglage des Views
        pseudo.setText("pseudo");
        contenuBox.setAdapter(adapter);

        /*//inclure si on veut montrer des tabs, ajouter le TabLayout dans le layout "activity_details"
        options = (TabLayout)findViewById(R.id.options);
        options.setupWithViewPager(contenuBox);
        */
    }

    public class SetContent extends FragmentPagerAdapter{

        public SetContent(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            DetailsFragment fragment;
            fragment=new DetailsFragment();

            //definir le type de fragment desire en passant les parametres au constructeur du fragment
            Bundle typeFragment = new Bundle();
            typeFragment.putString("eventID", eventID);//nom du cours ou evenement
            typeFragment.putInt("typeDetail", typeDetail);//details selon le TAB a partir duquel on lance la recherche de details, si TAB0 => details event sino details cours
            typeFragment.putBoolean("ownEvent", own);
            fragment.setArguments(typeFragment);

            return fragment;
        }

        @Override
        public int getCount() {
            return nbEvents;
        }

        @Override
        //Util si on utilise en TabLayout pour le ViewPager, sinon sans effet
        public CharSequence getPageTitle(int position) {
            String titre;
            if(position==0)
                titre="Mes Evenements";
            else if(position==1)
                titre="Recherche";
            else
                titre="Favoris";
            return titre;
        }

    }
}
