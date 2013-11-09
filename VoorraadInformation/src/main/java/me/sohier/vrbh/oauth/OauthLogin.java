package me.sohier.vrbh.oauth;

        import android.annotation.SuppressLint;
        import android.app.Dialog;
        import android.content.Context;
        import android.graphics.Bitmap;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.View;
        import android.view.ViewGroup.LayoutParams;
        import android.view.Window;
        import android.webkit.WebView;
        import android.webkit.WebViewClient;
        import android.widget.ProgressBar;

    import me.sohier.vrbh.R;


/**
 * Dialog to show the login page and catch OAuth URI
 */
public class OAuthLogin extends Dialog {

    private WebView mWebView;
    private OAuthMain mOAuthMain;

    public OAuthLogin(Context context, OAuthMain qype) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_layout);


        LayoutParams params = getWindow().getAttributes();
        params.height = LayoutParams.MATCH_PARENT;
        params.width = LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        mOAuthMain = qype;

        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Log.d("Oauth", "onReceivedError errorCode = " + errorCode + " description = " + description + " failingUrl = " + failingUrl);

                if(errorCode != -2 && (!failingUrl.startsWith(OAuthConstants.REDIRECT_URI) || !failingUrl.startsWith(OAuthConstants.CANCEL_URI))) {
                    mOAuthMain.error(errorCode, description, failingUrl);
                    dismiss();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                System.out.println("onPageStarted() on "+url);
                if(url.startsWith(OAuthConstants.REDIRECT_URI)) {
                    if(mOAuthMain.getAccessToken() == null) {
                        String[] token = mOAuthMain.getVerifier(url);
                        mOAuthMain.getAccessTokenAsync(token[1]);
                    }
                    mWebView.clearCache(true);
                    dismiss();
                } else if(url.startsWith(OAuthConstants.CANCEL_URI)) {
                    //_viadeo.cancel();
                    //mWebView.clearCache(true);
                    dismiss();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

        });

    }

    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //mWebView.clearCache(true);
        }
        return super.onKeyDown(keyCode, event);
    }
}