package stanislavsikorsyi.oauthtest.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;

import java.util.Arrays;

import stanislavsikorsyi.oauthtest.R;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private ConnectionResult mConnectionResult;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity_loginlayout);
        findViewById(R.id.sign_in_button_google).setOnClickListener(this);
        facebookLoginButton = (LoginButton)findViewById(R.id.sign_in_button_facebook);
        facebookLoginButton.setReadPermissions(Arrays.asList("user_friends"));
    }

    @Override
    public void onClick(View v) {
        onClickGoogleSignIn(v);
    }


    //Facebook listeber
    @Override
    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
        super.onSessionStateChange(session, state, exception);
        if (state.isOpened()) {
            Log.i(TAG, "Facebook Logged in...");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (state.isClosed()) {
            Log.i(TAG, "Facebook Logged out...");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        Log.d(TAG, "onConnected");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void refreshWelcomeText() {

    }
}