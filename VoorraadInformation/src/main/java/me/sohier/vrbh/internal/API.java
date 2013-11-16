package me.sohier.vrbh.internal;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.wuman.android.auth.AuthorizationDialogController;
import com.wuman.android.auth.AuthorizationFlow;
import com.wuman.android.auth.DialogFragmentController;
import com.wuman.android.auth.OAuthManager;
import com.google.api.client.json.jackson.JacksonFactory;
import com.wuman.android.auth.oauth2.store.SharedPreferencesCredentialStore;

import java.io.IOException;

import me.sohier.vrbh.internal.APIClasses.User;
import me.sohier.vrbh.productListFragment;

/**
 * Created by paulsohier on 12-11-13.
 */
public class API {
    private static final String CLIENT_ID = "4_5hjjrx830scgo8wwwcg40kwgo0cs88k8g8g4o88s40o8s0wswo";
    private static final String CLIENT_SECRET = "3q8u44bkkwe8ks0wswwk8w0ks0c0kww8440kc8skgoowo8w08k";
    public static final String HOST = "http://192.168.0.131:8000";//"http://10.0.2.2:8000";
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

    public static User getLoggedInUser(APICallbackInterface callback)
    {
        if (creds == null)
        {
            getCredentials(null);
        }

        creds.getAccessToken();


        return null;
    }

    public static void refreshToken(final APICallbackInterface cb)
    {
        new Thread() {
            @Override
            public void run() {
                if (getCredentials(null).getExpiresInSeconds() < 0)
                {
                    Log.d("vrbh/API/refreshToken", "Token expired: " + getCredentials(null).getExpiresInSeconds());
                    try {
                        boolean rs = getCredentials(null).refreshToken();
                        Log.d("vrbh/API/refreshtoken", "Result: " + rs);
                    } catch (IOException e) {
                        Log.e("vrbh/API/refreshToken", "Refresh token failed", e);
                        return;
                    }
                }
                else
                {
                    Log.d("vrbh/API/refreshtoken", "Token not expired: " + getCredentials(null).getExpiresInSeconds());
                }
                cb.call();
            }
        }.start();
    }

    public static Credential getCredentials(OAuthManager.OAuthCallback<Credential> cb) {
        if (creds != null) {
            Log.d("vrbh/oauth", "Got locally saved creds");

            return creds;
        }

        if (manager == null)
        {
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

    public static RequestQueue getQueue()
    {
        if (queue == null)
        {
            if (context == null)
            {
                throw new RuntimeException("Context is null");
            }

            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }
}
