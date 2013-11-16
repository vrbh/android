package me.sohier.vrbh;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.api.client.auth.oauth2.Credential;
import com.wuman.android.auth.OAuthManager;

import java.io.IOException;

import me.sohier.vrbh.internal.API;
import me.sohier.vrbh.internal.APICallbackInterface;
import me.sohier.vrbh.internal.APIClasses.User;
import me.sohier.vrbh.internal.GsonRequest;


public class StartActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();

        }

        StrictMode.enableDefaults();


        Log.e("Starting", " Startup");

        API.setContext(this);
        API.setFragmentManager(this.getSupportFragmentManager());

        final Context ct = this;

        if (API.getQueue() == null)
        {
            throw new RuntimeException("Volley queue is null");
        }

        OAuthManager.OAuthCallback<Credential> callback = new OAuthManager.OAuthCallback<Credential>() {
            @Override
            public void run(OAuthManager.OAuthFuture<Credential> future) {
                try {
                    Credential credential = future.getResult();
                    API.setCreds(credential);

                    Log.d("startAcitivty/oauth", "Got creds!");

                    API.refreshToken(new APICallbackInterface(){

                        @Override
                        public void call() {
                            GsonRequest<User> rq = new GsonRequest<User>(Request.Method.GET, "/api/user/current", User.class, null, new Response.Listener<User>(){

                                @Override
                                public void onResponse(User response) {
                                    Log.d("StartActivity", "Started :D");

                                    Intent detailIntent = new Intent(ct, productListActivity.class);

                                    startActivity(detailIntent);
                                    finish();
                                }
                            }, new Response.ErrorListener(){

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("StartActivity", "Error during request to the server: " + error);
                                }
                            });


                            API.getQueue().add(rq);
                        }
                    });
                } catch (IOException e) {
                    Log.e("Oauth error", "IO error during oauth", e);
                }
            }
        };

        API.getCredentials(callback);


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_start, container, false);
        }
    }
}
