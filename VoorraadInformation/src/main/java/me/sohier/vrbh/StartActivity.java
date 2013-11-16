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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.api.client.auth.oauth2.Credential;
import com.wuman.android.auth.OAuthManager;

import java.io.IOException;

import me.sohier.vrbh.internal.API;


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

                    Intent detailIntent = new Intent(ct, productListActivity.class);

                    startActivity(detailIntent);
                    finish();
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
