package me.sohier.vrbh.oauth;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * Code based from https://github.com/dzlab/QypeSample/
 */
public class OAuthMain {

    public static final String OAUTH_TOKEN = "oauth_token";
    public static final String OAUTH_VERIFIER = "oauth_verifier";

    public static final String ACCESS_TOKEN = "access_token";
    public static final String SECRET_TOKEN = "secret_token";

    private Context mContext;
    private OAuthConsumer mConsumer;
    private OAuthProvider mProvider;
    private String mCallbackUrl;

    private SharedPreferences mSettings;
    private OAuthListener mListener;

    public OAuthMain(Context context, String consumerKey, String consumerSecret) {
        Log.i("oauth", "Starting oauth main class");

        mContext = context;
        mConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
        mProvider = new CommonsHttpOAuthProvider(OAuthConstants.REQUEST_TOKEN_URL, OAuthConstants.ACCESS_TOKEN_URL, OAuthConstants.AUTHORIZE_URL);
        mProvider.setOAuth10a(true);
        mCallbackUrl = OAuthConstants.REDIRECT_URI;

        mSettings = context.getSharedPreferences(OAuthConstants.PREFS_NAME, 0);
    }

    public void authorize(OAuthListener listener)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {

        mListener = listener;

        OAuthLogin dl = new OAuthLogin(mContext, this);
        dl.loadUrl(getRequestToken());
        dl.show();

    }

    /**
     * Indicate if the user is already logged in to Qype through the application
     *
     * @return true if already logged in
     */
    public boolean isLoggedIn() {
        if (getAccessToken() != null)
            return true;
        return false;
    }

    /**
     * Disconnect the user from Qype, delete his access token from the shared preferences and delete cookies
     *
     * @return true if access token is correctly removed
     */
    public boolean logOut() {
        CookieSyncManager.createInstance(mContext);
        CookieManager.getInstance().removeAllCookie();
        return mSettings.edit().remove(ACCESS_TOKEN).commit() && mSettings.edit().remove(SECRET_TOKEN).commit();
    }

    /**
     * retrieve the request token
     */
    public String getRequestToken()
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException {
        String authUrl = mProvider.retrieveRequestToken(mConsumer, mCallbackUrl);
        return authUrl;
    }

    /**
     * retrieve the verifier from the callback URL
     */
    public String[] getVerifier(String uriString) {
        return getVerifier(Uri.parse(uriString));
    }

    public String[] getVerifier(Uri uri) {
        // extract the token if it exists
        if (uri == null) {
            return null;
        }
        String token = uri.getQueryParameter(OAUTH_TOKEN);
        String verifier = uri.getQueryParameter(OAUTH_VERIFIER);
        System.out.println(uri + " - token: " + token + ", verifier: " + verifier);
        return new String[]{token, verifier};
    }

    /**
     * Get the user access token to the Qype API saved in the shared preferences
     *
     * @return the user access token
     */
    public String[] getAccessToken() {
        String access_token = mSettings.getString(ACCESS_TOKEN, null);
        String secret_token = mSettings.getString(SECRET_TOKEN, null);
        if (access_token == null || secret_token == null)
            return null;
        return new String[]{access_token, secret_token};
    }

    public void getAccessTokenAsync(final String verifier) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean success = false;
                try {
                    mProvider.retrieveAccessToken(mConsumer, verifier);
                    mSettings.edit().putString(ACCESS_TOKEN, mConsumer.getToken()).commit();
                    mSettings.edit().putString(SECRET_TOKEN, mConsumer.getTokenSecret()).commit();
                    System.out.println("token: " + mConsumer.getToken() + ", secret: " + mConsumer.getTokenSecret());
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    mListener.onOAuthComplete();
                } else {
                    mListener.onOAuthError(-1, "failed", OAuthConstants.ACCESS_TOKEN_URL);
                }
            }
        }.execute();
    }

    /**
     * retrieve the access token and its secret
     */
    public String[] getAccessToken(String verifier)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException {
        mProvider.retrieveAccessToken(mConsumer, verifier);
        mSettings.edit().putString(ACCESS_TOKEN, mConsumer.getToken()).commit();
        mSettings.edit().putString(SECRET_TOKEN, mConsumer.getTokenSecret()).commit();
        return new String[]{mConsumer.getToken(), mConsumer.getTokenSecret()};
    }

    public String request(String url) {
        String content = null;
        try {
            String accessToken[] = getAccessToken();
            mConsumer.setTokenWithSecret(accessToken[0], accessToken[1]);
            HttpGet request = new HttpGet(url);
            // sign the request
            mConsumer.sign(request);
            // send the request
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            content = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    protected void error(int errorCode, String description, String failingUrl) {
        mListener.onOAuthError(errorCode, description, failingUrl);
    }
}