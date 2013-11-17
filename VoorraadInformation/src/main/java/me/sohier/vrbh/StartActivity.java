package me.sohier.vrbh;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.api.client.auth.oauth2.Credential;
import com.wuman.android.auth.OAuthManager;

import java.io.IOException;

import me.sohier.vrbh.Fragments.CreateOrgFragment;
import me.sohier.vrbh.Fragments.NoOrgsYetFragment;
import me.sohier.vrbh.internal.API;
import me.sohier.vrbh.internal.APIClasses.User;
import me.sohier.vrbh.internal.AlertDialogCallback;


public class StartActivity extends FragmentActivity {

    private boolean paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //if (savedInstanceState == null) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new PlaceholderFragment())
                .commit();

        //}


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            tryStartup();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.paused = true;
    }

    private void tryStartup() {
        StrictMode.enableDefaults();

        Log.e("Starting", " Startup");

        API.setContext(this);
        API.setFragmentManager(this.getSupportFragmentManager());

        API.updateStatus(getString(R.string.starting));

        if (API.getQueue() == null) {
            throw new RuntimeException("Volley queue is null");
        }

        OAuthManager.OAuthCallback<Credential> callback = new OAuthManager.OAuthCallback<Credential>() {
            @Override
            public void run(OAuthManager.OAuthFuture<Credential> future) {
                try {
                    Credential credential = future.getResult();
                    API.setCreds(credential);

                    Log.d("startAcitivty/oauth", "Got creds!");
                    API.updateStatus(getString(R.string.logged_in));

                    API.getUser(new Response.Listener<User>() {

                        @Override
                        public void onResponse(User response) {
                            Log.d("StartActivity", "Started :D");

                            Log.d("vrbh/start/user", "got a user: " + response.user.username);

                            API.updateStatus(getString(R.string.received_userdata));

                            if (response.user.orgs.length == 0) {
                                API.updateStatus(getString(R.string.no_orgs_yet));
                                noOrgsYet();
                            } else if (response.user.orgs.length == 1) {

                                API.updateStatus(getString(R.string.one_org_starting));

                                Intent detailIntent = new Intent(getApplicationContext(), productListActivity.class);

                                detailIntent.putExtra("user", response);
                                detailIntent.putExtra("org", response.user.orgs[0].id);

                                startActivity(detailIntent);
                                finish();
                            } else {
                                // User has a lot of orgs...
                                API.updateStatus(getString(R.string.more_orgs_choose));

                            }
                        }
                    });

                } catch (IOException e) {
                    Log.e("Oauth error", "IO error during oauth", e);
                }
            }
        };

        API.getCredentials(callback);
    }

    private void registerOrg() {

        final Response.Listener<String> rs = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("vrbh/registerOrg/registerdone", "Register is done. Orgname: " + response);
            }
        };
        CreateOrgFragment org = CreateOrgFragment.newInstance(true, rs);
        org.show(getSupportFragmentManager(), "create_org_dialog");
    }

    private void noOrgsYet() {
        NoOrgsYetFragment newFragment = NoOrgsYetFragment.newInstance(
                R.string.no_org, new AlertDialogCallback() {

            @Override
            public void doNegativeClick() {
                Log.d("vrbh/start", "User cancelled.");
                finish();
            }

            @Override
            public void doJoinClick() {

            }

            @Override
            public void doCreateClick() {
                registerOrg();
            }
        });
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_start, container, false);

            API.setStatus((TextView) v.findViewById(R.id.currentStatus));

            tryStartup();

            return v;
        }
    }
}
