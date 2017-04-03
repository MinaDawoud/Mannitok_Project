package com.example.pascal.Mannitok.Requetes;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.pascal.Mannitok.UserInformation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pascal on 15/04/2016.
 */
public class EditEventRequest extends StringRequest{



    private static final String API_URL = "http://www-ens.iro.umontreal.ca/~langevip/studyami/api/index.php";
    private Map<String, String> params;

    public EditEventRequest(String titre, String type, String description, String date, String heure, String lieu, String capacite, String eventID, Response.Listener<String> listener){
        super(Method.POST, API_URL, listener, null);
        params = new HashMap<>();
        params.put("action_post", "edit_event");
        params.put("titre", titre + "");
        params.put("type", type + "");
        params.put("description", description + "");
        params.put("date", date + "");
        params.put("heure", heure + "");
        params.put("lieu", lieu + "");
        params.put("capacite", capacite + "");
        params.put("eventID", eventID + "");
        String macAddress = UserInformation.getMacAddress();
        params.put("userID", macAddress + "");

    }

    //Volley va appeler cette fonction pour obtenir les paramètres lorsque la méthode est appelée
    public Map<String, String> getParams() {
        return params;
    }

}
