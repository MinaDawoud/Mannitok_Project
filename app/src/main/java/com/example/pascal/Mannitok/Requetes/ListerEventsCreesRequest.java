package com.example.pascal.Mannitok.Requetes;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.pascal.Mannitok.UserInformation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pascal on 16/04/2016.
 */
public class ListerEventsCreesRequest extends StringRequest {

    private static final String API_URL = "http://www-ens.iro.umontreal.ca/~langevip/studyami/api/index.php";
    private Map<String, String> params;

    public ListerEventsCreesRequest(Response.Listener<String> listener){
        super(Method.POST, API_URL, listener, null);
        params = new HashMap<>();
        params.put("action_post", "lister_event_creer");
        String macAddress = UserInformation.getMacAddress();
        params.put("userID", macAddress + "");

    }

    //Volley va appeler cette fonction pour obtenir les paramètres lorsque la méthode est appelée
    public Map<String, String> getParams() {
        return params;
    }
}
