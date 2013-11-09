package me.sohier.vrbh.oauth;

/**
 * Callback interface for the OAuthMain login process
 *
 * Code based from https://github.com/dzlab/QypeSample/
 */
public interface OAuthListener {
    /**
     * Called when the user is logged
     */
    public void onOAuthComplete();

    /**
     * Called when the user has canceled the login process
     */
    public void onOAuthCancel();

    /**
     * Called when the login process has failed
     *
     * @param errorCode
     * @param description
     * @param failingUrl
     */
    public void onOAuthError(int errorCode, String description, String failingUrl);
}