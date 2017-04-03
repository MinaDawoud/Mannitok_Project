package com.example.pascal.Mannitok.Requetes;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by Pascal on 14/04/2016.
 */
public class ListerCoursRequest extends StringRequest {
    private static final String API_URL = "http://www-ens.iro.umontreal.ca/~langevip/studyami/api/index.php?action=lister_cours";

    public ListerCoursRequest(Response.Listener<String> listener){
        super(Method.GET, API_URL, listener, null);
    }
}
