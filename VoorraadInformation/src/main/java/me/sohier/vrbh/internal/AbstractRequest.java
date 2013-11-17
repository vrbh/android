package me.sohier.vrbh.internal;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRequest<T> extends Request<T> {

    private final Map<String, String> headers;
    private final Response.Listener<T> listener;


    private static String makeUrl(String url) {
        url = API.HOST + url;
        url += "?&access_token=";
        url += API.getCredentials(null).getAccessToken();

        Log.d("GsonRequest", "URL: " + url);

        return url;
    }

    public  AbstractRequest(int method, String path, Map<String, String> headers, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, makeUrl(path), errorListener);

        this.headers = headers;
        this.listener = listener;

        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put("Accept", "application/json");
        headers.put("Content-type", "application/json");
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

}
