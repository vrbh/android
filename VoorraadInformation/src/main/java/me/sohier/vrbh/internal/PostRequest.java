package me.sohier.vrbh.internal;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

public class PostRequest<T> extends AbstractRequest<T> {

    private final Map<String, String> params;

    public PostRequest(String url, Class<T> clazz, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener, Map<String, String> param)
    {
        super(Method.POST, url, headers, listener, errorListener);

        if (param == null)
        {
            param = new HashMap<String, String>();
        }

        params = param;
    }

    public void addParam(String name, String value)
    {
        params.put(name, value);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    @Override
    protected Map<String, String> getParams()
    {
        return params;
    }
}
