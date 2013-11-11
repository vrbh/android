package me.sohier.vrbh.oauth;

public class OAuthConstants {

    public static final String LOG_TAG           = "vrbh/oauth/";
    public static final String PREFS_NAME        = "vrbh_oauth_shared_preferences";

    private static final String REQUEST_HOST     = "http://10.0.2.2:8000/app_dev.php";

    public static final String REQUEST_TOKEN_URL = REQUEST_HOST + "/oauth/v2/token";
    public static final String AUTHORIZE_URL     = REQUEST_HOST + "/oauth/v2/auth";
    public static final String ACCESS_TOKEN_URL  = REQUEST_HOST + "/oauth/v2/token";

    public static final String REDIRECT_URI      = "http://vrbh-success";
    public static final String CANCEL_URI        = "http://vrbh-cancel";

    public static final String PUBLIC_KEY        = "1_5t1sq9te3mcc4ck8g84ckc8wscogosg0o0kc4kg480cs04kkk0";
    public static final String SECRET            = "5e2d45a2zboc00gosks0o488ww00040kksgos0kkw48o4g84cs";

    //http://localhost:8000/app_dev.php/oauth/v2/token?client_id=2_3c64l8lw8dmokgokcck4wcos44c4swc8o4gccwgw4ckkwc8880&client_secret=54z8synhp9yg48048o8844gc4cs0o800ooo0ccw4wwg8okkc0oo&grant_type=client_credentials

}