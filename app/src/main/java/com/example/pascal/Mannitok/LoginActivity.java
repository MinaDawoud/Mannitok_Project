//Correspond à 1

package com.example.pascal.Mannitok;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.pascal.Mannitok.Requetes.RegisterRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username;
    Button enregistrer;
    String pseudo;

    //la mac address qui permet d'identifier le cellulaire
    String address;

    //queue de requêtes de Volley
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //champ du username
        username = (EditText) findViewById(R.id.username);

        //bouton d'enregistrement, avec listener
        enregistrer = (Button) findViewById(R.id.button_enregistrer);
        enregistrer.setOnClickListener(this);

        //on récupère le mac address
        address = UserInformation.getMacAddress();

        queue = Volley.newRequestQueue(LoginActivity.this);
    }

    @Override
    public void onClick(View v) {

        //On va chercher le pseudo entré par l'utilisateur
        pseudo = username.getText().toString();
        //set le username
        UserInformation.setUsername(pseudo);

        //On crée le response listener qui attend la réponse de l'API
        Response.Listener<String> responseListenerRegister = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    JSONObject jsonResponse = new JSONObject(response);

                    //maintenant qu'on a notre réponse en json, on peut analyser la réponse
                    //va chercher le champ success de la réponse json, qui est soit true ou false
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        Toast.makeText(getApplicationContext(), "Bonjour " + username.getText().toString(), Toast.LENGTH_SHORT).show();
                        //Crée un intent qui ouvre un MainActivity, et on dit à LoginActivity de faire cet intent.
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        LoginActivity.this.startActivity(mainIntent);
                        finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("Veuillez choisir un pseudonyme").setNegativeButton("Réessayer", null).create().show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        //Requête pour enregistrer un nouvel utilisateur
        //Toast.makeText(getApplicationContext(), pseudo, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
        RegisterRequest registerRequest = new RegisterRequest(address, pseudo, responseListenerRegister);
        queue.add(registerRequest);


    }
}
