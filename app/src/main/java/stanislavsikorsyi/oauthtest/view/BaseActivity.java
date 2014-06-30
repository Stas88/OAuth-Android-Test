package stanislavsikorsyi.oauthtest.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.common.api.Status;


/**
 * Created by stanislavsikorsyi on 30.06.14.
 */

public abstract class BaseActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private boolean mResolvingError = false;
    private static final String DIALOG_ERROR = "dialog_error";
    private UiLifecycleHelper uiHelper;
    protected LoginButton facebookLoginButton;


    private ProgressDialog mConnectionProgressDialog;
    protected GoogleApiClient googleApiClient;
    private ConnectionResult mConnectionResult;
    private static final String TAG = "BaseActivity";

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.maintestactivity_layout);
        //findViewById(R.id.sign_in_button_google).setOnClickListener(this);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);


        Plus.PlusOptions options = new Plus.PlusOptions.Builder()
                .addActivityTypes("http://schemas.google.com/AddActivity",
                        "http://schemas.google.com/ReviewActivity")
                .build();


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API, options)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        // Если ошибку соединения не удастся разрешить, будет отображаться индикатор выполнения.
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");

    }

    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Facebook Logged in...");
        } else if (state.isClosed()) {
            Log.i(TAG, "Facebook Logged out...");
        }
    }

    public void onClickGoogleSignIn(View view) {
        Log.d(TAG, "onClickGoogleSignIn");
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (mConnectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                //mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            //showErrorDialog(mConnectionResult.getErrorCode());
            mResolvingError = true;
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG,"onConnectionFailed");
        mConnectionResult = result;
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        Log.d(TAG,"onActivityResult");
        switch (requestCode) {
            case REQUEST_CODE_RESOLVE_ERR:
                mResolvingError = false;
                if (responseCode == RESULT_OK) {
                    googleApiClient.connect();
                }
                break;
        }

        uiHelper.onActivityResult(requestCode, responseCode, intent);


    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG,"onConnected");
        // Все ошибки подключения устранены.
        mConnectionProgressDialog.dismiss();
    }


    public void onClickGoogleSignOut(View view) {
        Log.d(TAG,"onClickGoogleSignOut");
        if(googleApiClient.isConnected()) {
            Log.d(TAG, "onClickGoogleSignOut 1");
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            Plus.AccountApi
                    .revokeAccessAndDisconnect(googleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Log.d(TAG, "Disconnected after click");
                            refreshWelcomeText();
                        }
                    });
            googleApiClient.reconnect();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"onConnectionSuspended");

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    abstract void refreshWelcomeText();


//    @Override
//    public void onClick(View v) {
//        onClickGoogleSignIn(v);
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}

