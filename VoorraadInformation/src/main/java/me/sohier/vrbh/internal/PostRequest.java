package me.sohier.vrbh.internal;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PostRequest extends AbstractRequest<String> {

    private final Map<String, String> params;

    public PostRequest(String url, Map<String, String> headers,
                       Response.Listener<String> listener, Response.ErrorListener errorListener, Map<String, String> param)
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
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            if (response.statusCode == 201)
            {
                parsed = "OK";
            }
            else
            {
                throw new Exception("Error during post request");
            }
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected Map<String, String> getParams()
    {
        return params;
    }
}
