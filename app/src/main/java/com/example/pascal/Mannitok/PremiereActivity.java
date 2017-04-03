package com.example.pascal.Mannitok;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.pascal.Mannitok.Requetes.ListerFavorisRequest;
import com.example.pascal.Mannitok.Requetes.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class PremiereActivity extends AppCompatActivity {
    TextView studyami_text;

    //la mac address qui permet d'identifier le cellulaire
    String address;

    //queue de requêtes de Volley
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premiere);

        studyami_text = (TextView) findViewById(R.id.studyami_text);

        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        address = info.getMacAddress();
        //set le macaddress
        UserInformation.setMacAddress(address + "");

        queue = Volley.newRequestQueue(PremiereActivity.this);

        /*
        On initialise les favoris de l'utilisateur
         */
        UserInformation.createFavoris();
        //On remplie le ListView (liste) avec les éléments de la réponse du json

        Response.Listener<String> favorisListener = new Response.Listener<String>() {
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
                            UserInformation.addFavori(row.getString("code"));
                        } else {
                            Toast.makeText(PremiereActivity.this, "marche pas", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //On crée la requête
        ListerFavorisRequest listerFavoris = new ListerFavorisRequest(favorisListener);
        queue.add(listerFavoris);


        //On crée le requête pour le login. Si l'utlisateur est déjà dans la base de donnée, on saute directement à l'accueil (MainActivity) sans créer un nouvel utilisateur
        //Sinon on va à la LoginActivity pour qu'il enregistre un utilisateur.
        Response.Listener<String> responseListenerLogin = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    //Si success = false, alors on ne fait rien (donc on reste à la création de compte)
                    //Si success = true, alors on passe tout de suite à la page d'accueil.
                    if(success){
                        //on set le username
                        String pseudo = jsonResponse.getString("pseudo");
                        UserInformation.setUsername(pseudo);


                        //passe au MainActivity
                        Intent mainIntent = new Intent(PremiereActivity.this, MainActivity.class);
                        PremiereActivity.this.startActivity(mainIntent);
                        finish();
                    } else {
                        //passe au LoginActivity
                        Intent loginIntent = new Intent(PremiereActivity.this, LoginActivity.class);
                        PremiereActivity.this.startActivity(loginIntent);
                        finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        //Requête pour login un utilisateur
        LoginRequest loginRequest = new LoginRequest(address, responseListenerLogin);
        queue.add(loginRequest);
    }


}
