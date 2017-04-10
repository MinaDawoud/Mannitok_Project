package com.example.pascal.Mannitok.Requetes;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by Pascal on 14/04/2016.
 */
public class ListerEntreprisesRequest extends StringRequest {
    private static final String API_URL = "http://sevis.online/mannitok/secure/api.php?action_get=lister_entreprise";

    public ListerEntreprisesRequest(Response.Listener<String> listener){
        super(Method.GET, API_URL, listener, null);
    }
}
