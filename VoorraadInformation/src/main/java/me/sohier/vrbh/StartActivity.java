package me.sohier.vrbh;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.sohier.vrbh.oauth.OAuthConstants;
import me.sohier.vrbh.oauth.OAuthListener;
import me.sohier.vrbh.oauth.OAuthMain;
import oauth.signpost.exception.OAuthException;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();

        }

        StrictMode.enableDefaults();

        OAuthMain login = new OAuthMain(this, OAuthConstants.PUBLIC_KEY, OAuthConstants.SECRET);

        Log.e("Starting", " Startup");


        if (!login.isLoggedIn())
        {
            Log.i("vrbh/start", "Not logged in yet, starting login procedure");

            try {
                login.authorize(new OAuthListener() {
                    @Override
                    public void onOAuthComplete() {

                        Log.e("onOAuthComplete", "OK!");
                    }

                    @Override
                    public void onOAuthCancel() {
                        Log.e("onOAuthCancel", "Cancel!");
                    }

                    @Override
                    public void onOAuthError(int errorCode, String description, String failingUrl) {
                        Log.e("startactivity/onoautherror", errorCode + ": " + description);
                    }
                });
            } catch (OAuthException e) {
                // Something went wrong with the connection to the server.
                // @TODO: Display a nice message to the user instead just closing the app.
                Log.e("startactivity/oauth/error", "Something went wrong with the server?", e);
                this.finish();
                return;
            }
        }

        Intent detailIntent = new Intent(this, productListActivity.class);

        startActivity(detailIntent);
        this.finish();
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
