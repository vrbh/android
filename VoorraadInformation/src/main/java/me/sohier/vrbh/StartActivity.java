package me.sohier.vrbh;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import me.sohier.vrbh.oauth.OAuthListener;
import me.sohier.vrbh.oauth.OAuthMain;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

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

        OAuthMain login = new OAuthMain(this, "", "");

        Log.e("Starting", " Startup");


        if (!login.isLoggedIn())
        {
            Log.i("vrbh/start", "Not logged in yet, starting login procedure");

            try {
                login.authorize(new OAuthListener() {
                    @Override
                    public void onOAuthComplete() {

                    }

                    @Override
                    public void onOAuthCancel() {

                    }

                    @Override
                    public void onOAuthError(int errorCode, String description, String failingUrl) {

                    }
                });
            } catch (OAuthMessageSignerException e) {
                e.printStackTrace();
            } catch (OAuthNotAuthorizedException e) {
                e.printStackTrace();
            } catch (OAuthExpectationFailedException e) {
                e.printStackTrace();
            } catch (OAuthCommunicationException e) {
                e.printStackTrace();
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
            View rootView = inflater.inflate(R.layout.fragment_start, container, false);
            return rootView;
        }
    }

}
