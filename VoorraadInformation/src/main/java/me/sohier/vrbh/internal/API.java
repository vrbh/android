package me.sohier.vrbh.internal;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.jackson.JacksonFactory;
import com.wuman.android.auth.AuthorizationDialogController;
import com.wuman.android.auth.AuthorizationFlow;
import com.wuman.android.auth.DialogFragmentController;
import com.wuman.android.auth.OAuthManager;
import com.wuman.android.auth.oauth2.store.SharedPreferencesCredentialStore;

import java.io.IOException;
import java.util.HashMap;

import me.sohier.vrbh.internal.APIClasses.Organisation;
import me.sohier.vrbh.internal.APIClasses.User;


public class API {
    private static final String CLIENT_ID = "1_62xbho1zy4wss8g0wcokwwgs0s8ow4s04cw0gocwc8gwk8wc4s";
    private static final String CLIENT_SECRET = "6188eijypw0804ckg8owgow00ko4c40w888c0w04wows84oook";
    public static final String HOST = "http://dev.voorraad-beheer.info/app_dev.php";//"http://10.0.2.2:8000";
    public static final String AUTHORIZE = "/oauth/v2/auth";
    public static final String REQUEST_TOKEN = "/oauth/v2/token";

    private static OAuthManager manager = null;
    private static FragmentManager fragmentManager = null;
    private static Context context = null;
    private static Credential creds = null;

    private static RequestQueue queue;

    public static void setFragmentManager(FragmentManager fragment) {
        fragmentManager = fragment;
    }

    public static void setContext(Context context) {
        API.context = context;
    }

    public static void setCreds(Credential cr) {
        creds = cr;
    }



    public static void refreshToken(final APICallbackInterface cb) {
        new Thread() {
            @Override
            public void run() {
                if (getCredentials(null).getExpiresInSeconds() < 0) {
                    Log.d("vrbh/API/refreshToken", "Token expired: " + getCredentials(null).getExpiresInSeconds());
                    try {
                        boolean rs = getCredentials(null).refreshToken();
                        Log.d("vrbh/API/refreshtoken", "Result: " + rs);
                    } catch (IOException e) {
                        Log.e("vrbh/API/refreshToken", "Refresh token failed", e);
                        return;
                    }
                } else {
                    Log.d("vrbh/API/refreshtoken", "Token not expired: " + getCredentials(null).getExpiresInSeconds());
                }
                cb.call();
            }
        }.start();
    }

    public static Credential getCredentials(OAuthManager.OAuthCallback<Credential> cb) {
        // Only use locally saved creds in case no callback is used!
        if (creds != null && cb == null) {
            Log.d("vrbh/oauth", "Got locally saved creds");

            return creds;
        }

        if (manager == null) {
            manager = getManager();
        }

        if (manager == null) {
            throw new RuntimeException("Creating oauth manager failed");
        }

        try {
            if (cb == null) {
                creds = manager.authorizeExplicitly("userId", cb, null).getResult();//authorizeExplicitly("userId", null, null).getResult();
            } else {
                manager.authorizeExplicitly("userId", cb, null);
                ;
            }
        } catch (IOException e) {
            Log.e("vrbh/API/OAuthcreds", "Unable to write data", e);
            throw new RuntimeException("Unable to read/write data", e);
        }

        return creds;
    }

    public static OAuthManager getManager() {
        if (manager == null) {

            if (fragmentManager == null) {
                // This should not happen in normal usage cases.
                throw new RuntimeException("Missing fragment manager");
            }

            if (context == null) {
                throw new RuntimeException("Missing context");
            }

            AuthorizationDialogController controller;

            controller = new DialogFragmentController(fragmentManager) {
                @Override
                public boolean isJavascriptEnabledForWebView() {
                    return true;
                }

                @Override
                public String getRedirectUri() throws IOException {
                    return "http://android.local/";
                }
            };


            SharedPreferencesCredentialStore credentialStore =
                    new SharedPreferencesCredentialStore(context,
                            "preferenceFileName", new JacksonFactory());


            Log.d("oauthdebug", "HOST" + HOST + " token " + REQUEST_TOKEN);
            GenericUrl url = new GenericUrl(HOST + REQUEST_TOKEN);

            AuthorizationFlow.Builder builder = new AuthorizationFlow.Builder(
                    BearerToken.authorizationHeaderAccessMethod(),
                    AndroidHttp.newCompatibleTransport(),
                    new JacksonFactory(),
                    url,
                    new ClientParametersAuthentication(CLIENT_ID, CLIENT_SECRET),
                    CLIENT_ID,
                    HOST + AUTHORIZE);
            builder.setCredentialStore(credentialStore);
            //builder.setScopes(Arrays.asList("scope1", "scope2"));

            AuthorizationFlow flow = builder.build();


            manager = new OAuthManager(flow, controller);

        }

        return manager;
    }

    public static RequestQueue getQueue() {
        if (queue == null) {
            if (context == null) {
                throw new RuntimeException("Context is null");
            }

            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }

    public static void getUser(final Response.Listener<User> rs) {

        API.refreshToken(new APICallbackInterface() {

            @Override
            public void call() {
                GsonRequest<User> rq = new GsonRequest<User>(Request.Method.GET, "/api/user/current", User.class, null, rs, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("StartActivity", "Error during request to the server: " + error);

                        throw new RuntimeException();
                    }
                });


                API.getQueue().add(rq);
            }
        });
    }
    public static void registerOrg(String name, final Response.Listener<String> rs) {


        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", name);

        API.refreshToken(new APICallbackInterface() {

            @Override
            public void call() {
                PostRequest rq = new PostRequest("/api/organisation", null, rs, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("StartActivity", "Error during request to the server: " + error);

                        throw new RuntimeException();
                    }
                }, map);


                API.getQueue().add(rq);
            }
        });
    }
}
